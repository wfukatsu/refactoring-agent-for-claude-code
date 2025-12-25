package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.AuditSetCollaborators;
import com.scalar.events_log_tool.application.model.AuditSetItem;
import com.scalar.events_log_tool.application.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditSetItemRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "auditset_folder_file_mapping";
    private static final String AUDIT_SET_ID = "audit_set_id";
    private static final String ITEM_ID = "item_id";
    private static final String ITEM_TYPE = "item_type";
    private static final String ACCESS_LIST_TYPE = "access_list_type";
    private static final String LIST_JSON = "list_json";
    private static final String ITEM_NAME = "item_name";
    private static final String CREATED_AT = "created_at";
    private static final String ASSIGNED_BY_USERID = "assigned_by_user_id";
    public AuditSetItem create(AuditSetItem auditSetItem, DistributedTransaction transaction) {

        Key partitionKey = Key.ofText(AUDIT_SET_ID, auditSetItem.getAuditSetId());

        Key clusteringKey = Key.ofBigInt(ITEM_ID, auditSetItem.getItemId());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(partitionKey)
                    .clusteringKey(clusteringKey)
                    .textValue(ITEM_NAME, auditSetItem.getItemName())
                    .textValue(ITEM_TYPE, auditSetItem.getItemType())
                    .textValue(ACCESS_LIST_TYPE, auditSetItem.getAccessList())
                    .textValue(LIST_JSON, auditSetItem.getListJson())
                    .bigIntValue(CREATED_AT,auditSetItem.getCreatedAt())
                    .bigIntValue(ASSIGNED_BY_USERID,auditSetItem.getAssignedByUserId())
                    .build();
            transaction.put(put);
            return auditSetItem;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }


    public AuditSetItem get(String auditSetId, Long itemId, DistributedTransaction transaction) {

        Optional<Result> result;
        Key partitionKey = Key.ofText(AUDIT_SET_ID, auditSetId);
        Key clusteringKey = Key.ofBigInt(ITEM_ID, itemId);

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(partitionKey)
                .clusteringKey(clusteringKey)
                .build();
        try {
            result = transaction.get(get);

            if (result.isPresent()) {
                Result resultObject = result.get();
                return AuditSetItem.builder()
                        .auditSetId(resultObject.getText(AUDIT_SET_ID))
                        .itemId(resultObject.getBigInt(ITEM_ID))
                        .itemName(resultObject.getText(ITEM_NAME))
                        .itemType(resultObject.getText(ITEM_TYPE))
                        .accessList(resultObject.getText(ACCESS_LIST_TYPE))
                        .listJson(resultObject.getText(LIST_JSON))
                        .createdAt(resultObject.getBigInt(CREATED_AT))
                        .assignedByUserId(resultObject.getBigInt(ASSIGNED_BY_USERID))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    public void delete(String auditSetId, Long itemId, DistributedTransaction transaction) {

        Delete delete = Delete.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(AUDIT_SET_ID,auditSetId))
                .clusteringKey(Key.ofBigInt(ITEM_ID, itemId))
                .build();

        try {
            transaction.delete(delete);
        } catch (CrudException e) {
            e.printStackTrace();
        }

    }

    public List<AuditSetItem> getAuditSetItems(String auditSet, DistributedTransaction transaction) throws CrudException {

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(AUDIT_SET_ID, auditSet))
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditSetItem> items = new ArrayList<>();
        for (Result result : results) {
            AuditSetItem auditSetItem = AuditSetItem.builder()
                    .auditSetId(result.getText(AUDIT_SET_ID))
                    .itemId(result.getBigInt(ITEM_ID))
                    .itemType(result.getText(ITEM_TYPE))
                    .accessList(result.getText(ACCESS_LIST_TYPE))
                    .listJson(result.getText(LIST_JSON))
                    .itemName(result.getText(ITEM_NAME))
                    .build();
            items.add(auditSetItem);
        }
        return items;
    }

    public List<AuditSetItem> getAllItemsOfUser(Long userID,DistributedTransaction transaction) throws TransactionException {
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .indexKey(Key.ofBigInt(ASSIGNED_BY_USERID,userID))
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditSetItem> itemList = new ArrayList<>();
        for (Result result : results) {
            AuditSetItem auditSetItem = AuditSetItem.builder()
                    .auditSetId(result.getText(AUDIT_SET_ID))
                    .itemId(result.getBigInt(ITEM_ID))
                    .itemType(result.getText(ITEM_TYPE))
                    .accessList(result.getText(ACCESS_LIST_TYPE))
                    .listJson(result.getText(LIST_JSON))
                    .itemName(result.getText(ITEM_NAME))
                    .createdAt(result.getBigInt(CREATED_AT))
                    .assignedByUserId(result.getBigInt(ASSIGNED_BY_USERID))
                    .build();
            itemList.add(auditSetItem);
        }
        return itemList;
    }

}
