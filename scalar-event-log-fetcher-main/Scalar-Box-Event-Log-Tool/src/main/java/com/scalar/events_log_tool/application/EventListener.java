package com.scalar.events_log_tool.application;

import com.box.sdk.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.EventType;
import com.scalar.events_log_tool.application.constant.ItemType;
import com.scalar.events_log_tool.application.constant.TamperingStatusType;
import com.scalar.events_log_tool.application.dto.*;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.*;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.service.AssetService;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Abhishek
 */
@Slf4j
@Component
@Profile("!test")
public class EventListener {

    private final DistributedTransactionManager transactionManager;
    private final EventsRepository eventsRepository;
    private final PositionTrackerRepository positionTrackerRepository;
    private final ItemEventsRepository itemEventsRepository;
    private final ItemsBySha1Repository itemsBySha1Repository;
    private final AssetService assetService;
    private final BoxUtility boxUtility;
    private final ItemStatusRepository itemStatusRepository;
    private final ObjectMapper objectMapper;
    private final AuditSetItemRepository auditSetItemRepository;
    private EventStream stream;
    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection connection;


    @Autowired
    public EventListener(DistributedTransactionManager transactionManager,
                         EventsRepository eventsRepository,
                         PositionTrackerRepository positionTrackerRepository, ItemEventsRepository itemEventsRepository,
                         ItemsBySha1Repository itemsBySha1Repository, AssetService assetService,
                         EventStream stream, BoxUtility boxUtility, ItemStatusRepository itemStatusRepository, ObjectMapper objectMapper, AuditSetItemRepository auditSetItemRepository) {
        this.transactionManager = transactionManager;
        this.eventsRepository = eventsRepository;
        this.positionTrackerRepository = positionTrackerRepository;
        this.itemEventsRepository = itemEventsRepository;
        this.itemsBySha1Repository = itemsBySha1Repository;
        this.assetService = assetService;
        this.boxUtility = boxUtility;
        this.stream = stream;
        this.itemStatusRepository = itemStatusRepository;
        this.objectMapper = objectMapper;
        this.auditSetItemRepository = auditSetItemRepository;
    }


    @Scheduled(fixedRate = 600000) // Run every 10 minutes
    public void run() {
        log.info("-----------Started---------- ");

        runEventListener();
    }


