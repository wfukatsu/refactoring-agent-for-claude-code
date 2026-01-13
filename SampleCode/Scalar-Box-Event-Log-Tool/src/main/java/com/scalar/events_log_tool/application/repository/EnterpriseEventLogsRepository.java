package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.EnterpriseEventLogs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class EnterpriseEventLogsRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "enterprise_event_logs";
    private static final String ID = "event_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String EVENT_CREATED_USER_ID = "event_created_user_id";
    private static final String EVENT_CREATED_USER_NAME = "event_created_user_name";
    private static final String EVENT_CREATED_AT = "event_created_at";
    private static final String EVENT_OCCURED_ON = "event_occured_on";
    private static final String HASH = "hash";
    private static final String ITEM_ID = "item_id";
    private static final String PATH = "path";
    private static final String USER_ID = "user_id";

    public EnterpriseEventLogs create(EnterpriseEventLogs eventLogs, DistributedTransaction transaction) {

        Key key = Key.ofText(ID, eventLogs.getEventId());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .textValue(EVENT_TYPE, eventLogs.getEventType())
                    .textValue(EVENT_CREATED_USER_ID, eventLogs.getEventCreatedUserId())
                    .textValue(EVENT_CREATED_USER_NAME, eventLogs.getEventCreatedUserName())
                    .bigIntValue(EVENT_CREATED_AT, eventLogs.getCreatedAt())
                    .textValue(EVENT_OCCURED_ON, eventLogs.getEventOccuredOn())
                    .textValue(ITEM_ID, eventLogs.getItemId())
                    .textValue(HASH, eventLogs.getHash())
                    .textValue(PATH, eventLogs.getPath())
                    .textValue(USER_ID, eventLogs.getUserId())
                    .build();
            transaction.put(put);
            return eventLogs;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<EnterpriseEventLogs> getAllEvents(DistributedTransaction transaction) {

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .all()
                .build();

        List<Result> results = new ArrayList<>();
        try {
            results = transaction.scan(scan);
        } catch (CrudException e) {
            e.printStackTrace();
        }
        List<EnterpriseEventLogs> logsList = new ArrayList<>();
        for (Result result : results) {
            EnterpriseEventLogs eventLogs = new EnterpriseEventLogs();
            eventLogs.setEventId(result.getText(ID));
            eventLogs.setEventType(result.getText(EVENT_TYPE));
            eventLogs.setEventCreatedUserId(result.getText(EVENT_CREATED_USER_ID));
            eventLogs.setEventCreatedUserName(result.getText(EVENT_CREATED_USER_NAME));
            eventLogs.setEventOccuredOn(result.getText(EVENT_OCCURED_ON));
            eventLogs.setCreatedAt(result.getBigInt(EVENT_CREATED_AT));
            eventLogs.setUserId(result.getText(USER_ID));
            eventLogs.setItemId(result.getText(ITEM_ID));
            eventLogs.setHash(result.getText(HASH));
            eventLogs.setPath(result.getText(PATH));

            logsList.add(eventLogs);
        }
        return logsList;
    }


    public EnterpriseEventLogs getEventById(DistributedTransaction transaction, String eventId) {

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(ID, eventId))
                .build();


        try {
            Optional<Result> result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return EnterpriseEventLogs.builder()
                        .eventId(resultObject.getText(ID))
                        .itemId(resultObject.getText(ITEM_ID))
                        .eventOccuredOn(resultObject.getText(EVENT_OCCURED_ON))
                        .userId(resultObject.getText(USER_ID))
                        .path(resultObject.getText(PATH))
                        .eventCreatedUserName(resultObject.getText(EVENT_CREATED_USER_NAME))
                        .eventCreatedUserId(resultObject.getText(EVENT_CREATED_USER_ID))
                        .hash(resultObject.getText(HASH))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }
}
