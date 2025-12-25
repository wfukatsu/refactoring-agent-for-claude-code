package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.ItemsBySha1;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemsBySha1Repository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "items_by_sha1";
    private static final String SHA1_HASH = "sha1_hash";
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_VERSION_ID = "item_version_id";
    private static final String ITEM_VERSION_NUMBER = "item_version_number";
    private static final String ITEM_NAME = "item_name";
    private static final String OWNER_BY_JSON = "owner_by_json";
    private static final String PATH = "path";
    private static final String CREATED_AT = "created_at";


    public List<ItemsBySha1> getItemsBySha1(String Sha1Hash, DistributedTransaction transaction) throws TransactionException {

        Key key = Key.ofText(SHA1_HASH, Sha1Hash);

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .build();

        List<Result> results = transaction.scan(scan);
        List<ItemsBySha1> items = new ArrayList<>();
        for (Result result : results) {
            ItemsBySha1 itemsBySha1 = ItemsBySha1.builder()
                    .sha1Hash(result.getText(SHA1_HASH))
                    .itemId(result.getBigInt(ITEM_ID))
                    .itemVersionId(result.getBigInt(ITEM_VERSION_ID))
                    .itemVersionNumber(result.getInt(ITEM_VERSION_NUMBER))
                    .itemName(result.getText(ITEM_NAME))
                    .ownerByJson(result.getText(OWNER_BY_JSON))
                    .path(result.getText(PATH))
                    .createdAt(result.getBigInt(CREATED_AT))
                    .build();
            items.add(itemsBySha1);
        }
        return items;
    }


    public ItemsBySha1 create(ItemsBySha1 itemsBySha1, DistributedTransaction transaction) {

        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(SHA1_HASH, itemsBySha1.getSha1Hash()))
                    .clusteringKey(Key.of(ITEM_ID, itemsBySha1.getItemId(), ITEM_VERSION_ID, itemsBySha1.getItemVersionId()))
                    .intValue(ITEM_VERSION_NUMBER, itemsBySha1.getItemVersionNumber())
                    .textValue(ITEM_NAME, itemsBySha1.getItemName())
                    .textValue(OWNER_BY_JSON, itemsBySha1.getOwnerByJson())
                    .textValue(PATH, itemsBySha1.getPath())
                    .bigIntValue(CREATED_AT, itemsBySha1.getCreatedAt())
                    .build();
            transaction.put(put);
            return itemsBySha1;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ItemsBySha1 getItemsBySha1(String itemSha1, Long itemId, Long itemVersionId, DistributedTransaction transaction) {

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(SHA1_HASH, itemSha1))
                .clusteringKey(Key.of(ITEM_ID, itemId, ITEM_VERSION_ID, itemVersionId))
                .build();

        try {
            Optional<Result> optionalResult = transaction.get(get);
            if (optionalResult.isPresent()) {
                Result result = optionalResult.get();
                return ItemsBySha1.builder()
                        .sha1Hash(result.getText(SHA1_HASH))
                        .itemId(result.getBigInt(ITEM_ID))
                        .itemVersionId(result.getBigInt(ITEM_VERSION_ID))
                        .itemVersionNumber(result.getInt(ITEM_VERSION_NUMBER))
                        .ownerByJson(result.getText(OWNER_BY_JSON))
                        .itemName(result.getText(ITEM_NAME))
                        .createdAt(result.getBigInt(CREATED_AT))
                        .path(result.getText(PATH))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }
}
