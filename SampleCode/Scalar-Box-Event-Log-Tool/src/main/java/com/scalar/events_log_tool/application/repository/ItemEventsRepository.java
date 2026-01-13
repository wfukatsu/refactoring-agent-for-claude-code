package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.ItemEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemEventsRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "item_events";
    private static final String EVENT_DATE = "event_date";
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_VERSION = "item_version_id";
    private static final String EVENT_ID = "event_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String EVENT_JSON_DATA = "event_json_data";


    public ItemEvents create(ItemEvents itemEvents, DistributedTransaction transaction) {


        Key partitionKey = Key.ofBigInt(ITEM_ID, itemEvents.getItemId());

        Key clusteringKey = Key.of(EVENT_DATE, itemEvents.getEventDate(), ITEM_VERSION, itemEvents.getItemVersionId());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(partitionKey)
                    .clusteringKey(clusteringKey)
                    .textValue(EVENT_TYPE, itemEvents.getEventType())
                    .textValue(EVENT_ID, itemEvents.getEventId())
                    .textValue(EVENT_JSON_DATA, itemEvents.getEventJsonData())
                    .build();
            transaction.put(put);
            return itemEvents;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<ItemEvents> getEventsByItem(Long fileId, Long startDate, Long endDate, DistributedTransaction transaction) {

        Key partitionKey = Key.ofBigInt(ITEM_ID, fileId);

        Key startClusteringKey = Key.ofBigInt(EVENT_DATE, startDate);

        Key endClusteringKey = Key.ofBigInt(EVENT_DATE, endDate);

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

                return ItemEvents.builder()
                        .eventType(result.getText(EVENT_TYPE))
                        .eventId(result.getText(EVENT_ID))
                        .itemId(result.getBigInt(ITEM_ID))
                        .itemVersionId(result.getBigInt(ITEM_VERSION))
                        .eventDate(result.getBigInt(EVENT_DATE))
                        .eventJsonData(result.getText(EVENT_JSON_DATA))
                        .build();

            }).collect(Collectors.toList());

        } catch (CrudException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();


    }


    public ItemEvents getItemEvent(Long itemId, Long eventDate, Long itemVersionId, DistributedTransaction transaction) {

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofBigInt(ITEM_ID, itemId))
                .clusteringKey(Key.of(EVENT_DATE, eventDate, ITEM_VERSION, itemVersionId))
                .build();

        try {
            Optional<Result> resultOptional = transaction.get(get);
            if (resultOptional.isPresent()) {
                Result result = resultOptional.get();
                return ItemEvents.builder()
                        .eventType(result.getText(EVENT_TYPE))
                        .eventId(result.getText(EVENT_ID))
                        .itemId(result.getBigInt(ITEM_ID))
                        .itemVersionId(result.getBigInt(ITEM_VERSION))
                        .eventDate(result.getBigInt(EVENT_DATE))
                        .eventJsonData(result.getText(EVENT_JSON_DATA))
                        .build();
            }

        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;


    }
}
