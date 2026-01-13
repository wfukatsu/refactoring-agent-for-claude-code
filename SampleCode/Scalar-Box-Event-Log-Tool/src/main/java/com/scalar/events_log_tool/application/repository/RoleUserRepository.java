package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.RoleUser;
import com.scalar.events_log_tool.application.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class RoleUserRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "role_user";
    private static final String ROLE_NAME = "role_name";
    private static final String ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_EMAIL = "user_email";

    public RoleUser create(RoleUser roleUser, DistributedTransaction transaction) {

        Key key = Key.ofText(ROLE_NAME,roleUser.getRoleName());
        Key clusteringKey = Key.ofText(USER_EMAIL, roleUser.getUserEmail());
        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .clusteringKey(clusteringKey)
                    .bigIntValue(ID, roleUser.getUserId())
                    .textValue(USER_NAME, roleUser.getUserName())
                    .build();
            transaction.put(put);
            return roleUser;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public RoleUser get(String roleName, String userEmail, DistributedTransaction transaction) {
        Optional<Result> result;
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(ROLE_NAME,roleName))
                .clusteringKey(Key.ofText(USER_EMAIL, userEmail))
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return RoleUser.builder()
                        .roleName(resultObject.getText(ROLE_NAME))
                        .userId(resultObject.getBigInt(ID))
                        .userName(resultObject.getText(USER_NAME))
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public void delete(String roleName, String userEmail, DistributedTransaction transaction) {

        Delete delete = Delete.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(ROLE_NAME,roleName))
                .clusteringKey(Key.ofText(USER_EMAIL, userEmail))
                .build();

        try {
           transaction.delete(delete);
        } catch (CrudException e) {
            e.printStackTrace();
        }

    }

    public List<RoleUser> getByRole(String roleName,DistributedTransaction transaction) throws CrudException {

        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(ROLE_NAME,roleName))
                .build();

        List<Result> results = transaction.scan(scan);
        List<RoleUser> roleUsers = new ArrayList<>();
        for (Result result : results) {
            RoleUser roleUser = RoleUser.builder()
                    .userEmail(result.getText(USER_EMAIL))
                    .userId(result.getBigInt(ID))
                    .roleName(result.getText(ROLE_NAME))
                    .userName(result.getText(USER_NAME))
                    .build();
            roleUsers.add(roleUser);
        }
        return roleUsers;
    }
}
