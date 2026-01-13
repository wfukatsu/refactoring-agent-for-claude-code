package com.scalar.events_log_tool.application.service;

import com.box.sdk.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.ActionType;
import com.scalar.events_log_tool.application.constant.ItemType;
import com.scalar.events_log_tool.application.constant.TamperingStatusType;
import com.scalar.events_log_tool.application.constant.UserRoles;
import com.scalar.events_log_tool.application.dto.ExtAuditorEventLog;
import com.scalar.events_log_tool.application.dto.ItemOwner;
import com.scalar.events_log_tool.application.dto.OwnedBy;
import com.scalar.events_log_tool.application.dto.SubmitToken;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.exception.NotFoundException;
import com.scalar.events_log_tool.application.model.*;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.*;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileService {
    private final UserRepository userRepository;
    private final ItemStatusRepository itemStatusRepository;
    private final AuditorLogsRepository auditorLogsRepository;
    private final AuditSetRepository auditSetRepository;
    private final ItemsBySha1Repository itemsBySha1Repository;
    private final UserService userService;
    private final BoxUtility boxUtility;
    private final ObjectMapper objectMapper;

    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection connection;

    public FileService(UserRepository userRepository, ItemStatusRepository itemStatusRepository, AuditorLogsRepository auditorLogsRepository, AuditSetRepository auditSetRepository, ItemsBySha1Repository itemsBySha1Repository, UserService userService, BoxUtility boxUtility, ObjectMapper objectMapper) {

        this.userRepository = userRepository;
        this.itemStatusRepository = itemStatusRepository;
        this.auditorLogsRepository = auditorLogsRepository;
        this.auditSetRepository = auditSetRepository;
        this.itemsBySha1Repository = itemsBySha1Repository;
        this.userService = userService;
        this.boxUtility = boxUtility;
        this.objectMapper = objectMapper;
    }

    public ApiResponse getFileDetails(Long itemId, String auditSetId, String userEmail, DistributedTransaction transaction) {
        try {

            //get item from item table for getting details of particular item
            ItemStatus itemStatus = itemStatusRepository.get(itemId, transaction);

            if (itemId == null) {
                throw new GenericException(Translator.toLocale("com.item.notFound"));
            }

            //fetch role from token for authority
            List<String> authorities = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());
            //box connection

            boolean needsRefresh = this.connection.needsRefresh();
            log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

            if (needsRefresh) {
                this.connection = boxUtility.getBoxEnterpriseConnection();
                log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
            }

            BoxAPIConnection boxAPIConnection;
            if (auditSetId != null) {
                boxAPIConnection = connection;

            } else if (authorities.contains(UserRoles.AUDIT_ADMIN.toString()) || authorities.contains(UserRoles.GENERAL_USER.toString())) {
                SubmitToken submitToken = userService.updateLatestToken(userEmail, transaction);
                String accessToken = submitToken.getAccessToken();
                boxAPIConnection = new BoxAPIConnection(accessToken);
            } else {
                boxAPIConnection = connection;
            }

            BoxFile file = new BoxFile(boxAPIConnection, String.valueOf(itemId));

            BoxFile.Info info = file.getInfo();

            List<BoxFolder.Info> pathCollection = info.getPathCollection();

            String path = pathCollection.stream()
                    .map(e -> e.getName())
                    .collect(Collectors.joining("/"));

            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            //covert data into utc format(created at)
            Date createdAt = info.getCreatedAt();
            String formattedCreatedUtcDate = utcFormat.format(createdAt);

            //covert data into utc format(modified at)
            Date modifiedAt = info.getModifiedAt();
            String formattedModifiedUtcDate = utcFormat.format(modifiedAt);


            //set owned by object
            OwnedBy ownedBy = new OwnedBy();
            ownedBy.setId(info.getOwnedBy().getID());
            ownedBy.setName(info.getOwnedBy().getName());
            ownedBy.setLogin(info.getOwnedBy().getLogin());
            ownedBy.setType(String.valueOf(info.getOwnedBy().getType()));

            //set modified by object
            OwnedBy modifiedBy = new OwnedBy();
            modifiedBy.setId(info.getModifiedBy().getID());
            modifiedBy.setName(info.getModifiedBy().getName());
            modifiedBy.setLogin(info.getModifiedBy().getLogin());
            modifiedBy.setType(String.valueOf(info.getModifiedBy().getType()));

            // Convert size from bytes to megabytes
            String sizeInMegabytes = GenericUtility.getStringSizeLengthFile(info.getSize());


            //get data in file details dto
            FileDetailsDto fileDetailsDto = FileDetailsDto.builder()
                    .id(itemId)
                    .name(info.getName())
                    .type(info.getType())
                    .description(info.getDescription())
                    .createdAt(formattedCreatedUtcDate)
                    .modifiedAt(formattedModifiedUtcDate)
                    .size(sizeInMegabytes)
                    .sha1(info.getSha1())
                    .ownedBy(ownedBy)
                    .modifiedBy(modifiedBy)
                    .path(path)
                    .build();
            if (itemStatus == null) {
                fileDetailsDto.setTamperedStatus(TamperingStatusType.NOT_MONITORED.toString());
            } else {
                fileDetailsDto.setTamperedStatus(itemStatus.getStatus());
            }

            return new ApiResponse(true, "", HttpStatus.OK, fileDetailsDto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, Translator.toLocale("com.error.file"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ApiResponse getFolderDetails(Long folderId, String auditSetId, String userEmail, DistributedTransaction transaction) {
        try {

            if (folderId == null) {
                throw new GenericException(Translator.toLocale("com.folder.notFound"));
            }
            //fetch role from token for authority
            List<String> collect = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());
            //box connection

            boolean needsRefresh = connection.needsRefresh();
            log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

            if (needsRefresh) {
                connection = boxUtility.getBoxEnterpriseConnection();
                log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
            }

            BoxAPIConnection boxAPIConnection;
            if (auditSetId != null) {
                boxAPIConnection = connection;
            } else if (collect.contains(UserRoles.AUDIT_ADMIN.toString()) || collect.contains(UserRoles.GENERAL_USER.toString())) {
                SubmitToken submitToken = userService.updateLatestToken(userEmail, transaction);
                String accessToken = submitToken.getAccessToken();
                boxAPIConnection = new BoxAPIConnection(accessToken);
            } else {
                boxAPIConnection = connection;
            }

            BoxFolder folder = new BoxFolder(boxAPIConnection, String.valueOf(folderId));

            BoxFolder.Info info = folder.getInfo();

            List<BoxFolder.Info> pathCollection = info.getPathCollection();
            String path = pathCollection.stream()
                    .map(e -> e.getName())
                    .collect(Collectors.joining("-> "));
            //covert data into utc format(created at)
            Date createdAt = info.getCreatedAt();
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedUtcCreatedAt = utcFormat.format(createdAt);


            //covert data into utc format(modified at)
            Date modifiedAt = info.getModifiedAt();
            String formattedUtcModifiedAt = utcFormat.format(modifiedAt);

            //set owned by object
            OwnedBy ownedBy = new OwnedBy();
            ownedBy.setId(info.getOwnedBy().getID());
            ownedBy.setName(info.getOwnedBy().getName());
            ownedBy.setLogin(info.getOwnedBy().getLogin());
            ownedBy.setType(String.valueOf(info.getOwnedBy().getType()));

            //set modified by object
            OwnedBy modifiedBy = new OwnedBy();
            modifiedBy.setId(info.getModifiedBy().getID());
            modifiedBy.setName(info.getModifiedBy().getName());
            modifiedBy.setLogin(info.getModifiedBy().getLogin());
            modifiedBy.setType(String.valueOf(info.getModifiedBy().getType()));

            // Convert size from bytes to megabytes
            String sizeInMegabytes = GenericUtility.getStringSizeLengthFile(info.getSize());


            //get data in folder details dto
            FolderDetailsDto folderDetailsDto = FolderDetailsDto.builder()
                    .id(Long.valueOf(info.getID()))
                    .name(info.getName())
                    .type(info.getType())
                    .description(info.getDescription())
                    .createdAt(formattedUtcCreatedAt)
                    .modifiedAt(formattedUtcModifiedAt)
                    .size(sizeInMegabytes)
                    .ownedBy(ownedBy)
                    .modifiedBy(modifiedBy)
                    .path(path)
                    .build();

            return new ApiResponse(true, "", HttpStatus.OK, folderDetailsDto);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, Translator.toLocale("com.error.folder"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ApiResponse getFileCopies(String sha1Hash, Long itemId, DistributedTransaction transaction) throws TransactionException {

        // Retrieve items with the same SHA-1 hash
        List<ItemsBySha1> itemsBySha1 = itemsBySha1Repository.getItemsBySha1(sha1Hash, transaction);

        boolean needsRefresh = connection.needsRefresh();
        log.info("Does Box Connection need Refresh Call: " + needsRefresh);

        if (needsRefresh) {
            connection = boxUtility.getBoxEnterpriseConnection();
            log.info("After Box Connection Refresh Status: " + connection.needsRefresh());
        }

        // Filter out the item with the specified itemId
        List<CopyItems> copies = itemsBySha1.stream()
                .filter(e -> e.getItemId() != itemId)
                .map(e -> {
                    CopyItems copyItems = CopyItems.builder()
                            .sha1Hash(e.getSha1Hash())
                            .itemId(e.getItemId())
                            .itemVersionId(e.getItemVersionId())
                            .createdAt(e.getCreatedAt())
                            .itemName(e.getItemName())
                            .itemVersionNumber(e.getItemVersionNumber())
                            .path(e.getPath())
                            .isDeleted(false)
                            .build();
                    try {
                        BoxFile file = new BoxFile(connection, String.valueOf(e.getItemId()));
                        BoxFile.Info info = file.getInfo();
                    } catch (BoxAPIResponseException ex) {
                        // Handle the exception here
                        if (ex.getMessage().contains("not_found")) {
                            copyItems.setSha1Hash("deleted-item");
                            // this means deleted is permannetly deleted ..
                        }
                        log.info("Msg: " + ex.getMessage());
                        copyItems.setIsDeleted(true);
                    }
                    return copyItems;
                }).filter(s -> !s.getSha1Hash().equalsIgnoreCase("deleted-item"))
                .collect(Collectors.toList());

        // check copy items
        if (!copies.isEmpty())
            return new ApiResponse(true, "", HttpStatus.OK, copies);
        else
            return new ApiResponse(true, Translator.toLocale("com.empty.copies"), HttpStatus.OK, copies);
    }


    public ApiResponse getFileVersions(String fileId, String currentEmail, DistributedTransaction transaction, String auditSetId) throws TransactionException, ParseException {

        List<String> authorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .map(e -> e.getAuthority())
                .collect(Collectors.toList());

        boolean needsRefresh = connection.needsRefresh();
        log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

        if (needsRefresh) {
            connection = boxUtility.getBoxEnterpriseConnection();
            log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
        }

        BoxAPIConnection boxAPIConnection;

        if (auditSetId != null) {
            boxAPIConnection = connection;
        } else if (authorities.contains(UserRoles.AUDIT_ADMIN.toString()) || authorities.contains(UserRoles.GENERAL_USER.toString())) {
            SubmitToken submitToken = userService.updateLatestToken(currentEmail, transaction);
            String accessToken = submitToken.getAccessToken();
            boxAPIConnection = new BoxAPIConnection(accessToken);
        } else {
            boxAPIConnection = connection;
        }

        // Create a BoxFile instance using the fileId
        BoxFile file = new BoxFile(boxAPIConnection, fileId);

        // Retrieve versions of the file with specific fields
        Collection<BoxFileVersion> versions = file.getVersions("version_number", "id", "sha1", "modified_at", "name", "uploader_display_name");

        List<FileVersions> fileVersionDetailsList = new ArrayList<>();

        // Create a SimpleDateFormat object for UTC with the format
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Iterate through each version and extract relevant information
        for (BoxFileVersion version : versions) {

            Date createdAt = version.getModifiedAt();
            // Format the date in UTC
            String formattedUtcDate = utcFormat.format(createdAt);

            FileVersions fileVersionDetails = FileVersions.builder()
                    .uploaderName(version.getUploaderDisplayName())
                    .itemVersionNumber(version.getVersionNumber())
                    .itemVersionId(version.getVersionID())
                    .sha1Hash(version.getSha1())
                    .itemName(version.getName())
                    .modifiedAt(Long.parseLong(formattedUtcDate))
                    .build();

            fileVersionDetailsList.add(fileVersionDetails);

        }

        // Sort the fileVersionDetailsList based on version numbers
        fileVersionDetailsList.sort(Comparator.comparingLong(FileVersions::getItemVersionNumber));

        // If the file version list is empty
        if (fileVersionDetailsList.isEmpty()) {
            return new ApiResponse(true, Translator.toLocale("com.empty.fileVersion"), HttpStatus.OK, fileVersionDetailsList);
        }
        return new ApiResponse(true, "", HttpStatus.OK, fileVersionDetailsList);
    }


    public ApiResponse addExtAuditorEventLog(ExtAuditorEventLog extAuditorEventLog, String userEmail, DistributedTransaction transaction) {
        try {
            // Get current user
            User currentUser = userRepository.getByUserEmail(userEmail, transaction);

            // Check if current user exists
            if (currentUser == null) {
                throw new GenericException(Translator.toLocale("com.user.notFound"));
            }

            // Get audit set
            AuditSet auditSet = auditSetRepository.get(extAuditorEventLog.getAuditSetId(), transaction);

            // Check if audit set exists
            if (auditSet == null) {
                throw new NotFoundException(Translator.toLocale("com.auditSet.notFound"));
            }

            // Check user role
            if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(UserRoles.EXTERNAL_AUDITOR.toString()::equals)) {
                return new ApiResponse(false, Translator.toLocale("com.event.notAdd"), HttpStatus.BAD_REQUEST, null);
            }

            // Create UTC formatted date
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedUtcDate = utcFormat.format(new Date());
            String[] split = formattedUtcDate.split("-");
            String withoutMillis = split[1].substring(0, 6);

            Long eventDate = Long.parseLong(split[0] + withoutMillis);

            // Create auditor logs
            AuditorLogs auditorLogs = AuditorLogs.builder()
                    .auditSetId(extAuditorEventLog.getAuditSetId())
                    .itemId(extAuditorEventLog.getItemId())
                    .itemType(extAuditorEventLog.getItemType())
                    .userEmail(userEmail)
                    .eventDate(eventDate)
                    .customJsonEventDetails(null)
                    .build();

            // Create event type based on action type
            String actionType = extAuditorEventLog.getActionType().toUpperCase();
            if (actionType.equals(ActionType.ITEM_VIEW.toString()) || actionType.equals(ActionType.ITEM_PREVIEW.toString()) || actionType.equals(ActionType.ITEM_DOWNLOAD.toString())) {
                auditorLogs.setEventType(actionType);
            }

            // Create logs
            AuditorLogs isAuditorLogsExist = auditorLogsRepository.getAuditorLogsByPrimaryKey(extAuditorEventLog.getAuditSetId(), extAuditorEventLog.getItemId(), userEmail, eventDate, transaction);
            if (isAuditorLogsExist == null) {
                auditorLogsRepository.create(auditorLogs, transaction);
            }

            return new ApiResponse(true, "", HttpStatus.OK, auditorLogs);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse(false, Translator.toLocale("com.event.error"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    public ApiResponse getItemCollaborator(String itemId, String itemType, String currentUser, String auditSetId, DistributedTransaction transaction) {
        try {
            if (itemId == null) {
                throw new NotFoundException(Translator.toLocale("com.item.notFound"));
            }

            // Fetch role from token for authority
            List<String> collect = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());

            // Connect user with Box using API connection

            boolean needsRefresh = connection.needsRefresh();
            log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

            if (needsRefresh) {
                connection = boxUtility.getBoxEnterpriseConnection();
                log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
            }

            BoxAPIConnection boxAPIConnection;

            if (auditSetId != null) {
                boxAPIConnection = connection;
            } else if (collect.contains(UserRoles.AUDIT_ADMIN.toString()) || collect.contains(UserRoles.GENERAL_USER.toString())) {
                SubmitToken submitToken = userService.updateLatestToken(currentUser, transaction);
                String accessToken = submitToken.getAccessToken();
                boxAPIConnection = new BoxAPIConnection(accessToken);
            } else {
                boxAPIConnection = connection;
            }

            List<ItemCollaborator> itemCollaboratorList = new ArrayList<>();
            ItemOwner owner = null;

            if (itemType.equalsIgnoreCase(ItemType.FILE.toString())) {
                BoxFile file = new BoxFile(boxAPIConnection, itemId);
                Iterable<BoxCollaboration.Info> collaborations = file.getAllFileCollaborations();

                owner = new ItemOwner();

                for (BoxCollaboration.Info itemInfo : collaborations) {
                    if (!itemInfo.getAccessibleBy().getName().equalsIgnoreCase("Scalar-Box-Event-Log-Fetcher-App")) {
                        ItemCollaborator itemCollaborator1 = ItemCollaborator.builder()
                                .name(itemInfo.getAccessibleBy().getName())
                                .userEmail(itemInfo.getAccessibleBy().getLogin())
                                .userId(itemInfo.getAccessibleBy().getID())
                                .build();

                        itemCollaboratorList.add(itemCollaborator1);
                    }


                    owner.setOwnerName(itemInfo.getCreatedBy().getName());
                    owner.setOwnerEmail(itemInfo.getCreatedBy().getLogin());
                    owner.setOnwerId(itemInfo.getCreatedBy().getID());
                    owner.setItemCollaborators(itemCollaboratorList);
                }
            } else if (itemType.equalsIgnoreCase(ItemType.FOLDER.toString())) {
                BoxFolder folder = new BoxFolder(boxAPIConnection, itemId);
                Collection<BoxCollaboration.Info> collaborations = folder.getCollaborations();

                owner = new ItemOwner();

                for (BoxCollaboration.Info itemInfo : collaborations) {
                    if (!itemInfo.getAccessibleBy().getName().equalsIgnoreCase("Scalar-Box-Event-Log-Fetcher-App")) {
                        ItemCollaborator itemCollaborator = ItemCollaborator.builder()
                                .name(itemInfo.getAccessibleBy().getName())
                                .userEmail(itemInfo.getAccessibleBy().getLogin())
                                .userId(itemInfo.getAccessibleBy().getID())
                                .build();

                        itemCollaboratorList.add(itemCollaborator);
                    }


                    owner.setOwnerName(itemInfo.getCreatedBy().getName());
                    owner.setOwnerEmail(itemInfo.getCreatedBy().getLogin());
                    owner.setOnwerId(itemInfo.getCreatedBy().getID());
                    owner.setItemCollaborators(itemCollaboratorList);
                }
            }

            // Check if the owner is null or itemCollaboratorList is empty
            if (owner == null || itemCollaboratorList.isEmpty()) {
                // If the owner is null or itemCollaboratorList is empty, return a message indicating no collaborators found.
                return new ApiResponse(true, "", HttpStatus.OK, owner);
            } else {
                // If the owner and itemCollaboratorList are not empty, return the owner with the list of collaborators.
                List<ItemOwner> itemOwnerList = new ArrayList<>();
                itemOwnerList.add(owner);
                return new ApiResponse(true, "", HttpStatus.OK, itemOwnerList);
            }
        } catch (Exception e) {
            // Handle the exception here. You can log it or take appropriate actions.
            e.printStackTrace();
            return new ApiResponse(false, Translator.toLocale("com.itemCollaborator.error"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ItemStatus getItemStatus(Long itemId, DistributedTransaction transaction) {
        return itemStatusRepository.get(itemId, transaction);
    }

    public ItemStatus createItemStatus(ItemStatus itemStatus, DistributedTransaction transaction) {
        return itemStatusRepository.create(itemStatus, transaction);
    }

    public ItemsBySha1 getItemsBySha1(String itemSha1, Long itemId, Long itemVersionId, DistributedTransaction transaction){
        return itemsBySha1Repository.getItemsBySha1(itemSha1,itemId,itemVersionId,transaction);
    }

    public ItemsBySha1 create(ItemsBySha1 itemsBySha1, DistributedTransaction transaction){
    return itemsBySha1Repository.create(itemsBySha1,transaction);
    }
}