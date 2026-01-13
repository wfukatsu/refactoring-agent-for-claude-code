package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.Organization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class OrganizationRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "organization";
    private static final String ORG_NAME = "organization_name";
    private static final String ORG_ID = "org_id";

    public List<Organization> getOrganizationList(DistributedTransaction transaction) throws TransactionException {
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .all()
                .build();

        List<Result> results = transaction.scan(scan);
        List<Organization> orgList = new ArrayList<>();
        for (Result result : results) {
            Organization organization = Organization.builder()
                    .orgId(result.getText(ORG_ID))
                    .organizationName(result.getText(ORG_NAME))
                    .build();
            orgList.add(organization);
        }
        return orgList;
    }


    public Organization create(Organization organization, DistributedTransaction transaction) {
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(ORG_ID, organization.getOrgId()))
                    .textValue(ORG_NAME, organization.getOrganizationName())
                    .build();
            transaction.put(put);
            return organization;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }


    public Organization getOrganization(DistributedTransaction transaction, String orgId) {
        try {
            Get get = Get.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(ORG_ID, orgId))
                    .build();

            Optional<Result> results = transaction.get(get);
            if (results.isPresent()) {
                Result result = results.get();
                return Organization.builder()
                        .orgId(result.getText(ORG_ID))
                        .organizationName(result.getText(ORG_NAME))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }
}
