package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.dto.EditUserEmail;
import com.scalar.events_log_tool.application.model.AuditGroup;
import com.scalar.events_log_tool.application.model.RoleUser;
import com.scalar.events_log_tool.application.model.AuditSetCollaborators;
import com.scalar.events_log_tool.application.model.UserAuditGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserAuditGroupRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "user_audit_group";
    private static final String USER_EMAIL = "user_email";
    private static final String AUDIT_GROUP_ID = "audit_group_id";
    private static final String AUDIT_GROUP_NAME = "audit_group_name";
    private static final String PRIVILEGE = "privilege";

    public UserAuditGroup create(UserAuditGroup userAuditGroup, DistributedTransaction transaction) {
        Key clusteringKey = Key.ofText(AUDIT_GROUP_ID, userAuditGroup.getAuditGroupId());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(USER_EMAIL, userAuditGroup.getUserEmail()))
                    .clusteringKey(clusteringKey)
                    .textValue(AUDIT_GROUP_NAME, userAuditGroup.getAuditGroupName())
                    .textValue(PRIVILEGE, userAuditGroup.getPrivilege())
                    .build();
            transaction.put(put);
            return userAuditGroup;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<UserAuditGroup> getUserGroupList(String currentEmail, DistributedTransaction transaction) throws CrudException {

        Key key = Key.ofText(USER_EMAIL, currentEmail);
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .build();

        List<Result> results = transaction.scan(scan);
        List<UserAuditGroup> userAuditGroupList = new ArrayList<>();
        for (Result result : results) {
            UserAuditGroup userAuditGroup = UserAuditGroup.builder()
                    .userEmail(result.getText(USER_EMAIL))
                    .auditGroupId(result.getText(AUDIT_GROUP_ID))
                    .auditGroupName(result.getText(AUDIT_GROUP_NAME))
                    .privilege(result.getText(PRIVILEGE))
                    .build();
            userAuditGroupList.add(userAuditGroup);
        }
        return userAuditGroupList;
    }

    public UserAuditGroup get(String auditGroupId, String userEmail, DistributedTransaction transaction) {
        Optional<Result> result;

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(USER_EMAIL, userEmail))
                .clusteringKey(Key.ofText(AUDIT_GROUP_ID, auditGroupId))
                .build();

        try {
            result = transaction.get(get);

            log.info("Result:{}",result);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return UserAuditGroup.builder()
                        .auditGroupId(resultObject.getText(AUDIT_GROUP_ID))
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .auditGroupName(resultObject.getText(AUDIT_GROUP_NAME))
                        .privilege(resultObject.getText(PRIVILEGE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public void delete(String auditGroupId, String userEmail, DistributedTransaction transaction) {

        Delete delete = Delete.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(USER_EMAIL, userEmail))
                .clusteringKey(Key.ofText(AUDIT_GROUP_ID, auditGroupId))
                .build();

        try {
            transaction.delete(delete);
        } catch (CrudException e) {
            e.printStackTrace();
        }

    }
    public UserAuditGroup createAndDeleteUserEmail(UserAuditGroup userAuditGroup, EditUserEmail editUserEmail, DistributedTransaction transaction) {

        Key key = Key.ofText(USER_EMAIL, editUserEmail.getUserEmail());

        Key keyToBeDelete = Key.ofText(USER_EMAIL, userAuditGroup.getUserEmail());
        Key clusteringKey = Key.ofText(AUDIT_GROUP_ID, userAuditGroup.getAuditGroupId());


        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .clusteringKey(clusteringKey)
                    .build();

            Delete delete = Delete.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(keyToBeDelete)
                    .clusteringKey(clusteringKey)
                    .build();
            transaction.mutate(Arrays.asList(put, delete));
            return userAuditGroup;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }
}