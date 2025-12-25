package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.Events;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class EventsRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "events";
    private static final String YYYY_MM_DD = "yyyy_mm_dd";

    private static final String TIMESTAMP = "timestamp";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_EMAIL = "user_email";
    private static final String ASSET_ID = "asset_id";
    private static final String ASSET_AGE = "asset_age";
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_VERSION = "item_version_id";
    private static final String EVENT_ID = "event_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String SHA1_HASH = "sha1_hash";
    private static final String EVENT_CREATED_AT = "event_created_at";
    private static final String EVENT_OCCURRED_ON = "event_occured_on";
    private static final String PARENT_FOLDER_ID = "parent_folder_id";
    private static final String SOURCE_JSON = "source_json";


    public Events create(Events events, DistributedTransaction transaction) {

        Key partitionKey = Key.ofText(YYYY_MM_DD, events.getYyyyMMdd());

        Key clusteringKey = Key.of(TIMESTAMP, events.getTimestamp(), USER_ID, events.getUserId(), EVENT_ID, events.getEventId());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(partitionKey)
                    .clusteringKey(clusteringKey)
                    .textValue(EVENT_TYPE, events.getEventType())
                    .textValue(USER_NAME, events.getUserName())
                    .textValue(USER_EMAIL, events.getUserEmail())
                    .textValue(ASSET_ID, events.getAssetId())
                    .bigIntValue(ITEM_ID, events.getItemId())
                    .intValue(ASSET_AGE, events.getAssetAge())
                    .bigIntValue(ITEM_VERSION, events.getItemVersionId())
                    .textValue(SHA1_HASH, events.getSha1Hash())
                    .bigIntValue(EVENT_CREATED_AT, events.getCreatedAt())
                    .textValue(EVENT_OCCURRED_ON, events.getEventOccuredOn())
                    .bigIntValue(PARENT_FOLDER_ID, events.getParentFolderId())
                    .textValue(SOURCE_JSON, events.getSourceJson())
                    .build();
            transaction.put(put);
            return events;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Events> getAllEvents(DistributedTransaction transaction) throws CrudException {

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .all()
                .build();

        List<Result> results = transaction.scan(scan);
        return results.stream().map(result -> {

            return Events.builder()
                    .yyyyMMdd(result.getText(YYYY_MM_DD))
                    .timestamp(result.getText(TIMESTAMP))
                    .eventType(result.getText(EVENT_TYPE))
                    .eventId(result.getText(EVENT_ID))
                    .userId(result.getBigInt(USER_ID))
                    .userName(result.getText(USER_NAME))
                    .userEmail(result.getText(USER_EMAIL))
                    .assetId(result.getText(ASSET_ID))
                    .itemId(result.getBigInt(ITEM_ID))
                    .assetAge(result.getInt(ASSET_AGE))
                    .itemVersionId(result.getBigInt(ITEM_VERSION))
                    .sha1Hash(result.getText(SHA1_HASH))
                    .createdAt(result.getBigInt(EVENT_CREATED_AT))
                    .eventOccuredOn(result.getText(EVENT_OCCURRED_ON))
                    .parentFolderId(result.getBigInt(PARENT_FOLDER_ID))
                    .sourceJson(result.getText(SOURCE_JSON))
                    .build();

        }).collect(Collectors.toList());

    }


    public Events getEventByEventId(DistributedTransaction transaction, String eventId) {

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .indexKey(Key.ofText(EVENT_ID, eventId))
                .build();


        try {
            Optional<Result> optionalResult = transaction.get(get);
            if (optionalResult.isPresent()) {
                Result result = optionalResult.get();
                return Events.builder()
                        .yyyyMMdd(result.getText(YYYY_MM_DD))
                        .timestamp(result.getText(TIMESTAMP))
                        .eventType(result.getText(EVENT_TYPE))
                        .eventId(result.getText(EVENT_ID))
                        .userId(result.getBigInt(USER_ID))
                        .userName(result.getText(USER_NAME))
                        .userEmail(result.getText(USER_EMAIL))
                        .assetId(result.getText(ASSET_ID))
                        .itemId(result.getBigInt(ITEM_ID))
                        .assetAge(result.getInt(ASSET_AGE))
                        .itemVersionId(result.getBigInt(ITEM_VERSION))
                        .sha1Hash(result.getText(SHA1_HASH))
                        .createdAt(result.getBigInt(EVENT_CREATED_AT))
                        .eventOccuredOn(result.getText(EVENT_OCCURRED_ON))
                        .parentFolderId(result.getBigInt(PARENT_FOLDER_ID))
                        .sourceJson(result.getText(SOURCE_JSON))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Events> getEventsByDate(DistributedTransaction transaction, String date, String startTime, String endTime) {

        Key partitionKey = Key.ofText(YYYY_MM_DD, date);
        Key startClusteringKey = Key.ofText(TIMESTAMP, startTime);
        Key endClusteringKey = Key.ofText(TIMESTAMP, endTime);

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(partitionKey)
                .start(startClusteringKey, true)
                .end(endClusteringKey, true)
                .build();

        try {
            List<Result> results = transaction.scan(scan);
            return results.stream().map(result -> {

                return Events.builder()
                        .yyyyMMdd(result.getText(YYYY_MM_DD))
                        .timestamp(result.getText(TIMESTAMP))
                        .eventType(result.getText(EVENT_TYPE))
                        .eventId(result.getText(EVENT_ID))
                        .userId(result.getBigInt(USER_ID))
                        .userName(result.getText(USER_NAME))
                        .userEmail(result.getText(USER_EMAIL))
                        .assetId(result.getText(ASSET_ID))
                        .itemId(result.getBigInt(ITEM_ID))
                        .assetAge(result.getInt(ASSET_AGE))
                        .itemVersionId(result.getBigInt(ITEM_VERSION))
                        .sha1Hash(result.getText(SHA1_HASH))
                        .createdAt(result.getBigInt(EVENT_CREATED_AT))
                        .eventOccuredOn(result.getText(EVENT_OCCURRED_ON))
                        .parentFolderId(result.getBigInt(PARENT_FOLDER_ID))
                        .sourceJson(result.getText(SOURCE_JSON))
                        .build();

            }).collect(Collectors.toList());

        } catch (CrudException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    public Events getEventByPTAndCK(DistributedTransaction transaction, String yyyyMMdd, String timeStamp, Long userId, String eventId) {

        Key partitionKey = Key.ofText(YYYY_MM_DD, yyyyMMdd);

        Key clusteringKey = Key.of(TIMESTAMP, timeStamp, USER_ID, userId, EVENT_ID, eventId);

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(partitionKey)
                .clusteringKey(clusteringKey)
                .build();

        try {
            Optional<Result> optionalResult = transaction.get(get);
            if (optionalResult.isPresent()) {
                Result result = optionalResult.get();
                return Events.builder()
                        .yyyyMMdd(result.getText(YYYY_MM_DD))
                        .timestamp(result.getText(TIMESTAMP))
                        .eventType(result.getText(EVENT_TYPE))
                        .eventId(result.getText(EVENT_ID))
                        .userId(result.getBigInt(USER_ID))
                        .userName(result.getText(USER_NAME))
                        .userEmail(result.getText(USER_EMAIL))
                        .assetId(result.getText(ASSET_ID))
                        .itemId(result.getBigInt(ITEM_ID))
                        .assetAge(result.getInt(ASSET_AGE))
                        .itemVersionId(result.getBigInt(ITEM_VERSION))
                        .sha1Hash(result.getText(SHA1_HASH))
                        .createdAt(result.getBigInt(EVENT_CREATED_AT))
                        .eventOccuredOn(result.getText(EVENT_OCCURRED_ON))
                        .parentFolderId(result.getBigInt(PARENT_FOLDER_ID))
                        .sourceJson(result.getText(SOURCE_JSON))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }


}

