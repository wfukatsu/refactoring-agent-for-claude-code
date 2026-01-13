package com.scalar.events_log_tool.application.repository;


import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.dto.EditUserEmail;
import com.scalar.events_log_tool.application.model.AuditSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class AuditSetRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "audit_set";
    private static final String ID = "audit_set_id";
    private static final String AUDIT_SET_NAME = "audit_set_name";
    private static final String DESCRIPTION = "description";
    private static final String OWNER_ID = "owner_id";
    private static final String OWNER_NAME = "owner_name";
    private static final String OWNER_EMAIL = "owner_email";
    private static final String ACL_JSON = "acl_json";
    private static final String IS_DELETED = "is_deleted";
    private static final String CREATED_AT = "createdAt";
    private static final String AUDIT_GROUP_LIST_JSON = "audit_group_list_json";


    public AuditSet create(AuditSet auditSet, DistributedTransaction transaction) {

        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(ID, auditSet.getAuditSetId()))
                    .textValue(AUDIT_SET_NAME, auditSet.getAuditSetName())
                    .textValue(DESCRIPTION, auditSet.getDescription())
                    .bigIntValue(OWNER_ID, auditSet.getOwnerId())
                    .textValue(OWNER_NAME, auditSet.getOwnerName())
                    .textValue(OWNER_EMAIL, auditSet.getOwnerEmail())
                    .textValue(ACL_JSON, auditSet.getAclJson())
                    .booleanValue(IS_DELETED, auditSet.getIsDeleted())
                    .bigIntValue(CREATED_AT, auditSet.getCreatedAt())
                    .textValue(AUDIT_GROUP_LIST_JSON, auditSet.getAuditGroupListJson())
                    .build();
            transaction.put(put);
            return auditSet;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }


    public AuditSet get(String id, DistributedTransaction transaction) {
        Optional<Result> result;
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(ID, id))
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return AuditSet.builder()
                        .auditSetId(resultObject.getText(ID))
                        .auditSetName(resultObject.getText(AUDIT_SET_NAME))
                        .description(resultObject.getText(DESCRIPTION))
                        .ownerId(resultObject.getBigInt(OWNER_ID))
                        .ownerName(resultObject.getText(OWNER_NAME))
                        .ownerEmail(resultObject.getText(OWNER_EMAIL))
                        .aclJson(resultObject.getText(ACL_JSON))
                        .isDeleted(resultObject.getBoolean(IS_DELETED))
                        .createdAt(resultObject.getBigInt(CREATED_AT))
                        .auditGroupListJson(resultObject.getText(AUDIT_GROUP_LIST_JSON))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public List<AuditSet> getAuditSetList(DistributedTransaction transaction) throws CrudException {
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .all()
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditSet> auditSetList = new ArrayList<>();
        for (Result result : results) {
            AuditSet auditSet = AuditSet.builder()
                    .auditSetId(result.getText(ID))
                    .auditSetName(result.getText(AUDIT_SET_NAME))
                    .description(result.getText(DESCRIPTION))
                    .ownerId(result.getBigInt(OWNER_ID))
                    .ownerName(result.getText(OWNER_NAME))
                    .ownerEmail(result.getText(OWNER_EMAIL))
                    .aclJson(result.getText(ACL_JSON))
                    .isDeleted(result.getBoolean(IS_DELETED))
                    .createdAt(result.getBigInt(CREATED_AT))
                    .auditGroupListJson(result.getText(AUDIT_GROUP_LIST_JSON))
                    .build();
            auditSetList.add(auditSet);
        }
        return auditSetList;
    }

    public List<AuditSet> getMyAuditSetList(String currentEmail, DistributedTransaction transaction) throws CrudException {
        Key key = Key.ofText(OWNER_EMAIL, currentEmail);
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .indexKey(key)
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditSet> auditSetList = new ArrayList<>();
        for (Result result : results) {
            AuditSet auditSet = AuditSet.builder()
                    .auditSetId(result.getText(ID))
                    .auditSetName(result.getText(AUDIT_SET_NAME))
                    .description(result.getText(DESCRIPTION))
                    .ownerId(result.getBigInt(OWNER_ID))
                    .ownerName(result.getText(OWNER_NAME))
                    .ownerEmail(result.getText(OWNER_EMAIL))
                    .aclJson(result.getText(ACL_JSON))
                    .isDeleted(result.getBoolean(IS_DELETED))
                    .createdAt(result.getBigInt(CREATED_AT))
                    .auditGroupListJson(result.getText(AUDIT_GROUP_LIST_JSON))
                    .build();
            auditSetList.add(auditSet);
        }
        return auditSetList;
    }

    public AuditSet createAndDeleteUserEmail(AuditSet auditSet, EditUserEmail editUserEmail, DistributedTransaction transaction) {

        Key key = Key.ofText(OWNER_EMAIL, editUserEmail.getUserEmail());

        Key keyToBeDelete = Key.ofText(OWNER_EMAIL, auditSet.getOwnerEmail());

        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .textValue(ID, auditSet.getAuditSetId())
                    .textValue(AUDIT_SET_NAME, auditSet.getAuditSetName())
                    .textValue(DESCRIPTION, auditSet.getDescription())
                    .bigIntValue(OWNER_ID, auditSet.getOwnerId())
                    .textValue(OWNER_NAME, editUserEmail.getName())
                    .textValue(ACL_JSON, auditSet.getAclJson())
                    .booleanValue(IS_DELETED, auditSet.getIsDeleted())
                    .bigIntValue(CREATED_AT, auditSet.getCreatedAt())
                    .textValue(AUDIT_GROUP_LIST_JSON, auditSet.getAuditGroupListJson())
                    .build();

            Delete delete = Delete.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(keyToBeDelete)
                    .build();
            transaction.mutate(Arrays.asList(put, delete));
            return auditSet;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

}