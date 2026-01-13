package com.scalar.events_log_tool.application.business;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.ErrorLogMessages;
import com.scalar.events_log_tool.application.constant.ItemType;
import com.scalar.events_log_tool.application.constant.TamperingStatusType;
import com.scalar.events_log_tool.application.dto.ExtAuditorEventLog;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.ItemStatus;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.service.AssetService;
import com.scalar.events_log_tool.application.service.FileService;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Service
public class FileBusiness {

    private final FileService fileService;

    private final DistributedTransactionManager transactionManager;

    private final AssetService assetService;

    public FileBusiness(FileService fileService, DistributedTransactionManager transactionManager, AssetService assetService) {
        this.fileService = fileService;
        this.transactionManager = transactionManager;
        this.assetService = assetService;
    }

    public ApiResponse getFileCopies(String sha1Hash, Long itemId) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = fileService.getFileCopies(sha1Hash, itemId, transaction);
            transaction.commit();
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }


    /**
     * Description: This API get file details using fileId.
     */
    public ApiResponse getFileDetails(Long itemId, String auditSetId, String userEmail) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = fileService.getFileDetails(itemId, auditSetId, userEmail, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    /**
     * Description: This API get folder details using folderId.
     */
    public ApiResponse getFolderDetails(Long folderId, String auditSetId, String userEmail) {
        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = fileService.getFolderDetails(folderId, auditSetId, userEmail, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse getFileVersions(String fileId, String currentEmail, String auditSetId) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = fileService.getFileVersions(fileId, currentEmail, transaction, auditSetId);
            transaction.commit();
            return apiResponse;
        } catch (TransactionException | ParseException e) {

            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }


    /**
     * Description: This API adds external auditor event log in auditor table when external auditor will do any action on audit set.
     */
    public ApiResponse addExtAuditorEventLog(ExtAuditorEventLog extAuditorEventLog, String userEmail) {
        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = fileService.addExtAuditorEventLog(extAuditorEventLog, userEmail, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    public ApiResponse getItemCollaborator(String itemId, String itemType, String currentUser, String auditSetId) {
        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = fileService.getItemCollaborator(itemId, itemType, currentUser, auditSetId, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }


    public ApiResponse checkTamperingStatus(String fileId) {
        int retryCount = 0;
        while (retryCount < 3) {
            // Start Transaction
            DistributedTransaction transaction;
            try {
                transaction = transactionManager.start();
            } catch (TransactionException e) {
                throw new GenericException(Translator.toLocale("com.unable.transaction"));
            }

            try {
                ItemStatus itemStatus = fileService.getItemStatus(Long.parseLong(fileId), transaction);

                SimpleDateFormat utcFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();
                String date = utcFormat.format(new Date());

                if (itemStatus == null) {
                    itemStatus = ItemStatus.builder()
                            .status(TamperingStatusType.NOT_TAMPERED.toString())
                            .itemId(Long.parseLong(fileId))
                            .lastValidatedAt(date)
                            .itemType(ItemType.FILE.getType())
                            .monitoredStatus(TamperingStatusType.MONITORED.toString())
                            .listOfAuditsetJson(GenericUtility.convertObjectToStringJson(new ArrayList<>()))
                            .build();
                }

                String tamperingStatus = assetService.getTamperingStatus(fileId);
                itemStatus.setStatus(tamperingStatus);
                itemStatus.setLastValidatedAt(date);

                log.info("Item Status Object: {}", itemStatus);

                fileService.createItemStatus(itemStatus, transaction);
                transaction.commit();

                return ApiResponse.builder()
                        .data(Collections.singletonMap("tamperingStatus", tamperingStatus))
                        .message("")
                        .httpStatus(HttpStatus.OK)
                        .status(true)
                        .build();

            } catch (TransactionException e) {
                log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
                try {
                    transaction.rollback();
                } catch (RollbackException ex) {
                    ex.printStackTrace();
                }
                // Increment retry count
                retryCount++;
                if (retryCount >= 3) {
                    throw new GenericException("Something Went Wrong !!");
                }
            }
        }
        throw new GenericException("Something Went Wrong !!");
    }


}