    public void runEventListener() {


        boolean needsRefresh = connection.needsRefresh();
        log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

        if (needsRefresh) {
            connection = boxUtility.getBoxEnterpriseConnection();
            log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
        }


        // "COLLAB_INVITE_COLLABORATOR"

        List<String> eventsToCapture = Arrays.stream(EventType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        log.info("events:" + eventsToCapture);

        if (stream != null && stream.isStarted()) {
            log.info("Closing the existing stream...");
            stream.stop();
        }

        DistributedTransaction newTransaction = getNewTransaction();

        PositionTracker position = positionTrackerRepository.getPositionByUserId(newTransaction, 1111111111L);

        if (position == null) {
            stream = new EventStream(connection);
            log.info("started listener from  current date, no  previous record found ...");
        } else {
            log.info("listener Resuming from last Position...");
            stream = new EventStream(connection, Long.parseLong(position.getPosition()));
        }
        commitTransaction(newTransaction);


        stream.addListener(new com.box.sdk.EventListener() {
            @Override
            public void onEvent(BoxEvent event) {

                log.info("--------------------------User Event Details---------------------------");
                log.info("Event Id: {} ", event.getID());
                log.info("User Name: {}", event.getCreatedBy().getName());
                log.info("Login: {}", event.getCreatedBy().getLogin());
                log.info("Event Type: {}", event.getEventType());
                log.info("Created at: {}", event.getCreatedAt());
                log.info("Source JSON: {}", event.getSourceJSON());
                // get only that event...
                if (eventsToCapture.contains(event.getEventType().toString())) {

                    DistributedTransaction transaction = getNewTransaction();

                    Date createdAt = event.getCreatedAt();

                    // Create a SimpleDateFormat object for UTC with the format
                    SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
                    utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    SimpleDateFormat utcDateFormatter = GenericUtility.getUTCDateFormatWithoutMilliseconds();


                    ItemData itemData = null;
                    String yyyymmdd = null;
                    String timeStamp = null;
                    Long userId = null;
                    String eventId = null;

                    CustomEventData customEventData = null;
                    try {

                        // Format the date in UTC
                        String formattedUtcDate = utcFormat.format(createdAt);
                        String[] split = formattedUtcDate.split("-");
                        String withoutMillis = split[1].substring(0, 6);
                        Long ymdhmsDate = Long.parseLong(split[0] + withoutMillis);

                        eventId = event.getID();
                        yyyymmdd = split[0];
                        timeStamp = split[1];

                        userId = Long.parseLong(event.getCreatedBy().getID());

                        log.info("YYYY-MM-DD:" + yyyymmdd + " Timestamp:" + timeStamp + " UserId:" + userId + " eventID:" + event.getID());
                        Events eventById = eventsRepository.getEventByPTAndCK(transaction, yyyymmdd, timeStamp, userId, event.getID());
                        if (eventById == null) {
                            itemData = parseStringJsonToObject(event.getSourceJSON().toString());
                            if (itemData.getType().equalsIgnoreCase(ItemType.FILE.toString())) {

                                log.info("ItemData: {}", itemData);

                                Events eventObject = Events.builder()
                                        .eventId(eventId)
                                        .yyyyMMdd(yyyymmdd)
                                        .timestamp(timeStamp)
                                        .sourceJson(event.getSourceJSON().toString())
                                        .sha1Hash(ItemType.FILE.toString().equalsIgnoreCase(itemData.getType()) ? itemData.getSha1() : null)
                                        .itemId(Long.parseLong(itemData.getId()))
                                        .itemVersionId(ItemType.FILE.toString().equalsIgnoreCase(itemData.getType()) ? Long.parseLong(itemData.getFileVersion().getId()) : 0L)
                                        .createdAt(ymdhmsDate)
                                        .userId(userId)
                                        .userName(event.getCreatedBy().getName())
                                        .userEmail(event.getCreatedBy().getLogin())
                                        .assetAge(null)
                                        .assetId(null)
                                        .eventOccuredOn("ITEM")
                                        .eventType(event.getEventType().toString())
                                        // GenericUtility.getParentFolder(itemData.getPathCollection())  getting issue with Item_trash
                                        .parentFolderId(0L)
                                        .build();

                                eventsRepository.create(eventObject, transaction);


                                customEventData = CustomEventData.builder()
                                        .type(itemData.getType())
                                        .id(itemData.getId())
                                        .createdByUser(itemData.getCreatedBy())
                                        .modifiedByUser(itemData.getModifiedBy())
                                        .eventId(String.valueOf(eventId))
                                        .name(itemData.getName())
                                        .description(itemData.getDescription())
                                        .itemVersionId(ItemType.FILE.toString().equalsIgnoreCase(itemData.getType()) ? Long.parseLong(itemData.getFileVersion().getId()) : 0L)
                                        .sha1(ItemType.FILE.toString().equalsIgnoreCase(itemData.getType()) ? itemData.getSha1() : null)
                                        .size(itemData.getSize())
                                        .itemCreatedAtDate(utcDateFormatter.format(itemData.getCreatedAt()))
                                        .itemModifiedAtDate(utcDateFormatter.format(itemData.getModifiedAt()))
                                        .eventCreatedUserId(Long.parseLong(event.getCreatedBy().getID()))
                                        .eventCreatedUserEmail(event.getCreatedBy().getLogin())
                                        .eventCreatedUserName(event.getCreatedBy().getName())
                                        .build();


                                ItemEvents itemEvent = itemEventsRepository.getItemEvent(Long.parseLong(itemData.getId()), ymdhmsDate, ItemType.FILE.toString().equalsIgnoreCase(itemData.getType()) ? Long.parseLong(itemData.getFileVersion().getId()) : 0L, transaction);
                                if (itemEvent == null) {
                                    ItemEvents itemEvents = ItemEvents.builder()
                                            .itemId(Long.parseLong(itemData.getId()))
                                            .itemVersionId(ItemType.FILE.toString().equalsIgnoreCase(itemData.getType()) ? Long.parseLong(itemData.getFileVersion().getId()) : 0L)
                                            .eventType(event.getEventType().toString())
                                            .eventDate(ymdhmsDate)
                                            .eventId(String.valueOf(eventId))
                                            .eventJsonData(parseObjectToString(customEventData))
                                            .build();

                                    itemEventsRepository.create(itemEvents, transaction);
                                }
                                log.info("Item events:{}", itemEvent);

                                // if item type is equal to file, add data to ItemsBySha1
                                if (ItemType.FILE.toString().equalsIgnoreCase(itemData.getType())) {

                                    ItemsBySha1 sha1Item = itemsBySha1Repository.getItemsBySha1(itemData.getSha1(), Long.parseLong(itemData.getId()), Long.parseLong(itemData.getFileVersion().getId()), transaction);
                                    if (sha1Item == null) {
                                        ItemsBySha1 itemsBySha1 = ItemsBySha1.builder()
                                                .itemName(itemData.getName())
                                                .itemId(Long.parseLong(itemData.getId()))
                                                .itemVersionNumber(0)  //build logic later..
                                                .itemVersionId(Long.parseLong(itemData.getFileVersion().getId()))
                                                .sha1Hash(itemData.getSha1())
                                                .createdAt(Long.parseLong(utcDateFormatter.format(itemData.getCreatedAt())))
                                                .ownerByJson(GenericUtility.convertObjectToStringJson(itemData.getOwnedBy()))
                                                .path(GenericUtility.getExactPath(itemData.getPathCollection()))
                                                .build();
                                        itemsBySha1Repository.create(itemsBySha1, transaction);
                                    } else {
                                        // if item was trash before then, item got restore update the path in db.
                                        sha1Item.setPath(GenericUtility.getExactPath(itemData.getPathCollection()));
                                        itemsBySha1Repository.create(sha1Item, transaction);
                                    }

                                }

                                //new process
                                if (event.getEventType().toString().equalsIgnoreCase(EventType.ITEM_UPLOAD.toString()) ||
                                        event.getEventType().toString().equalsIgnoreCase(EventType.ITEM_COPY.toString())) {

                                    List<PathEntry> parents = new ArrayList<>(itemData.getPathCollection().getEntries());

                                    if (itemData.getPathCollection().getTotalCount() >= 2) {

                                        parents.stream().skip(1).forEach(item -> {
                                            Long itemId = Long.parseLong(item.getId());
                                            ItemStatus itemStatus = itemStatusRepository.get(itemId, transaction);
                                            if (itemStatus != null) {

                                                List<String> existingAuditSet = new ArrayList<>();
                                                try {
                                                    existingAuditSet = objectMapper.readValue(itemStatus.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                                                    });
                                                } catch (JsonProcessingException e) {
                                                    e.printStackTrace();
                                                    //handle later..
                                                }

                                                existingAuditSet.forEach(auditSetId -> {
                                                    AuditSetItem auditSetItemObject = auditSetItemRepository.get(auditSetId, itemId, transaction);
                                                    if (auditSetItemObject != null) {

                                                        PathEntry pathEntry = parents.get(parents.size() - 1);

                                                        //---

                                                        List<BasicItemInfo> basicItemInfos;

                                                        try {
                                                            basicItemInfos = objectMapper.readValue(auditSetItemObject.getListJson(), new TypeReference<List<BasicItemInfo>>() {
                                                            });
                                                        } catch (JsonProcessingException e) {
                                                            throw new GenericException("Error converting JSON to Object");
                                                        }

                                                        List<Long> itemIds = basicItemInfos.stream()
                                                                .map(BasicItemInfo::getId)
                                                                .collect(Collectors.toList());

                                                        BoxFolder folder = new BoxFolder(connection, pathEntry.getId());
                                                        SimpleDateFormat dateFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();

                                                        Date createdAtDate;
                                                        try {
                                                            createdAtDate = dateFormat.parse(String.valueOf(auditSetItemObject.getCreatedAt()));
                                                        } catch (ParseException e) {
                                                            throw new GenericException("Error parsing created date");
                                                        }

                                                        List<BoxItem.Info> deniedList = new ArrayList<>();
                                                        List<BoxItem.Info> allowedList = new ArrayList<>();

                                                        for (BoxItem.Info itemInfo : folder) {
                                                            if (itemInfo instanceof BoxFile.Info) {

                                                                boolean toShow = itemIds.contains(Long.parseLong(itemInfo.getID()));
                                                                BoxFile fileObject = new BoxFile(connection, itemInfo.getID());
                                                                BoxFile.Info fileInfo = fileObject.getInfo();
                                                                if (!toShow) {
                                                                    System.out.println("file Name:" + fileInfo.getName() + " file created at date : " + fileInfo.getCreatedAt() + "AuditSet Date:" + createdAtDate);

                                                                    if (fileInfo.getCreatedAt().before(createdAtDate)) {
                                                                        //file was created before creating audit set and was denied
                                                                        deniedList.add(fileInfo);
                                                                    } else {
                                                                        //need to check if any items are in deniedList, if not denied, that means folder was in all allowed
                                                                        //so this item should be allowed
                                                                        //update the  allowed list in db

                                                                        allowedList.add(fileInfo);

                                                                    }
                                                                }
                                                            } else if (itemInfo instanceof BoxFolder.Info) {
                                                                boolean toShow = itemIds.contains(Long.parseLong(itemInfo.getID()));
                                                                BoxFolder folderObject = new BoxFolder(connection, itemInfo.getID());
                                                                BoxFolder.Info folderInfo = folderObject.getInfo();
                                                                if (!toShow) {
                                                                    if (folderInfo.getCreatedAt().before(createdAtDate)) {
                                                                        //file was created before creating audit set and was denied
                                                                        deniedList.add(folderInfo);
                                                                    } else {
                                                                        //need to check if any items are in deniedList, if not denied, that means folder was in all allowed
                                                                        //so this item should be allowed
                                                                        //update the  allowed list in db

                                                                        allowedList.add(folderInfo);

                                                                    }
                                                                }
                                                            } else {
                                                                log.info("Invalid type");
                                                            }
                                                        }
                                                        if (deniedList.isEmpty() && !allowedList.isEmpty()) {
                                                            //need to check if any items are in deniedList, if not denied, that means folder was in all allowed
                                                            //so this item should be allowed
                                                            //update the  allowed list in db

                                                            for (BoxItem.Info itemInfo : allowedList) {

                                                                Long itemID = Long.parseLong(itemInfo.getID());
                                                                basicItemInfos.add(BasicItemInfo.builder()
                                                                        .id(itemID)
                                                                        .type(itemInfo.getType())
                                                                        .build());

                                                                ItemStatus itemStatusObject = itemStatusRepository.get(itemID, transaction);

                                                                // if file is added then set status monitored ..
                                                                if (itemStatusObject == null) {
                                                                    String date = GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date());
                                                                    ItemStatus newItemStatusObject = ItemStatus.builder()
                                                                            .itemId(itemID)
                                                                            .status(TamperingStatusType.NOT_TAMPERED.toString())
                                                                            .lastValidatedAt(date)
                                                                            .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                                                                            .monitoredStatus(TamperingStatusType.MONITORED.toString())
                                                                            .itemType(itemInfo.getType())
                                                                            .build();
                                                                    itemStatusRepository.create(newItemStatusObject, transaction);
                                                                } else {

                                                                    List<String> existingAuditSetList = new ArrayList<>();
                                                                    try {
                                                                        existingAuditSetList = objectMapper.readValue(itemStatusObject.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                                                                        });
                                                                    } catch (JsonProcessingException e) {
                                                                        e.printStackTrace();
                                                                        //handle later..
                                                                    }

                                                                    existingAuditSetList.add(auditSetId);
                                                                    itemStatusObject.setMonitoredStatus(TamperingStatusType.MONITORED.toString());
                                                                    itemStatusObject.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSetList));
                                                                    itemStatusObject.setItemType(itemInfo.getType());
                                                                    itemStatusRepository.create(itemStatusObject, transaction);


                                                                }


                                                                log.info("Item :" + Long.parseLong(itemInfo.getID()));
                                                            }

                                                            //since new allowed file found update the List Json
                                                            auditSetItemObject.setListJson(GenericUtility.convertObjectToStringJson(basicItemInfos));

                                                            auditSetItemRepository.create(auditSetItemObject, transaction);
                                                            //---


                                                        }

                                                    }
                                                });
                                            }

                                        });
                                    }

                                }

                            }
                        }

                        commitTransaction(transaction);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Start DL execution after committing transaction
                    if (itemData != null && itemData.getType().equalsIgnoreCase(ItemType.FILE.toString())) {
                        log.info("CustomData: {}", customEventData);
                        ApiResponse response = assetService.addAsset(customEventData);
                        log.info("Response From DL: {}", response);

                    }
                }

            }


