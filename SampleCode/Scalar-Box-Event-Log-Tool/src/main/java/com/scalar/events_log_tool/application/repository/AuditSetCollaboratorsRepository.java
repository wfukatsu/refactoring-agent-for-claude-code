package com.scalar.events_log_tool.application.repository;


import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.dto.EditUserEmail;
import com.scalar.events_log_tool.application.model.AuditSetCollaborators;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AuditSetCollaboratorsRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "audit_set_collaborators";
    private static final String AUDIT_SET_ID = "audit_set_id";
    private static final String USER_EMAIL = "user_email";
    private static final String AUDIT_SET_NAME = "audit_set_name";
    private static final String USER_NAME = "user_name";
    private static final String AUDIT_SET_ROLE = "audit_set_role";
    private static final String ACCESS_STATUS = "access_status";
    private static final String IS_FAVOURITE = "is_favourite";

    public AuditSetCollaborators create(AuditSetCollaborators auditSetCollaborators, DistributedTransaction transaction) {

        Key key = Key.ofText(USER_EMAIL, auditSetCollaborators.getUserEmail());
        Key clusteringKey = Key.of(AUDIT_SET_ID, auditSetCollaborators.getAuditSetId(), AUDIT_SET_ROLE, auditSetCollaborators.getAuditSetRole());
        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .clusteringKey(clusteringKey)
                    .textValue(AUDIT_SET_NAME, auditSetCollaborators.getAuditSetName())
                    .textValue(USER_NAME, auditSetCollaborators.getUserName())
                    .textValue(ACCESS_STATUS, auditSetCollaborators.getAccessStatus())
                    .booleanValue(IS_FAVOURITE, auditSetCollaborators.getIsFavourite())
                    .build();
            transaction.put(put);
            return auditSetCollaborators;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }


    public AuditSetCollaborators createAndDelete(AuditSetCollaborators auditSetCollaboratorsToPut, String auditSetRole, DistributedTransaction transaction) {

        Key key = Key.ofText(USER_EMAIL, auditSetCollaboratorsToPut.getUserEmail());
        Key clusteringKey = Key.of(AUDIT_SET_ID, auditSetCollaboratorsToPut.getAuditSetId(), AUDIT_SET_ROLE, auditSetCollaboratorsToPut.getAuditSetRole());
        Key clusteringKeyToDelete = Key.of(AUDIT_SET_ID, auditSetCollaboratorsToPut.getAuditSetId(), AUDIT_SET_ROLE, auditSetRole);

        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .clusteringKey(clusteringKey)
                    .textValue(AUDIT_SET_NAME, auditSetCollaboratorsToPut.getAuditSetName())
                    .textValue(USER_NAME, auditSetCollaboratorsToPut.getUserName())
                    .textValue(ACCESS_STATUS, auditSetCollaboratorsToPut.getAccessStatus())
                    .booleanValue(IS_FAVOURITE, auditSetCollaboratorsToPut.getIsFavourite())
                    .build();


            Delete delete = Delete.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .clusteringKey(clusteringKeyToDelete)
                    .build();
            transaction.mutate(Arrays.asList(put, delete));
            return auditSetCollaboratorsToPut;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public AuditSetCollaborators get(String id, String role, String userEmail, DistributedTransaction transaction) {

        Optional<Result> result;
        Key clusteringKey = Key.of(AUDIT_SET_ID, id, AUDIT_SET_ROLE, role);

        Key key = Key.ofText(USER_EMAIL, userEmail);

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .clusteringKey(clusteringKey)
                .build();
        try {
            result = transaction.get(get);

            if (result.isPresent()) {
                Result resultObject = result.get();
                return AuditSetCollaborators.builder()
                        .auditSetId(resultObject.getText(AUDIT_SET_ID))
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .auditSetName(resultObject.getText(AUDIT_SET_NAME))
                        .auditSetRole(resultObject.getText(AUDIT_SET_ROLE))
                        .userName(resultObject.getText(USER_NAME))
                        .accessStatus(resultObject.getText(ACCESS_STATUS))
                        .isFavourite(resultObject.getBoolean(IS_FAVOURITE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void delete(String auditSetId, String role, String userEmail, DistributedTransaction transaction) {

        Key clusteringKey = Key.of(AUDIT_SET_ID, auditSetId, AUDIT_SET_ROLE, role);

        Delete delete = Delete.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(USER_EMAIL, userEmail))
                .clusteringKey(clusteringKey)
                .build();
        try {
            transaction.delete(delete);
        } catch (CrudException e) {
            e.printStackTrace();
        }

    }

    public List<AuditSetCollaborators> getAuditSetCollaboratorList(String userEmail, DistributedTransaction transaction) throws CrudException {
        Key key = Key.ofText(USER_EMAIL, userEmail);
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditSetCollaborators> auditSetCollaboratorsList = new ArrayList<>();
        for (Result result : results) {
            AuditSetCollaborators auditSetCollaborators = AuditSetCollaborators.builder()
                    .auditSetId(result.getText(AUDIT_SET_ID))
                    .userEmail(result.getText(USER_EMAIL))
                    .auditSetName(result.getText(AUDIT_SET_NAME))
                    .userName(result.getText(USER_NAME))
                    .auditSetRole(result.getText(AUDIT_SET_ROLE))
                    .accessStatus(result.getText(ACCESS_STATUS))
                    .isFavourite(result.getBoolean(IS_FAVOURITE))
                    .build();
            auditSetCollaboratorsList.add(auditSetCollaborators);
        }
        return auditSetCollaboratorsList;
    }

    public AuditSetCollaborators createAndDeleteUserEmail(AuditSetCollaborators auditSetCollaborators, EditUserEmail editUserEmail, DistributedTransaction transaction) {

        Key key = Key.ofText(USER_EMAIL, editUserEmail.getUserEmail());

        Key keyToBeDelete = Key.ofText(USER_EMAIL, auditSetCollaborators.getUserEmail());
        Key clusteringKey = Key.of(AUDIT_SET_ID, auditSetCollaborators.getAuditSetId(), AUDIT_SET_ROLE, auditSetCollaborators.getAuditSetRole());


        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .clusteringKey(clusteringKey)
                    .textValue(AUDIT_SET_NAME, auditSetCollaborators.getAuditSetName())
                    .textValue(USER_NAME, editUserEmail.getName())
                    .build();

            Delete delete = Delete.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(keyToBeDelete)
                    .clusteringKey(clusteringKey)
                    .build();
            transaction.mutate(Arrays.asList(put, delete));
            return auditSetCollaborators;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<AuditSetCollaborators> getAuditSetCollaborators(String userEmail, String auditSetId, DistributedTransaction transaction) {
        Key key = Key.ofText(USER_EMAIL, userEmail);
        Key clusteringKey = Key.ofText(AUDIT_SET_ID, auditSetId);
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .start(clusteringKey, true)
                .end(clusteringKey, true)
                .build();
        try {
            List<Result> resultObject = transaction.scan(scan);
            if (!resultObject.isEmpty()) {
                return resultObject.stream().map(result -> AuditSetCollaborators.builder()
                                .auditSetId(result.getText(AUDIT_SET_ID))
                                .userEmail(result.getText(USER_EMAIL))
                                .auditSetName(result.getText(AUDIT_SET_NAME))
                                .auditSetRole(result.getText(AUDIT_SET_ROLE))
                                .userName(result.getText(USER_NAME))
                                .accessStatus(result.getText(ACCESS_STATUS))
                                .isFavourite(result.getBoolean(IS_FAVOURITE))
                                .build())
                        .collect(Collectors.toList());
            }
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public AuditSetCollaborators getAuditSetCollaborator(String userEmail, String auditSetId, DistributedTransaction transaction) {
        Key key = Key.ofText(USER_EMAIL, userEmail);
        Key clusteringKey = Key.ofText(AUDIT_SET_ID, auditSetId);
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .start(clusteringKey, true)
                .build();
        try {
            List<Result> resultObject = transaction.scan(scan);
            if (!resultObject.isEmpty()) {
                Result result = resultObject.get(0);
                return AuditSetCollaborators.builder()
                        .auditSetId(result.getText(AUDIT_SET_ID))
                        .userEmail(result.getText(USER_EMAIL))
                        .auditSetName(result.getText(AUDIT_SET_NAME))
                        .auditSetRole(result.getText(AUDIT_SET_ROLE))
                        .userName(result.getText(USER_NAME))
                        .accessStatus(result.getText(ACCESS_STATUS))
                        .isFavourite(result.getBoolean(IS_FAVOURITE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();

        }
        return null;
    }
}