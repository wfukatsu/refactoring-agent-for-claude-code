package com.scalar.events_log_tool.application.business;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.ErrorLogMessages;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.service.FolderService;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FolderBusiness {


    private final DistributedTransactionManager transactionManager;

    private final FolderService folderService;

    public FolderBusiness(DistributedTransactionManager transactionManager, FolderService folderService) {
        this.transactionManager = transactionManager;
        this.folderService = folderService;
    }

    public ApiResponse getItemList(String folderId, String currentEmail) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = folderService.getItemList(folderId, currentEmail, transaction);
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
