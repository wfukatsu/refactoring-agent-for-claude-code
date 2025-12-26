package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.dto.EditUserEmail;
import com.scalar.events_log_tool.application.model.AuditGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class AuditGroupRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "audit_group";
    private static final String USER_EMAIL = "user_email";
    private static final String AUDIT_GROUP_ID = "audit_group_id";
    private static final String AUDIT_GROUP_NAME = "audit_group_name";
    private static final String DESCRIPTION = "description";
    private static final String OWNER_ID = "owner_id";
    private static final String OWNER_NAME = "owner_name";
    private static final String MEMBER_LIST_JSON = "member_list_json";
    private static final String CREATED_AT = "created_at";
    private static final String IS_DELETED = "is_deleted";


    public AuditGroup create(AuditGroup auditGroup, DistributedTransaction transaction) {

        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(AUDIT_GROUP_ID, auditGroup.getAuditGroupId()))
                    .textValue(USER_EMAIL, auditGroup.getUserEmail())
                    .textValue(AUDIT_GROUP_NAME, auditGroup.getAuditGroupName())
                    .textValue(DESCRIPTION, auditGroup.getDescription())
                    .bigIntValue(OWNER_ID, auditGroup.getOwnerId())
                    .textValue(OWNER_NAME, auditGroup.getOwnerName())
                    .textValue(MEMBER_LIST_JSON, auditGroup.getMemberListJson())
                    .bigIntValue(CREATED_AT, auditGroup.getCreatedAt())
                    .booleanValue(IS_DELETED, auditGroup.getIsDeleted())
                    .build();
            transaction.put(put);
            return auditGroup;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<AuditGroup> getgroupList(DistributedTransaction transaction) throws CrudException {
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .all()
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditGroup> groupList = new ArrayList<>();
        for (Result result : results) {
            AuditGroup auditGroup = AuditGroup.builder()
                    .userEmail(result.getText(USER_EMAIL))
                    .auditGroupId(result.getText(AUDIT_GROUP_ID))
                    .auditGroupName(result.getText(AUDIT_GROUP_NAME))
                    .description(result.getText(DESCRIPTION))
                    .ownerId(result.getBigInt(OWNER_ID))
                    .ownerName(result.getText(OWNER_NAME))
                    .memberListJson(result.getText(MEMBER_LIST_JSON))
                    .createdAt(result.getBigInt(CREATED_AT))
                    .isDeleted(result.getBoolean(IS_DELETED))
                    .build();
            groupList.add(auditGroup);
        }
        return groupList;
    }


    public AuditGroup getAuditGroup(String auditGroupId, DistributedTransaction transaction) {
        Optional<Result> result;
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(AUDIT_GROUP_ID, auditGroupId))
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return AuditGroup.builder()
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .auditGroupId(resultObject.getText(AUDIT_GROUP_ID))
                        .auditGroupName(resultObject.getText(AUDIT_GROUP_NAME))
                        .description(resultObject.getText(DESCRIPTION))
                        .ownerId(resultObject.getBigInt(OWNER_ID))
                        .ownerName(resultObject.getText(OWNER_NAME))
                        .memberListJson(resultObject.getText(MEMBER_LIST_JSON))
                        .createdAt(resultObject.getBigInt(CREATED_AT))
                        .isDeleted(resultObject.getBoolean(IS_DELETED))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public List<AuditGroup> getMygroupList(String auditGroupId, DistributedTransaction transaction) throws CrudException {
        Key key = Key.ofText(AUDIT_GROUP_ID, auditGroupId);
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditGroup> groupList = new ArrayList<>();
        for (Result result : results) {
            AuditGroup auditGroup = AuditGroup.builder()
                    .userEmail(result.getText(USER_EMAIL))
                    .auditGroupId(result.getText(AUDIT_GROUP_ID))
                    .auditGroupName(result.getText(AUDIT_GROUP_NAME))
                    .description(result.getText(DESCRIPTION))
                    .ownerId(result.getBigInt(OWNER_ID))
                    .ownerName(result.getText(OWNER_NAME))
                    .memberListJson(result.getText(MEMBER_LIST_JSON))
                    .createdAt(result.getBigInt(CREATED_AT))
                    .isDeleted(result.getBoolean(IS_DELETED))
                    .build();
            groupList.add(auditGroup);
        }
        return groupList;
    }

    public AuditGroup createAndDeleteUserEmail(AuditGroup auditGroup, EditUserEmail editUserEmail, DistributedTransaction transaction) {

        Key key = Key.ofText(AUDIT_GROUP_ID, auditGroup.getAuditGroupId());


        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .textValue(USER_EMAIL, editUserEmail.getUserEmail())
                    .textValue(AUDIT_GROUP_NAME, auditGroup.getAuditGroupName())
                    .textValue(DESCRIPTION, auditGroup.getDescription())
                    .textValue(OWNER_ID, auditGroup.getAuditGroupId())
                    .textValue(OWNER_NAME, editUserEmail.getName())
                    .textValue(MEMBER_LIST_JSON, auditGroup.getMemberListJson())
                    .bigIntValue(CREATED_AT, auditGroup.getCreatedAt())
                    .booleanValue(IS_DELETED, auditGroup.getIsDeleted())
                    .build();

            Delete delete = Delete.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .build();
            transaction.mutate(Arrays.asList(put, delete));
            return auditGroup;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public AuditGroup update(AuditGroup auditGroup, DistributedTransaction transaction) {
        try {
            Key key = Key.ofText(AUDIT_GROUP_ID, auditGroup.getAuditGroupId());

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .textValue(USER_EMAIL, auditGroup.getUserEmail())
                    .textValue(AUDIT_GROUP_NAME, auditGroup.getAuditGroupName())
                    .textValue(DESCRIPTION, auditGroup.getDescription())
                    .textValue(OWNER_ID, auditGroup.getAuditGroupId())
                    .textValue(OWNER_NAME, auditGroup.getOwnerName())
                    .textValue(MEMBER_LIST_JSON, auditGroup.getMemberListJson())
                    .bigIntValue(CREATED_AT, auditGroup.getCreatedAt())
                    .booleanValue(IS_DELETED, auditGroup.getIsDeleted())
                    .build();
            transaction.put(put);
            return auditGroup;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }

}
