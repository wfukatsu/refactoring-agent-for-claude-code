package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.AuditorLogs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j

@Repository
public class AuditorLogsRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "auditor_logs";
    private static final String ID = "audit_set_id";
    private static final String ITEM_ID = "item_id";
    private static final String USER_EMAIL = "user_email";
    private static final String EVENT_TYPE = "event_type";
    private static final String EVENT_DATE = "event_date";
    private static final String CUSTOM_JSON_EVENTDETAILS = "custom_json_eventDetails";
    private static final String ITEM_TYPE = "item_type";


    public AuditorLogs create(AuditorLogs auditorLogs, DistributedTransaction transaction) {
        Key clusteringKey = Key.of(ITEM_ID, auditorLogs.getItemId(), USER_EMAIL, auditorLogs.getUserEmail(), EVENT_DATE, auditorLogs.getEventDate());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(ID, auditorLogs.getAuditSetId()))
                    .clusteringKey(clusteringKey)
                    .textValue(EVENT_TYPE, auditorLogs.getEventType())
                    .textValue(CUSTOM_JSON_EVENTDETAILS, auditorLogs.getCustomJsonEventDetails())
                    .textValue(ITEM_TYPE, auditorLogs.getItemType())
                    .build();
            transaction.put(put);
            return auditorLogs;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<AuditorLogs> getExtAuditorAccessLog(String auditSetId, Long itemId, DistributedTransaction transaction) throws CrudException {
        Key key = Key.ofText(ID, auditSetId);

        Key clusteringKey = Key.ofBigInt(ITEM_ID, itemId);

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .start(clusteringKey, true)
                .end(clusteringKey, true)
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditorLogs> auditorLogsList = new ArrayList<>();
        for (Result result : results) {
            AuditorLogs auditorLogs = AuditorLogs.builder()
                    .auditSetId(result.getText(ID))
                    .itemId(result.getBigInt(ITEM_ID))
                    .userEmail(result.getText(USER_EMAIL))
                    .eventType(result.getText(EVENT_TYPE))
                    .eventDate(result.getBigInt(EVENT_DATE))
                    .customJsonEventDetails(result.getText(CUSTOM_JSON_EVENTDETAILS))
                    .itemType(result.getText(ITEM_TYPE))
                    .build();
            auditorLogsList.add(auditorLogs);
        }
        return auditorLogsList;
    }

    public List<AuditorLogs> getListOfAuditorLog(String userEmail, DistributedTransaction transaction) throws CrudException {


        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .indexKey(Key.ofText(USER_EMAIL, userEmail))
                .build();
        List<Result> results = transaction.scan(scan);
        List<AuditorLogs> auditorLogsList = new ArrayList<>();
        for (Result result : results) {
            AuditorLogs auditorLogs = AuditorLogs.builder()
                    .auditSetId(result.getText(ID))
                    .itemId(result.getBigInt(ITEM_ID))
                    .userEmail(result.getText(USER_EMAIL))
                    .eventType(result.getText(EVENT_TYPE))
                    .eventDate(result.getBigInt(EVENT_DATE))
                    .customJsonEventDetails(result.getText(CUSTOM_JSON_EVENTDETAILS))
                    .itemType(result.getText(ITEM_TYPE))
                    .build();
            auditorLogsList.add(auditorLogs);
        }
        return auditorLogsList;
    }


    public AuditorLogs getAuditorLogsByPrimaryKey(String auditSetId, Long itemId, String userEmail, Long eventDate, DistributedTransaction transaction) {

        Optional<Result> result;
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(ID, auditSetId))
                .clusteringKey(Key.of(ITEM_ID, itemId, USER_EMAIL, userEmail, EVENT_DATE, eventDate))
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return AuditorLogs.builder()
                        .auditSetId(resultObject.getText(ID))
                        .itemId(resultObject.getBigInt(ITEM_ID))
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .eventType(resultObject.getText(EVENT_TYPE))
                        .eventDate(resultObject.getBigInt(EVENT_DATE))
                        .customJsonEventDetails(resultObject.getText(CUSTOM_JSON_EVENTDETAILS))
                        .itemType(resultObject.getText(ITEM_TYPE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public AuditorLogs createAnddeleteByUserMail(AuditorLogs auditorLogs, String newUserMail, DistributedTransaction transaction) {

        Key clusteringKeyToPut = Key.of(ITEM_ID, auditorLogs.getItemId(), USER_EMAIL, newUserMail, EVENT_DATE, auditorLogs.getEventDate());
        Key clusteringKeyToDelete = Key.of(ITEM_ID, auditorLogs.getItemId(), USER_EMAIL, auditorLogs.getUserEmail(), EVENT_DATE, auditorLogs.getEventDate());

        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(ID, auditorLogs.getAuditSetId()))
                    .clusteringKey(clusteringKeyToPut)
                    .textValue(EVENT_TYPE, auditorLogs.getEventType())
                    .textValue(CUSTOM_JSON_EVENTDETAILS, auditorLogs.getCustomJsonEventDetails())
                    .textValue(ITEM_TYPE, auditorLogs.getItemType())
                    .build();

            Delete delete = Delete.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(ID, auditorLogs.getAuditSetId()))
                    .clusteringKey(clusteringKeyToDelete)
                    .build();

            transaction.mutate(Arrays.asList(put, delete));
            return auditorLogs;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }
}