            @Override
            public void onNextPosition(long position) {
                log.info("Position {}", position);

                DistributedTransaction transaction = getNewTransaction();
                PositionTracker positionByUserId = positionTrackerRepository.getPositionByUserId(transaction, 1111111111L);

                String positionInString = String.valueOf(position);
                if (positionByUserId == null || !positionByUserId.getPosition().equals(positionInString)) {
                    if (positionByUserId == null) {
                        log.info("New Position Arrived ..");
                        positionByUserId = PositionTracker.builder()
                                .userId(1111111111L)
                                .position(positionInString)
                                .build();
                    } else {
                        positionByUserId.setPosition(positionInString);
                        log.info("Updated Position Tracker Object {}", positionByUserId);
                    }

                    positionTrackerRepository.create(positionByUserId, transaction);
                    commitTransaction(transaction);
                }
            }


            @Override
            public boolean onException(Throwable e) {
                e.printStackTrace();
                return true;
            }
        });
        stream.start();
        log.info("Event Listener Started...");
        log.info("Event Status:" + stream.isStarted());

    }


    public String parseObjectToString(CustomEventData customEventData) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(customEventData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went Wrong with parsing Json");
        }

    }


    public ItemData parseStringJsonToObject(String jsonString) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonString, ItemData.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went Wrong with parsing Json");
        }

    }


    public DistributedTransaction getNewTransaction() {
        try {
            log.info("Starting transaction...");
            return transactionManager.start();
        } catch (TransactionException te) {
            te.printStackTrace();
            throw new RuntimeException("Unable to Start Transaction..");
        }
    }

    public void commitTransaction(DistributedTransaction transaction) {

        try {
            log.info("Committing transaction...");
            transaction.commit();
        } catch (TransactionException te) {
            log.info("Error While committing transaction");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException("Unable to Commit Transaction..");

        }
    }


    public JsonDataAges getParseAges(String jsonString) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString
                    , JsonDataAges.class);
        } catch (JsonProcessingException e) {
            log.info("Failed at parsing the data :{}", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}