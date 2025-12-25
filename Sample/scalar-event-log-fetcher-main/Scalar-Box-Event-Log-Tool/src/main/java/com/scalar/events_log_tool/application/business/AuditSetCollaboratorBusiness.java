package com.scalar.events_log_tool.application.business;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.dto.AuditSetCollab;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.service.AuditSetCollaboratorService;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditSetCollaboratorBusiness {

    private final AuditSetCollaboratorService auditSetCollaboratorService;

    private final DistributedTransactionManager transactionManager;

    public AuditSetCollaboratorBusiness(AuditSetCollaboratorService auditSetCollaboratorService, DistributedTransactionManager transactionManager) {
        this.auditSetCollaboratorService = auditSetCollaboratorService;
        this.transactionManager = transactionManager;
    }



    public ApiResponse changeAuditSetOwner(String auditSetId, String newOwnerId, String currentUser) {
        int retryCount = 0;
        while (retryCount < 3) {
            DistributedTransaction transaction;
            try {
                transaction = transactionManager.start();
                log.info("Transaction Started");
            } catch (TransactionException te) {
                throw new GenericException(Translator.toLocale("com.unable.transaction"));
            }

            try {
                ApiResponse apiResponse = auditSetCollaboratorService.changeAuditSetOwner(auditSetId, newOwnerId, currentUser, transaction);
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
                // Increment retry count
                retryCount++;
                if (retryCount >= 3) {
                    throw new GenericException(Translator.toLocale("com.something.wrong"));
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new GenericException(e.getMessage());
            }
        }

        if (retryCount >= 3) {
            throw new GenericException(Translator.toLocale("com.something.Wrong"));
        }

        throw new GenericException(Translator.toLocale("com.unexpected.error"));
        // In case the loop somehow exits without returning
    }


    public ApiResponse getCollaboratorForAuditSet(String auditSetId) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetCollaboratorService.getCollaboratorForAuditSet(auditSetId, transaction);
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

    public ApiResponse markIsFavouriteAuditSet(String auditSetId, Boolean status, String name) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetCollaboratorService.markIsFavouriteAuditSet(auditSetId, status, name, transaction);
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

    public ApiResponse getGeneralUserList() {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetCollaboratorService.getGeneralUserList(transaction);
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

    public ApiResponse getAuditAdminList() {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditSetCollaboratorService.getAuditAdminList(transaction);
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
