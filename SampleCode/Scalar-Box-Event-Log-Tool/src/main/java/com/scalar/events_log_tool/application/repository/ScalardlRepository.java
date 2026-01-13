package com.scalar.events_log_tool.application.repository;


import com.fasterxml.jackson.databind.JsonNode;
import com.scalar.dl.client.exception.ClientException;
import com.scalar.dl.client.service.ClientService;
import com.scalar.dl.ledger.model.ContractExecutionResult;
import com.scalar.dl.ledger.model.LedgerValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ScalardlRepository {

    @Autowired
    private ClientService clientService;

    public ContractExecutionResult callContract(String contactId, JsonNode argument) {
        log.info("Executing Contract .." + contactId);
        ContractExecutionResult result = clientService.executeContract(contactId, argument);
        logResponse("Executing Contract response", result);
        return result;
    }

    public ContractExecutionResult callContractAndFunction(String contactId, JsonNode argument, String functionId,
                                                           JsonNode functionArgs) {

        log.info("Executing Contract .." + contactId);
        ContractExecutionResult result = clientService.executeContract(contactId, argument, functionId, functionArgs);
        logResponse("Executing Contract response", result);
        return result;
    }

    public String validateLedger(String assetId) {
        LedgerValidationResult assetResponse;
        try {
            assetResponse = clientService.validateLedger(assetId);
        } catch (ClientException e) {
            log.info("CODE:" + e.getStatusCode() + " Message:" + e.getMessage());
            return e.getStatusCode().toString();
        } catch (Exception ex) {
            log.info("Exception Message :" + ex.getMessage());
            return "INTERNAL_SERVER_ERROR";
        }
        if ("OK".equals(assetResponse.getCode().toString())) {
            logResponseValidator(assetResponse);
        }
        return assetResponse.getCode().toString();
    }

    private void logResponse(String header, ContractExecutionResult result) {
        log.info(
                header
                        + ": ("
                        + (result.getContractResult().isPresent() ? result.getContractResult().get() : "{}")
                        + ")");

        log.info(
                "FunctionResult: ("
                        + (result.getFunctionResult().isPresent() ? result.getFunctionResult().get() : "{}")
                        + ")");
    }

    private void logResponseValidator(LedgerValidationResult result) {

        log.info("CODE:" + result.getCode());
        log.info(
                "Ledger : ("
                        + (result.getLedgerProof().isPresent() ? result.getLedgerProof().get().toString() : "{}")
                        + ")");


        log.info(
                "Auditor: ("
                        + (result.getAuditorProof().isPresent() ? result.getAuditorProof().get().toString() : "{}")
                        + ")");
    }


}