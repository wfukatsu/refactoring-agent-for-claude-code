package com.scalar.events_log_tool.application.business;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.dto.CreateAuditGroup;
import com.scalar.events_log_tool.application.dto.UpdateAuditGroup;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.service.AuditGroupService;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditGroupBusiness {
    @Autowired
    private DistributedTransactionManager transactionManager;
    @Autowired
    private AuditGroupService auditGroupService;


    /**
     *  Description: This API creates audit group. Only audit admin create audit group
     *
     */
    public ApiResponse createAuditGroup(CreateAuditGroup createAuditGroup, String currentEmail) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditGroupService.createAuditGroup(createAuditGroup, currentEmail, transaction);
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


    public ApiResponse getListOfAuditGroup(String currentEmail) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditGroupService.getListOfAuditGroup(currentEmail, transaction);
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


    public ApiResponse updateAuditGroup(String auditGroupId, UpdateAuditGroup updateAuditGroup) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditGroupService.updateAuditGroup(auditGroupId, updateAuditGroup, transaction);
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

    public ApiResponse deleteAuditGroup(String auditGroupId) {
        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditGroupService.deleteAuditGroup(auditGroupId, transaction);
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

    public ApiResponse getListOfAuditGroupMembers(String auditGroupId) {
        DistributedTransaction transaction = null;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = auditGroupService.getListOfAuditGroupMembers(auditGroupId, transaction);
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

