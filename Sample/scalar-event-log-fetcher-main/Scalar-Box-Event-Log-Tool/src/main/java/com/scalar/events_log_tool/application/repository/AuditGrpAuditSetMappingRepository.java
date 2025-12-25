package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.AuditGroup;
import com.scalar.events_log_tool.application.model.AuditGrpAuditSetMapping;
import com.scalar.events_log_tool.application.model.AuditSetCollaborators;
import com.scalar.events_log_tool.application.model.UserAuditGroup;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditGrpAuditSetMappingRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "audit_grp_audit_set_mapping";
    private static final String AUDIT_GROUP_ID = "audit_group_id";
    private static final String AUDIT_GROUP_NAME = "audit_group_name";
    private static final String AUDIT_SET_ID = "audit_set_id";
    private static final String AUDIT_SET_NAME = "audit_set_name";


    public AuditGrpAuditSetMapping create(AuditGrpAuditSetMapping auditGrpAuditSetMapping, DistributedTransaction transaction) {

        Key key = Key.ofText(AUDIT_GROUP_ID, auditGrpAuditSetMapping.getAuditGroupId());
        Key clusteringKey = Key.ofText(AUDIT_SET_ID, auditGrpAuditSetMapping.getAuditSetId());
        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .clusteringKey(clusteringKey)
                    .textValue(AUDIT_GROUP_NAME, auditGrpAuditSetMapping.getAuditGroupName())
                    .textValue(AUDIT_SET_NAME, auditGrpAuditSetMapping.getAuditSetName())
                    .build();
            transaction.put(put);
            return auditGrpAuditSetMapping;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }


    public List<AuditGrpAuditSetMapping> getUserGrpAuditSetList(String auditGroupId, DistributedTransaction transaction) throws CrudException {

        Key key = Key.ofText(AUDIT_GROUP_ID, auditGroupId);

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .build();

        List<Result> results = transaction.scan(scan);
        List<AuditGrpAuditSetMapping> auditGrpAuditSetMappingList = new ArrayList<>();
        for (Result result : results) {
            AuditGrpAuditSetMapping auditGrpAuditSetMapping = AuditGrpAuditSetMapping.builder()
                    .AuditGroupId(result.getText(AUDIT_GROUP_ID))
                    .AuditGroupName(result.getText(AUDIT_GROUP_NAME))
                    .auditSetId(result.getText(AUDIT_SET_ID))
                    .auditSetName(result.getText(AUDIT_SET_NAME))
                    .build();
            auditGrpAuditSetMappingList.add(auditGrpAuditSetMapping);
        }
        return auditGrpAuditSetMappingList;
    }

    public List<AuditGrpAuditSetMapping> getAllAuditSetByGroupId(String auditGroupId,DistributedTransaction transaction) throws CrudException {


        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(AUDIT_GROUP_ID, auditGroupId))
                .build();
        List<Result> results = transaction.scan(scan);
            List<AuditGrpAuditSetMapping> auditGrpAuditSetMappingList = new ArrayList<>();
            for (Result result : results) {
                AuditGrpAuditSetMapping auditGrpAuditSetMapping = AuditGrpAuditSetMapping.builder()
                        .AuditGroupId(result.getText(AUDIT_GROUP_ID))
                        .AuditGroupName(result.getText(AUDIT_GROUP_NAME))
                        .auditSetId(result.getText(AUDIT_SET_ID))
                        .auditSetName(result.getText(AUDIT_SET_NAME))
                        .build();
                auditGrpAuditSetMappingList.add(auditGrpAuditSetMapping);
            }

            return auditGrpAuditSetMappingList;
    }

    public void delete(String auditGroupId, String auditSetId, DistributedTransaction transaction) {

        Delete delete = Delete.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(AUDIT_GROUP_ID, auditGroupId))
                .clusteringKey(Key.ofText(AUDIT_SET_ID, auditSetId))
                .build();

        try {
            transaction.delete(delete);
        } catch (CrudException e) {
            e.printStackTrace();
        }

    }
    public AuditGrpAuditSetMapping getAuditGroupAndAuditSetMapping(String auditGroupId,String auditSetId, DistributedTransaction transaction) {
        Optional<Result> result;
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(AUDIT_GROUP_ID, auditGroupId))
                .clusteringKey(Key.ofText(AUDIT_SET_ID,auditSetId))
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return AuditGrpAuditSetMapping.builder()
                        .AuditGroupId(resultObject.getText(AUDIT_GROUP_ID))
                        .AuditGroupName(resultObject.getText(AUDIT_GROUP_NAME))
                        .auditSetId(resultObject.getText(AUDIT_SET_ID))
                        .auditSetName(resultObject.getText(AUDIT_SET_NAME))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
