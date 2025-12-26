package com.scalar.events_log_tool.application.service;

import com.box.sdk.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.events_log_tool.application.constant.AccessStatus;
import com.scalar.events_log_tool.application.constant.ItemType;
import com.scalar.events_log_tool.application.constant.TamperingStatusType;
import com.scalar.events_log_tool.application.dto.AddItem;
import com.scalar.events_log_tool.application.dto.BasicItemInfo;
import com.scalar.events_log_tool.application.dto.UpdateAuditSetItem;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.*;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.AuditSetItemsVisibility;
import com.scalar.events_log_tool.application.responsedto.AuditSetLists;
import com.scalar.events_log_tool.application.responsedto.VerifyItemStatus;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuditSetItemService {
    private final AuditSetRepository auditSetRepository;
    private final UserRepository userRepository;
    private final AuditSetItemRepository auditSetItemRepository;
    private final ObjectMapper objectMapper;
    private final AssetService assetService;
    private final ItemStatusRepository itemStatusRepository;
    private final AuditSetCollaboratorsRepository auditSetCollaboratorsRepository;
    private final String boxServiceAccountEmail;
    private final BoxUtility boxUtility;
    private final AuditSetService auditSetService;

    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection connection;

    public AuditSetItemService(AuditSetRepository auditSetRepository, UserRepository userRepository, AuditSetItemRepository auditSetItemRepository, ObjectMapper objectMapper, AssetService assetService, ItemStatusRepository itemStatusRepository, AuditSetCollaboratorsRepository auditSetCollaboratorsRepository, @Value("${box.server-authentication.service-acc}") String boxServiceAccountEmail, BoxUtility boxUtility, AuditSetService auditSetService) {
        this.auditSetRepository = auditSetRepository;
        this.userRepository = userRepository;
        this.auditSetItemRepository = auditSetItemRepository;
        this.objectMapper = objectMapper;
        this.assetService = assetService;
        this.itemStatusRepository = itemStatusRepository;
        this.auditSetCollaboratorsRepository = auditSetCollaboratorsRepository;
        this.boxServiceAccountEmail = boxServiceAccountEmail;
        this.boxUtility = boxUtility;
        this.auditSetService = auditSetService;
    }

    public ApiResponse addItemToAuditSet(String auditSetId, AddItem addItem, String currentUser, DistributedTransaction transaction, BoxAPIConnection boxAPIConnection) {

        AuditSet auditSet = auditSetRepository.get(auditSetId, transaction);
        if (auditSet == null) {
            throw new GenericException(Translator.toLocale("com.auditSet.notFound"));
        }

        User byUserEmail = userRepository.getByUserEmail(currentUser, transaction);

        // Check if the item is already present in the AuditSet
        AuditSetItem checkItem = auditSetItemRepository.get(auditSet.getAuditSetId(), addItem.getItemId(), transaction);

        SimpleDateFormat dateFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();
        if (checkItem == null) {

            if (addItem.getItemType().equalsIgnoreCase(ItemType.FOLDER.toString())) {


                BoxFolder boxFolder = new BoxFolder(boxAPIConnection, String.valueOf(addItem.getItemId()));
                BoxFolder.Info info = boxFolder.getInfo("modified_at");

                AuditSetItem auditSetItem = AuditSetItem.builder()
                        .auditSetId(auditSet.getAuditSetId())
                        .itemId(addItem.getItemId())
                        .itemName(addItem.getItemName())
                        .itemType(addItem.getItemType())
                        .accessList("ALLOW")
                        .listJson(GenericUtility.convertObjectToStringJson(addItem.getItems()))
                        .createdAt(Long.parseLong(dateFormat.format(info.getModifiedAt())))
                        .assignedByUserId(byUserEmail.getId())
                        .build();

                auditSetItemRepository.create(auditSetItem, transaction);

                // for each item set status monitored ..
                addToMonitoredStatus(addItem.getItems(), auditSet.getAuditSetId(), transaction);
            } else if (addItem.getItemType().equalsIgnoreCase(ItemType.FILE.toString())) {

                // to be removed later, created new APi for this updateAuditSetsForItemId
                AuditSetItem auditSetItem = AuditSetItem.builder()
                        .auditSetId(auditSet.getAuditSetId())
                        .itemId(addItem.getItemId())
                        .itemName(addItem.getItemName())
                        .itemType(addItem.getItemType())
                        .accessList("ALLOW")
                        .listJson(null)
                        .createdAt(Long.parseLong(dateFormat.format(new Date())))
                        .assignedByUserId(byUserEmail.getId())
                        .build();

                auditSetItemRepository.create(auditSetItem, transaction);


                ItemStatus itemStatus = itemStatusRepository.get(addItem.getItemId(), transaction);

                // if file is added then set status monitored ..
                if (itemStatus == null) {
                    String date = GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date());
                    ItemStatus newItemStatusObject = ItemStatus.builder()
                            .itemId(addItem.getItemId())
                            .status(TamperingStatusType.NOT_TAMPERED.toString())
                            .lastValidatedAt(date)
                            .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                            .monitoredStatus(TamperingStatusType.MONITORED.toString())
                            .itemType(addItem.getItemType())
                            .build();
                    itemStatusRepository.create(newItemStatusObject, transaction);
                } else {

                    List<String> existingAuditSet = new ArrayList<>();
                    try {
                        existingAuditSet = objectMapper.readValue(itemStatus.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        //handle later..
                    }

                    existingAuditSet.add(auditSetId);
                    itemStatus.setMonitoredStatus(TamperingStatusType.MONITORED.toString());
                    itemStatus.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSet));
                    itemStatus.setItemType(addItem.getItemType());
                    itemStatusRepository.create(itemStatus, transaction);


                }
            }
        } else {
            //already present update json list
            if (addItem.getItemType().equalsIgnoreCase(ItemType.FOLDER.toString())) {

                Optional<BasicItemInfo> root = addItem.getItems().stream().filter(e -> e.getId().equals(addItem.getItemId())).findFirst();

                List<BasicItemInfo> basicItemInfos = new ArrayList<>();

                try {
                    basicItemInfos = objectMapper.readValue(checkItem.getListJson(), new TypeReference<List<BasicItemInfo>>() {
                    });
                } catch (JsonProcessingException e) {
                    throw new GenericException("Error converting JSON to Object");
                }


                if (!root.isPresent()) {

                    auditSetItemRepository.delete(checkItem.getAuditSetId(), checkItem.getItemId(), transaction);

                    //remove monitored status
                    removeMonitoredStatus(basicItemInfos.stream()
                                    .map(e -> e.getId())
                                    .collect(Collectors.toList()),
                            auditSetId, transaction);

                } else {


                    Set<Long> existingItems = basicItemInfos.stream()
                            .map(e -> e.getId())
                            .collect(Collectors.toSet());


                    Set<Long> updateItems = addItem.getItems().stream()
                            .map(e -> e.getId())
                            .collect(Collectors.toSet());


                    existingItems.removeAll(updateItems);

                    //remove monitored status from existingList which is not in updatedList.

                    removeMonitoredStatus(existingItems.stream()
                                    .collect(Collectors.toList()),
                            auditSetId,
                            transaction);

                    checkItem.setAccessList("ALLOW");
                    checkItem.setListJson(GenericUtility.convertObjectToStringJson(addItem.getItems()));
                    checkItem.setCreatedAt(Long.parseLong(dateFormat.format(new Date())));
                    auditSetItemRepository.create(checkItem, transaction);

                    // for each item set status monitored ..

                    addToMonitoredStatusBasedOnExisting(
                            basicItemInfos.stream()
                                    .map(e -> e.getId())
                                    .collect(Collectors.toSet()),
                            addItem.getItems(),
                            auditSet.getAuditSetId(),
                            transaction);

                }
            }

        }
        return new ApiResponse(true, Translator.toLocale("com.addItem.auditSet"), HttpStatus.OK, addItem.getItems());
    }

    private void addToMonitoredStatusBasedOnExisting(Set<Long> existingItems, List<BasicItemInfo> items, String auditSetId, DistributedTransaction transaction) {


        items.stream()
                .filter(e -> !existingItems.contains(e.getId()))
                .forEach(itemInfo -> {
                    ItemStatus itemObject = itemStatusRepository.get(itemInfo.getId(), transaction);
                    if (itemObject == null) {
                        String date = GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date());
                        ItemStatus newItemStatusObject = ItemStatus.builder()
                                .itemId(itemInfo.getId())
                                .itemType(itemInfo.getType())
                                .status(TamperingStatusType.NOT_TAMPERED.toString())
                                .lastValidatedAt(date)
                                .monitoredStatus(TamperingStatusType.MONITORED.toString())
                                .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                                .build();
                        itemStatusRepository.create(newItemStatusObject, transaction);
                    } else {
                        List<String> existingAuditSet = new ArrayList<>();
                        try {
                            existingAuditSet = objectMapper.readValue(itemObject.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                            });
                        } catch (JsonProcessingException e) {
                            throw new GenericException("Error converting JSON to Object");
                        }
                        existingAuditSet.add(auditSetId);
                        itemObject.setMonitoredStatus(TamperingStatusType.MONITORED.toString());
                        itemObject.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSet));
                        itemObject.setItemType(itemInfo.getType());
                        itemStatusRepository.create(itemObject, transaction);

                    }
                });

    }

    private void removeMonitoredStatus(List<Long> items, String auditSetId, DistributedTransaction transaction) {


        items.stream()
                .map(itemInfo -> itemStatusRepository.get(itemInfo, transaction))
                .filter(item -> item != null)
                .forEach(itemInfo -> {
                            List<String> existingAuditSet = new ArrayList<>();
                            try {
                                existingAuditSet = objectMapper.readValue(itemInfo.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                                });
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                                //handle later..
                            }

                            if (existingAuditSet.contains(auditSetId)) {
                                existingAuditSet.remove(auditSetId);
                                itemInfo.setMonitoredStatus(existingAuditSet.size() >= 1 ? TamperingStatusType.MONITORED.toString() : TamperingStatusType.NOT_MONITORED.toString());
                                itemInfo.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSet));
                                itemInfo.setItemType(itemInfo.getItemType());
                                itemStatusRepository.create(itemInfo, transaction);

                            }
                        }

                );

    }

    public AuditSetItem isAuditSetItemPresent(String auditSetId, Long itemId, DistributedTransaction transaction) {
        return auditSetItemRepository.get(auditSetId, itemId, transaction);
    }


    void addToMonitoredStatus(List<BasicItemInfo> basicItemInfo, String auditSetId, DistributedTransaction transaction) {

        basicItemInfo.stream()
                .forEach(itemInfo -> {
                            ItemStatus itemObject = itemStatusRepository.get(itemInfo.getId(), transaction);
                            if (itemObject == null) {
                                String date = GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date());
                                ItemStatus newItemStatusObject = ItemStatus.builder()
                                        .itemId(itemInfo.getId())
                                        .itemType(itemInfo.getType())
                                        .status(TamperingStatusType.NOT_TAMPERED.toString())
                                        .lastValidatedAt(date)
                                        .monitoredStatus(TamperingStatusType.MONITORED.toString())
                                        .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                                        .build();
                                itemStatusRepository.create(newItemStatusObject, transaction);
                            } else {
                                List<String> existingAuditSet = new ArrayList<>();
                                try {
                                    existingAuditSet = objectMapper.readValue(itemObject.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                                    });
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                    //handle later..
                                }
                                existingAuditSet.add(auditSetId);
                                itemObject.setMonitoredStatus(TamperingStatusType.MONITORED.toString());
                                itemObject.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSet));
                                itemObject.setItemType(itemInfo.getType());
                                itemStatusRepository.create(itemObject, transaction);

                            }
                        }

                );
    }


    public ApiResponse getAuditSetItems(String auditSetId, DistributedTransaction transaction) throws CrudException {

        List<AuditSetItem> itemsList = auditSetItemRepository.getAuditSetItems(auditSetId, transaction);

        // Empty list
        List<AuditSetItemsVisibility> auditSetItemsList = new ArrayList<>();

        User user = userRepository.getByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName(), transaction);

        if (!itemsList.isEmpty()) {

            List<AuditSetCollaborators> auditSetCollaborators = auditSetCollaboratorsRepository.getAuditSetCollaborators(user.getUserEmail(), auditSetId, transaction);
            auditSetCollaborators
                    .forEach(auditSetCollaborators1 -> {
                                AuditSetCollaborators auditSetCollaborators2 = auditSetCollaboratorsRepository.get(auditSetCollaborators1.getAuditSetId(), auditSetCollaborators1.getAuditSetRole(), auditSetCollaborators1.getUserEmail(), transaction);
                                auditSetCollaborators2.setAccessStatus(AccessStatus.UNDER_REVIEW.toString());
                                auditSetCollaboratorsRepository.create(auditSetCollaborators2, transaction);
                            }
                    );

            SimpleDateFormat dateOutputFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();

            boolean needsRefresh = connection.needsRefresh();
            log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

            if (needsRefresh) {
                connection = boxUtility.getBoxEnterpriseConnection();
                log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
            }

            for (AuditSetItem auditSetItem : itemsList) {

                log.info("ItemId : {}", auditSetItem.getItemId());
                log.info("Item Type: {}", auditSetItem.getItemType());
                try {
                    if (auditSetItem.getItemType().equalsIgnoreCase(ItemType.FILE.toString())) {
                        BoxFile fileObject = new BoxFile(connection, String.valueOf(auditSetItem.getItemId()));
                        BoxFile.Info fileInfo = fileObject.getInfo();
                        AuditSetItemsVisibility auditSetItemsVisibility = new AuditSetItemsVisibility(
                                Long.parseLong(fileInfo.getID()),
                                fileInfo.getName(),
                                fileInfo.getType(),
                                true,
                                0L,
                                dateOutputFormat.format(fileInfo.getCreatedAt()),
                                dateOutputFormat.format(fileInfo.getModifiedAt()),
                                fileInfo.getCreatedBy().getName(),
                                fileInfo.getModifiedBy().getName(),
                                GenericUtility.getStringSizeLengthFile(fileInfo.getSize()),
                                fileInfo.getCreatedBy().getLogin(),
                                fileInfo.getModifiedBy().getLogin(),
                                fileInfo.getSha1()
                        );
                        auditSetItemsList.add(auditSetItemsVisibility);
                    } else {
                        BoxFolder folderObject = new BoxFolder(connection, String.valueOf(auditSetItem.getItemId()));
                        BoxFolder.Info folderInfo = folderObject.getInfo();
                        AuditSetItemsVisibility auditSetItemsVisibility = new AuditSetItemsVisibility(
                                Long.parseLong(folderInfo.getID()),
                                folderInfo.getName(),
                                folderInfo.getType(),
                                true,
                                0L,
                                dateOutputFormat.format(folderInfo.getCreatedAt()),
                                dateOutputFormat.format(folderInfo.getModifiedAt()),
                                folderInfo.getCreatedBy().getName(),
                                folderInfo.getModifiedBy().getName(),
                                GenericUtility.getStringSizeLengthFile(folderInfo.getSize()),
                                folderInfo.getCreatedBy().getLogin(),
                                folderInfo.getModifiedBy().getLogin(),
                                ""
                        );
                        auditSetItemsList.add(auditSetItemsVisibility);
                    }
                } catch (BoxAPIResponseException e) {
                    // Handle the exception here
                    log.info("Error processing item: " + auditSetItem.getItemId() + ". Skipping...");
                    e.printStackTrace(); // This is optional, you may handle the exception silently if desired
                    continue; // Skip to the next iteration of the loop
                }
            }

            return new ApiResponse(true, "", HttpStatus.OK, auditSetItemsList);
        }
        return new ApiResponse(true, Translator.toLocale("com.item.empty"), HttpStatus.OK, auditSetItemsList);

    }


    public ApiResponse getAllowListFromAuditSet(String auditSetId, Long itemId, DistributedTransaction transaction) {

        // Check if the item is already present in the AuditSet
        AuditSetItem auditSetItemObject = auditSetItemRepository.get(auditSetId, itemId, transaction);

        List<BasicItemInfo> basicItemInfos = new ArrayList<>();
        if (auditSetItemObject == null) {
            return new ApiResponse(true, "", HttpStatus.OK, basicItemInfos);
        }

        try {

            basicItemInfos = objectMapper.readValue(auditSetItemObject.getListJson(), new TypeReference<List<BasicItemInfo>>() {
            });
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting JSON to Object");
        }

        return new ApiResponse(true, "", HttpStatus.OK, basicItemInfos);


    }

    public ApiResponse getItemFromAuditSet(String auditSetId, Long itemId, Long subfolderId, DistributedTransaction transaction) {
        // Check if the item is already present in the AuditSet
        AuditSetItem auditSetItemObject = auditSetItemRepository.get(auditSetId, itemId, transaction);
        List<AuditSetItemsVisibility> auditSetItemsVisibilityList = new ArrayList<>();

        if (auditSetItemObject == null) {
            return new ApiResponse(true, "", HttpStatus.OK, auditSetItemsVisibilityList);
        }
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

        boolean needsRefresh = connection.needsRefresh();
        log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

        if (needsRefresh) {
            connection = boxUtility.getBoxEnterpriseConnection();
            log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
        }

        BoxFolder folder = new BoxFolder(connection, subfolderId == null ? String.valueOf(itemId) : String.valueOf(subfolderId));
        SimpleDateFormat dateFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();

        for (BoxItem.Info itemInfo : folder) {
            boolean isAllowed;
            if (itemInfo instanceof BoxFile.Info) {

                boolean toShow = itemIds.contains(Long.parseLong(itemInfo.getID()));
                BoxFile fileObject = new BoxFile(connection, itemInfo.getID());
                BoxFile.Info fileInfo = fileObject.getInfo();
                if (toShow) {
                    log.info("------------------------------");
                    log.info("Files Name: " + fileInfo.getName() + ", CreatedAt: " + fileInfo.getCreatedAt() + ", Size: " + fileInfo.getSize());
                    isAllowed = itemIds.contains(Long.parseLong(fileInfo.getID()));
                    AuditSetItemsVisibility auditSetItemsVisibility = new AuditSetItemsVisibility(
                            Long.parseLong(fileInfo.getID()),
                            fileInfo.getName(),
                            fileInfo.getType(),
                            isAllowed,
                            itemId,
                            dateFormat.format(fileInfo.getCreatedAt()),
                            dateFormat.format(fileInfo.getModifiedAt()),
                            fileInfo.getCreatedBy().getName(),
                            fileInfo.getModifiedBy().getName(),
                            GenericUtility.getStringSizeLengthFile(fileInfo.getSize()),
                            fileInfo.getCreatedBy().getLogin(),
                            fileInfo.getModifiedBy().getLogin(),
                            fileInfo.getSha1()

                    );
                    auditSetItemsVisibilityList.add(auditSetItemsVisibility);
                }
            } else if (itemInfo instanceof BoxFolder.Info) {
                boolean toShow = itemIds.contains(Long.parseLong(itemInfo.getID()));
                BoxFolder folderObject = new BoxFolder(connection, itemInfo.getID());
                BoxFolder.Info folderInfo = folderObject.getInfo();
                if (toShow) {
                    log.info("------------------------------");
                    log.info("Folder Name: " + folderInfo.getName() + ", CreatedAt: " + folderInfo.getCreatedAt() + ", Size: " + folderInfo.getSize());
                    isAllowed = itemIds.contains(Long.parseLong(folderInfo.getID()));
                    AuditSetItemsVisibility auditSetItemsVisibility = new AuditSetItemsVisibility(
                            Long.parseLong(folderInfo.getID()),
                            folderInfo.getName(),
                            folderInfo.getType(),
                            isAllowed,
                            itemId,
                            dateFormat.format(folderInfo.getCreatedAt()),
                            dateFormat.format(folderInfo.getModifiedAt()),
                            folderInfo.getCreatedBy().getName(),
                            folderInfo.getModifiedBy().getName(),
                            GenericUtility.getStringSizeLengthFile(folderInfo.getSize()),
                            folderInfo.getCreatedBy().getLogin(),
                            folderInfo.getModifiedBy().getLogin(),
                            ""
                    );
                    auditSetItemsVisibilityList.add(auditSetItemsVisibility);
                }
            } else {
                log.info("Invalid type");
            }
        }
        return new ApiResponse(true, "", HttpStatus.OK, auditSetItemsVisibilityList);
    }


    public ApiResponse verifyAuditSet(String auditSetId, DistributedTransaction transaction) {
        ApiResponse auditSetItems;
        try {
            auditSetItems = getAuditSetItems(auditSetId, transaction);
        } catch (CrudException e) {
            throw new GenericException("Something Went Wrong !!");
        }
        if (auditSetItems.getStatus().equals(false)) {
            return auditSetItems;
        }
        List<VerifyItemStatus> verifyItemStatuses = new ArrayList<>();
        List<AuditSetItemsVisibility> auditSetItemsList = (List<AuditSetItemsVisibility>) auditSetItems.getData();

        for (AuditSetItemsVisibility auditSetItemsVisibility : auditSetItemsList) {
            AuditSetItem auditSetItemObject = auditSetItemRepository.get(auditSetId, auditSetItemsVisibility.getItemId(), transaction);
            if (auditSetItemObject == null) {
                log.info("AuditSet Id:" + auditSetId + " Item Id:" + auditSetItemsVisibility.getItemId() + " ItemType:" + auditSetItemsVisibility.getItemType());
                throw new GenericException(Translator.toLocale("com.invalid.item"));
            }
            if (auditSetItemObject.getItemType().equalsIgnoreCase(ItemType.FILE.toString())) {
                ItemStatus itemStatus = itemStatusRepository.get(auditSetItemObject.getItemId(), transaction);
                String date = GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date());
                String tamperingStatus = assetService.getTamperingStatus(String.valueOf(auditSetItemObject.getItemId()));
                if (itemStatus == null) {
                    itemStatus = ItemStatus.builder()
                            .status(TamperingStatusType.NOT_TAMPERED.toString())
                            .itemId(auditSetItemObject.getItemId())
                            .itemType(ItemType.FILE.getType())
                            .monitoredStatus(TamperingStatusType.MONITORED.toString())
                            .lastValidatedAt(date)
                            .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                            .build();
                }
                itemStatus.setStatus(tamperingStatus);
                itemStatus.setLastValidatedAt(date);

                itemStatusRepository.create(itemStatus, transaction);

                boolean needsRefresh = connection.needsRefresh();
                log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

                if (needsRefresh) {
                    connection = boxUtility.getBoxEnterpriseConnection();
                    log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
                }

                BoxFile fileObject = new BoxFile(connection, String.valueOf(auditSetItemObject.getItemId()));
                BoxFile.Info fileInfo = fileObject.getInfo();
                List<String> paths = fileInfo.getPathCollection().stream()
                        .map(path -> path.getName())
                        .collect(Collectors.toList());
                paths.add(fileInfo.getName());
                String path = paths.stream().collect(Collectors.joining("/"));
                verifyItemStatuses.add(
                        VerifyItemStatus.builder()
                                .id(fileInfo.getID())
                                .status(tamperingStatus)
                                .name(fileInfo.getName())
                                .type("file")
                                .path(path)
                                .build()
                );
            } else {
                ItemStatus itemStatus = itemStatusRepository.get(auditSetItemsVisibility.getItemId(), transaction);
                if (itemStatus == null) {
                    itemStatus = ItemStatus.builder()
                            .itemId(auditSetItemsVisibility.getItemId())
                            .itemType(ItemType.FOLDER.getType())
                            .monitoredStatus(TamperingStatusType.MONITORED.toString())
                            .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                            .build();
                    itemStatusRepository.create(itemStatus, transaction);

                } else {
                    List<String> existingAuditSetList = new ArrayList<>();
                    try {
                        existingAuditSetList = objectMapper.readValue(itemStatus.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    if (!existingAuditSetList.contains(auditSetId)) {
                        existingAuditSetList.add(auditSetId);
                        itemStatus.setMonitoredStatus(TamperingStatusType.MONITORED.toString());
                        itemStatus.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSetList));
                        itemStatusRepository.create(itemStatus, transaction);
                    }
                }

                List<BasicItemInfo> basicItemInfo;
                try {
                    basicItemInfo = objectMapper.readValue(auditSetItemObject.getListJson(), new TypeReference<List<BasicItemInfo>>() {
                    });
                } catch (JsonProcessingException e) {
                    throw new GenericException("Error converting JSON to Object");
                }
                List<Long> itemsIds = basicItemInfo.stream()
                        .map(e -> e.getId())
                        .collect(Collectors.toList());
                verifyItemStatuses.addAll(validateFolder(auditSetId, auditSetItemsVisibility, itemsIds, connection, transaction));
            }
        }

        List<VerifyItemStatus> tamperedFiles = verifyItemStatuses.stream()
                .filter(e -> e.getStatus().equalsIgnoreCase(TamperingStatusType.TAMPERED.toString()))
                .collect(Collectors.toList());
        LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>(Map.ofEntries(
                Map.entry("totalFilesChecked", verifyItemStatuses.size()),
                Map.entry("filesTamperedCount", tamperedFiles.size()),
                Map.entry("tamperedFiles", tamperedFiles)
        ));
        return new ApiResponse(true, Translator.toLocale("com.validate.item"), HttpStatus.OK, linkedHashMap);
    }

    private List<VerifyItemStatus> validateFolder(String auditSetId, AuditSetItemsVisibility auditSetItemsVisibility, List<Long> itemIDs, BoxAPIConnection boxAPIConnection, DistributedTransaction transaction) {
        List<VerifyItemStatus> verifyItemStatuses = new ArrayList<>();

        BoxFolder folder = new BoxFolder(boxAPIConnection, String.valueOf(auditSetItemsVisibility.getItemId()));
        for (BoxItem.Info itemInfo : folder) {
            if (itemIDs.contains(Long.parseLong(itemInfo.getID()))) {

                if (itemInfo instanceof BoxFile.Info) {
                    BoxFile fileObject = new BoxFile(boxAPIConnection, itemInfo.getID());
                    BoxFile.Info fileInfo = fileObject.getInfo();
                    Long fileId = Long.parseLong(fileInfo.getID());
                    SimpleDateFormat dateFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();
                    String date = dateFormat.format(new Date());
                    String tamperingStatus = assetService.getTamperingStatus(fileInfo.getID());
                    ItemStatus itemStatus = itemStatusRepository.get(fileId, transaction);
                    if (itemStatus == null) {
                        itemStatus = ItemStatus.builder()
                                .status(TamperingStatusType.NOT_TAMPERED.toString())
                                .itemId(fileId)
                                .itemType(ItemType.FILE.getType())
                                .monitoredStatus(TamperingStatusType.MONITORED.toString())
                                .lastValidatedAt(date)
                                .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                                .build();
                    }
                    itemStatus.setStatus(tamperingStatus);
                    itemStatus.setLastValidatedAt(date);

                    itemStatusRepository.create(itemStatus, transaction);

                    List<String> paths = fileInfo.getPathCollection().stream()
                            .map(path -> path.getName())
                            .collect(Collectors.toList());
                    paths.add(fileInfo.getName());
                    String path = paths.stream().collect(Collectors.joining("/"));
                    verifyItemStatuses.add(
                            VerifyItemStatus.builder()
                                    .id(fileInfo.getID())
                                    .status(tamperingStatus)
                                    .name(fileInfo.getName())
                                    .type("file")
                                    .path(path)
                                    .build()
                    );
                } else if (itemInfo instanceof BoxFolder.Info) {
                    BoxFolder folderObject = new BoxFolder(boxAPIConnection, itemInfo.getID());
                    BoxFolder.Info folderInfo = folderObject.getInfo();
                    long folderId = Long.parseLong(folderInfo.getID());
                    AuditSetItemsVisibility itemsVisibility = new AuditSetItemsVisibility(
                            Long.parseLong(folderInfo.getID()),
                            folderInfo.getName(),
                            folderInfo.getType(),
                            true,
                            0L,
                            folderInfo.getCreatedAt().toString(),
                            folderInfo.getModifiedAt().toString(),
                            folderInfo.getCreatedBy().getName(),
                            folderInfo.getModifiedBy().getName(),
                            GenericUtility.getStringSizeLengthFile(folderInfo.getSize()),
                            folderInfo.getCreatedBy().getLogin(),
                            folderInfo.getModifiedBy().getLogin(),
                            ""
                    );

                    ItemStatus itemStatus = itemStatusRepository.get(folderId, transaction);
                    if (itemStatus == null) {
                        itemStatus = ItemStatus.builder()
                                .itemId(folderId)
                                .itemType(ItemType.FOLDER.getType())
                                .monitoredStatus(TamperingStatusType.MONITORED.toString())
                                .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSetId)))
                                .build();
                        itemStatusRepository.create(itemStatus, transaction);

                    } else {
                        List<String> existingAuditSetList = new ArrayList<>();
                        try {
                            existingAuditSetList = objectMapper.readValue(itemStatus.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                            });
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        if (!existingAuditSetList.contains(auditSetId)) {
                            existingAuditSetList.add(auditSetId);
                            itemStatus.setMonitoredStatus(TamperingStatusType.MONITORED.toString());
                            itemStatus.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSetList));
                            itemStatusRepository.create(itemStatus, transaction);
                        }
                    }


                    verifyItemStatuses.addAll(validateFolder(auditSetId, itemsVisibility, itemIDs, boxAPIConnection, transaction));
                } else {
                    log.info("Invalid type");
                    continue; // Skip to the next iteration if the item type is invalid
                }
            }
        }

        return verifyItemStatuses;
    }

    public ApiResponse updateAuditSetsForItemId(Long itemId, UpdateAuditSetItem auditSetItem, BoxAPIConnection boxApiConnection, DistributedTransaction transaction) {

        User currentUser = userRepository.getByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName(), transaction);

        ApiResponse apiResponse = auditSetService.getMyAuditSetList(SecurityContextHolder.getContext().getAuthentication().getName(), transaction);
        List<AuditSetLists> auditSetLists = (List<AuditSetLists>) apiResponse.getData();
        auditSetLists.forEach(auditSetObject -> {

                    auditSetObject.setIsItemIdAdded(auditSetService.isItemExistInAuditSet(itemId, auditSetObject.getAuditSetId(), transaction));
                }
        );

        auditSetItem.getAuditSetLists().forEach(
                auditSet -> {
                    if (auditSet.getIsItemIdAdded()) {

                        AuditSetItem auditSetItemObj = auditSetItemRepository.get(auditSet.getAuditSetId(), itemId, transaction);

                        if (auditSetItemObj == null) {
                            AuditSetItem auditSetItemPut = AuditSetItem.builder()
                                    .auditSetId(auditSet.getAuditSetId())
                                    .itemId(itemId)
                                    .itemName(auditSetItem.getItemName())
                                    .itemType(ItemType.FILE.getType())
                                    .accessList("ALLOW")
                                    .listJson(null)
                                    .createdAt(Long.parseLong(GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date())))
                                    .assignedByUserId(currentUser.getId())
                                    .build();

                            auditSetItemRepository.create(auditSetItemPut, transaction);

                            ItemStatus itemStatus = itemStatusRepository.get(itemId, transaction);

                            // if file is added then set status monitored ..
                            if (itemStatus == null) {
                                String date = GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date());
                                ItemStatus newItemStatusObject = ItemStatus.builder()
                                        .itemId(itemId)
                                        .status(TamperingStatusType.NOT_TAMPERED.toString())
                                        .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(List.of(auditSet.getAuditSetId())))
                                        .monitoredStatus(TamperingStatusType.MONITORED.toString())
                                        .lastValidatedAt(date)
                                        .itemType(ItemType.FILE.getType())
                                        .build();
                                itemStatusRepository.create(newItemStatusObject, transaction);
                            } else {

                                List<String> existingAuditSetList = new ArrayList<>();
                                try {
                                    existingAuditSetList = objectMapper.readValue(itemStatus.getListOfAuditsetJson(), new TypeReference<List<String>>() {
                                    });
                                } catch (JsonProcessingException e) {
                                    throw new GenericException(Translator.toLocale("com.unexpected.error"));
                                }

                                existingAuditSetList.add(auditSet.getAuditSetId());
                                itemStatus.setMonitoredStatus(TamperingStatusType.MONITORED.toString());
                                itemStatus.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSetList));
                                itemStatus.setItemType(ItemType.FILE.getType());
                                itemStatusRepository.create(itemStatus, transaction);

                            }

                        } else {
                            auditSetItemObj.setCreatedAt(Long.parseLong(GenericUtility.getUTCDateFormatWithoutMilliseconds().format(new Date())));
                            auditSetItemRepository.create(auditSetItemObj, transaction);
                        }

                        BoxFile boxFile = new BoxFile(boxApiConnection, String.valueOf(itemId));

                        BoxResourceIterable<BoxCollaboration.Info> allFileCollaborations = boxFile.getAllFileCollaborations();

                        List<String> collaborators = new ArrayList<>();
                        for (BoxCollaboration.Info info : allFileCollaborations) {
                            collaborators.add(info.getAccessibleBy().getLogin());

                        }

                        if (!collaborators.contains(boxServiceAccountEmail)) {
                            boxFile.collaborate(boxServiceAccountEmail, BoxCollaboration.Role.EDITOR, false, false);
                        }

                    } else {
                        AuditSetItem auditSetItemObj = auditSetItemRepository.get(auditSet.getAuditSetId(), itemId, transaction);

                        if (auditSetItemObj != null) {

                            auditSetItemRepository.delete(auditSetItemObj.getAuditSetId(), auditSetItemObj.getItemId(), transaction);

                        }

                        //check if initially it was true(is added field), if no,  don't remove this audit set from list..
                        //if yes, then  remove from this list
                        Optional<AuditSetLists> auditSetStatusObject = auditSetLists.stream()
                                .filter(e -> e.getAuditSetId().equals(auditSet.getAuditSetId()))
                                .findFirst();
                        log.info("auditSetLists Object : {}", auditSetStatusObject.get());


                        if (auditSetStatusObject.isPresent() && auditSetStatusObject.get().getIsItemIdAdded().equals(true)) {


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

                                if (existingAuditSet.contains(auditSet.getAuditSetId())) {
                                    existingAuditSet.remove(auditSet.getAuditSetId());
                                    itemStatus.setMonitoredStatus(existingAuditSet.size() >= 1 ? TamperingStatusType.MONITORED.toString() : TamperingStatusType.NOT_MONITORED.toString());
                                    itemStatus.setListOfAuditsetJson(GenericUtility.convertObjectToStringJson(existingAuditSet));
                                    itemStatus.setItemType(ItemType.FILE.getType());
                                    log.info("ItemStatus: {}", itemStatus);
                                    itemStatusRepository.create(itemStatus, transaction);

                                }
                            }
                        }

                    }

                }
        );

        return ApiResponse.builder()
                .status(true)
                .httpStatus(HttpStatus.OK)
                .message(Translator.toLocale("com.auditSetWithItem.update"))
                .data(auditSetItem)
                .build();


    }
}


