package com.scalar.events_log_tool.application.business;


import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxCollaboration;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxResourceIterable;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.ErrorLogMessages;
import com.scalar.events_log_tool.application.dto.AuditSetInputDto;
import com.scalar.events_log_tool.application.dto.SubmitToken;
import com.scalar.events_log_tool.application.dto.UpdateAuditSet;
import com.scalar.events_log_tool.application.dto.UpdateAuditSetItem;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.AuditSetItem;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.AuditSetLists;
import com.scalar.events_log_tool.application.service.AuditSetItemService;
import com.scalar.events_log_tool.application.service.AuditSetService;
import com.scalar.events_log_tool.application.service.UserService;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuditSetBusiness {
    private final DistributedTransactionManager transactionManager;
    private final AuditSetService auditSetService;
    private final AuditSetItemService auditSetItemService;
    private final UserService userService;

    public AuditSetBusiness(DistributedTransactionManager transactionManager, AuditSetService auditSetService, AuditSetItemService auditSetItemService, UserService userService) {
        this.transactionManager = transactionManager;
        this.auditSetService = auditSetService;
        this.auditSetItemService = auditSetItemService;
        this.userService = userService;
    }

    /**
     * Description: This API creates audit set. Only audit admin create audit set
     */
    public ApiResponse createAuditSet(AuditSetInputDto auditSetInputDto, String userEmail) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetService.createAuditSet(auditSetInputDto, userEmail, transaction);
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
     * Description: This API delete audit set.only audit admin delete audit set
     */
    public ApiResponse deleteAuditSet(String auditSetId, String userEmail) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetService.deleteAuditSet(auditSetId, userEmail, transaction);
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


    public ApiResponse getMyAuditSetList(String currentEmail) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetService.getMyAuditSetList(currentEmail, transaction);
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

    public ApiResponse viewExtAuditorEventLog(String auditSetId, Long itemId, String userEmail) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetService.viewExtAuditorEventLog(auditSetId, itemId, userEmail, transaction);
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

    public ApiResponse updateAuditSetInfo(String auditSetId, UpdateAuditSet updateAuditSet, String currentEmail) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetService.updateAuditSetInfo(auditSetId, updateAuditSet, currentEmail, transaction);
            transaction.commit();
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

    public ApiResponse verifyAuditSet(String auditSetId) {

        int retryCount = 0;
        while (retryCount < 3) {
            DistributedTransaction transaction;
            try {
                transaction = transactionManager.start();
            } catch (TransactionException te) {
                throw new GenericException(Translator.toLocale("com.unable.transaction"));
            }

            try {
                ApiResponse apiResponse = auditSetItemService.verifyAuditSet(auditSetId, transaction);
                transaction.commit();
                return apiResponse;
            } catch (TransactionException e) {
                log.info("Error while Committing transaction ..");
                try {
                    transaction.rollback();
                } catch (RollbackException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
            // Increment retry count
            retryCount++;
            if (retryCount >= 3) {
                throw new GenericException("Something Went Wrong !!");
            }
        }
        throw new GenericException("Something Went Wrong !!");

    }

    public ApiResponse getMyAuditSetListForItemId(Long itemId) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetService.getMyAuditSetList(SecurityContextHolder.getContext().getAuthentication().getName(), transaction);
            List<AuditSetLists> auditSetLists = (List<AuditSetLists>) apiResponse.getData();
            auditSetLists.forEach(auditSet -> {
                        auditSet.setIsItemIdAdded(auditSetService.isItemExistInAuditSet(itemId, auditSet.getAuditSetId(), transaction));
                    }
            );
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

    public ApiResponse updateAuditSetsForItemId(Long itemId, UpdateAuditSetItem auditSetItem) {


        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {

            SubmitToken submitToken = userService.updateLatestToken(SecurityContextHolder.getContext().getAuthentication().getName(), transaction);
            if (submitToken == null) {
                throw new GenericException("Something Went Wrong !!");
            }
            log.info("submit Token Object: {}", submitToken);

            BoxAPIConnection boxApiConnection = new BoxAPIConnection(submitToken.getAccessToken());

            ApiResponse apiResponse = auditSetItemService.updateAuditSetsForItemId(itemId,auditSetItem,boxApiConnection, transaction);

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
}
