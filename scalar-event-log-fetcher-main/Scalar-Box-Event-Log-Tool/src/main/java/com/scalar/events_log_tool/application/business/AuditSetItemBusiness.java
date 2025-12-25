package com.scalar.events_log_tool.application.business;

import com.box.sdk.*;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.ItemType;
import com.scalar.events_log_tool.application.dto.AddItem;
import com.scalar.events_log_tool.application.dto.SubmitToken;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.AuditSetItem;
import com.scalar.events_log_tool.application.model.ItemsBySha1;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.service.AuditSetItemService;
import com.scalar.events_log_tool.application.service.FileService;
import com.scalar.events_log_tool.application.service.UserService;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuditSetItemBusiness {

    private final AuditSetItemService auditSetItemService;
    private final DistributedTransactionManager transactionManager;
    private final UserService userService;
    private final String boxServiceAccountEmail;
    private final BoxUtility boxUtility;
    private final FileService fileService;

    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection boxAPIConnection;


    public AuditSetItemBusiness(AuditSetItemService auditSetItemService, DistributedTransactionManager transactionManager, UserService userService, @Value("${box.server-authentication.service-acc}") String boxServiceAccountEmail, BoxUtility boxUtility, FileService fileService) {
        this.auditSetItemService = auditSetItemService;
        this.transactionManager = transactionManager;
        this.userService = userService;
        this.boxServiceAccountEmail = boxServiceAccountEmail;
        this.boxUtility = boxUtility;
        this.fileService = fileService;
    }

    public ApiResponse addItemToAuditSet(String auditSet, AddItem addItem, String currentUser) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            SubmitToken submitToken = userService.updateLatestToken(currentUser, transaction);
            if (submitToken == null) {
                log.info("Access & Refresh Token not found for the current user.");
                throw new GenericException("Something Went Wrong !!");
            }
            log.info("submit Token Object: {}", submitToken);
            AuditSetItem itemPresent = auditSetItemService.isAuditSetItemPresent(auditSet, addItem.getItemId(), transaction);

            BoxAPIConnection connection = new BoxAPIConnection(submitToken.getAccessToken());
            ApiResponse apiResponse = auditSetItemService.addItemToAuditSet(auditSet, addItem, currentUser, transaction, connection);
            if (itemPresent == null) {
                if (addItem.getItemType().equalsIgnoreCase(ItemType.FOLDER.toString())) {
                    BoxFolder boxFolder = new BoxFolder(connection, String.valueOf(addItem.getItemId()));
                    List<String> collaborators = boxFolder.getCollaborations()
                            .stream()
                            .map(e -> e.getAccessibleBy().getLogin())
                            .collect(Collectors.toList());


                    if (!collaborators.contains(boxServiceAccountEmail)) {
                        boxFolder.collaborate(boxServiceAccountEmail, BoxCollaboration.Role.EDITOR);
                    }

                } else if (addItem.getItemType().equalsIgnoreCase(ItemType.FILE.toString())) {
                    BoxFile boxFile = new BoxFile(connection, String.valueOf(addItem.getItemId()));

                    BoxResourceIterable<BoxCollaboration.Info> allFileCollaborations = boxFile.getAllFileCollaborations();

                    List<String> collaborators = new ArrayList<>();
                    for (BoxCollaboration.Info info : allFileCollaborations) {
                        collaborators.add(info.getAccessibleBy().getLogin());

                    }

                    if (!collaborators.contains(boxServiceAccountEmail)) {
                        boxFile.collaborate(boxServiceAccountEmail, BoxCollaboration.Role.EDITOR, false, false);
                    }

                }
            }


            boolean needsRefresh = boxAPIConnection.needsRefresh();
            log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

            if (needsRefresh) {
                this.boxAPIConnection = boxUtility.getBoxEnterpriseConnection();
                log.info("After Box Connection Refresh Status :" + boxAPIConnection.needsRefresh());
            }

            //opening this connection to this file in ItemsBysha Table to this file should exist in copies
            addItem.getItems().stream()
                    .filter(e -> e.getType().equalsIgnoreCase(ItemType.FILE.toString()))
                    .forEach(file -> {
                        log.info("item" + file.getId());
                        BoxFile fileObject;
                        BoxFile.Info info;
                        try {
                            fileObject = new BoxFile(boxAPIConnection, String.valueOf(file.getId()));
                            info = fileObject.getInfo(
                                    "name", "file_version", "id", "owned_by", "sha1", "created_at", "path_collection", "version_number"
                            );
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            return; // Skip further processing for this file only if an exception occurs
                        }
                        Long itemId = Long.parseLong(info.getID());
                        Long itemVersionId = Long.parseLong(info.getVersion().getID());

                        ItemsBySha1 sha1Item = fileService.getItemsBySha1(info.getSha1(), itemId, itemVersionId, transaction);
                        if (sha1Item == null) {

                            String path = info.getPathCollection().stream()
                                    .map(e -> e.getName())
                                    .collect(Collectors.joining("/"));

                            ItemsBySha1 itemsBySha1 = ItemsBySha1.builder()
                                    .itemName(info.getName())
                                    .itemId(itemId)
                                    .itemVersionNumber(Integer.parseInt(info.getVersionNumber()))
                                    .itemVersionId(itemVersionId)
                                    .sha1Hash(info.getSha1())
                                    .createdAt(Long.parseLong(GenericUtility.getUTCDateFormatWithoutMilliseconds().format(info.getCreatedAt())))
                                    .ownerByJson(GenericUtility.convertObjectToStringJson(new com.scalar.events_log_tool.application.dto.User(
                                            info.getOwnedBy().getType().name(),
                                            info.getOwnedBy().getID(),
                                            info.getOwnedBy().getName(),
                                            info.getOwnedBy().getLogin())))
                                    .path(path)
                                    .build();
                            log.info("ItemsBySha1 Object : {}", itemsBySha1);
                            fileService.create(itemsBySha1, transaction);

                        }
                    });

            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse getAuditSetItems(String auditSet) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetItemService.getAuditSetItems(auditSet, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(Translator.toLocale("com.unexpected.error"));
        }
    }


    public ApiResponse getAllowListFromAuditSet(String auditSetId, Long itemId) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetItemService.getAllowListFromAuditSet(auditSetId, itemId, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }


    }

    public ApiResponse getItemFromAuditSet(String auditSetId, Long itemId, Long subfolderId) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetItemService.getItemFromAuditSet(auditSetId, itemId, subfolderId, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

}
