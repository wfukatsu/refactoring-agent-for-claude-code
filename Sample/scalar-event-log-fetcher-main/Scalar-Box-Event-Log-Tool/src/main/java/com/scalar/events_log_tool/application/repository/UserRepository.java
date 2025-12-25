package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.*;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "user";
    private static final String USER_EMAIL = "user_email";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PASSWORD = "password";
    private static final String ROLE_JSON = "role_json";
    private static final String ORG_ID = "org_id";
    private static final String ORGANISATION_NAME = "organization_name";
    private static final String IMAGE_URL = "image_url";
    private static final String IS_DELETED = "is_deleted";
    private static final String IS_BOX_ADMIN = "is_box_admin";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String REFRESH_TOKEN_EXPIRY = "refresh_token_expiry";
    private static final String LANGUAGE_CODE = "language_code";

    public User create(User user, DistributedTransaction transaction) {
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(USER_EMAIL, user.getUserEmail()))
                    .bigIntValue(ID, user.getId())
                    .textValue(NAME, user.getName())
                    .textValue(PASSWORD, user.getPassword())
                    .textValue(ROLE_JSON, user.getRoleJson())
                    .textValue(ORG_ID, user.getOrgId())
                    .textValue(ORGANISATION_NAME, user.getOrganizationName())
                    .textValue(IMAGE_URL, user.getImageUrl())
                    .booleanValue(IS_DELETED, user.getIsDeleted())
                    .booleanValue(IS_BOX_ADMIN, user.getIsBoxAdmin())
                    .textValue(REFRESH_TOKEN, user.getRefreshToken())
                    .bigIntValue(REFRESH_TOKEN_EXPIRY, user.getRefreshTokenExpiry())
                    .textValue(LANGUAGE_CODE,user.getLanguageCode())
                    .build();
            transaction.put(put);
            return user;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public List<User> getUserList(DistributedTransaction transaction) throws TransactionException {
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .all()
                .build();

        List<Result> results = transaction.scan(scan);
        List<User> userList = new ArrayList<>();
        for (Result result : results) {
            User user = User.builder()
                    .userEmail(result.getText(USER_EMAIL))
                    .id(result.getBigInt(ID))
                    .name(result.getText(NAME))
                    .password(result.getText(PASSWORD))
                    .roleJson(result.getText(ROLE_JSON))
                    .orgId(result.getText(ORG_ID))
                    .organizationName(result.getText(ORGANISATION_NAME))
                    .imageUrl(result.getText(IMAGE_URL))
                    .isDeleted(result.getBoolean(IS_DELETED))
                    .isBoxAdmin(result.getBoolean(IS_BOX_ADMIN))
                    .refreshToken(result.getText(REFRESH_TOKEN))
                    .refreshTokenExpiry(result.getBigInt(REFRESH_TOKEN_EXPIRY))
                    .languageCode(result.getText(LANGUAGE_CODE))
                    .build();
            userList.add(user);
        }
        return userList;
    }

    public List<User> getOrgUserList(String orgId, DistributedTransaction transaction) throws TransactionException {

        Key key = Key.ofText(ORG_ID, orgId);
        Scan scan = Scan.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .indexKey(key)
                .build();

        List<Result> results = transaction.scan(scan);
        List<User> userList = new ArrayList<>();
        for (Result result : results) {
            User user = User.builder()
                    .userEmail(result.getText(USER_EMAIL))
                    .id(result.getBigInt(ID))
                    .name(result.getText(NAME))
                    .password(result.getText(PASSWORD))
                    .roleJson(result.getText(ROLE_JSON))
                    .orgId(result.getText(ORG_ID))
                    .organizationName(result.getText(ORGANISATION_NAME))
                    .imageUrl(result.getText(IMAGE_URL))
                    .isDeleted(result.getBoolean(IS_DELETED))
                    .isBoxAdmin(result.getBoolean(IS_BOX_ADMIN))
                    .refreshToken(result.getText(REFRESH_TOKEN))
                    .refreshTokenExpiry(result.getBigInt(REFRESH_TOKEN_EXPIRY))
                    .languageCode(result.getText(LANGUAGE_CODE))
                    .build();
            userList.add(user);
        }
        return userList;
    }

    public User getByUserEmail(String userEmail, DistributedTransaction transaction) {
        Optional<Result> result;
        Key key = Key.ofText(USER_EMAIL, userEmail);
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return User.builder()
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .id(resultObject.getBigInt(ID))
                        .name(resultObject.getText(NAME))
                        .password(resultObject.getText(PASSWORD))
                        .roleJson(resultObject.getText(ROLE_JSON))
                        .orgId(resultObject.getText(ORG_ID))
                        .organizationName(resultObject.getText(ORGANISATION_NAME))
                        .imageUrl(resultObject.getText(IMAGE_URL))
                        .isDeleted(resultObject.getBoolean(IS_DELETED))
                        .isBoxAdmin(resultObject.getBoolean(IS_BOX_ADMIN))
                        .refreshToken(resultObject.getText(REFRESH_TOKEN))
                        .refreshTokenExpiry(resultObject.getBigInt(REFRESH_TOKEN_EXPIRY))
                        .languageCode(resultObject.getText(LANGUAGE_CODE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public User createAndDelete(User user, String userEmailId, DistributedTransaction transaction) {

        Key key = Key.ofText(USER_EMAIL, user.getUserEmail());

        Key keyToDelete = Key.ofText(USER_EMAIL, userEmailId);
        try {

            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .bigIntValue(ID, user.getId())
                    .textValue(NAME, user.getName())
                    .textValue(PASSWORD, user.getPassword())
                    .textValue(ROLE_JSON, user.getRoleJson())
                    .textValue(ORG_ID, user.getOrgId())
                    .textValue(ORGANISATION_NAME, user.getOrganizationName())
                    .textValue(IMAGE_URL, user.getImageUrl())
                    .booleanValue(IS_DELETED, user.getIsDeleted())
                    .booleanValue(IS_BOX_ADMIN, user.getIsBoxAdmin())
                    .textValue(REFRESH_TOKEN, user.getRefreshToken())
                    .bigIntValue(REFRESH_TOKEN_EXPIRY, user.getRefreshTokenExpiry())
                    .textValue(LANGUAGE_CODE,user.getLanguageCode())
                    .build();


            Delete delete = Delete.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(keyToDelete)
                    .build();
            transaction.mutate(Arrays.asList(put, delete));
            return user;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public User getByUserID(Long userId, DistributedTransaction transaction) {

        Optional<Result> result;
        Key key = Key.ofBigInt(ID, userId);
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .indexKey(key)
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return User.builder()
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .id(resultObject.getBigInt(ID))
                        .name(resultObject.getText(NAME))
                        .password(resultObject.getText(PASSWORD))
                        .roleJson(resultObject.getText(ROLE_JSON))
                        .orgId(resultObject.getText(ORG_ID))
                        .organizationName(resultObject.getText(ORGANISATION_NAME))
                        .imageUrl(resultObject.getText(IMAGE_URL))
                        .isDeleted(resultObject.getBoolean(IS_DELETED))
                        .isBoxAdmin(resultObject.getBoolean(IS_BOX_ADMIN))
                        .refreshToken(resultObject.getText(REFRESH_TOKEN))
                        .refreshTokenExpiry(resultObject.getBigInt(REFRESH_TOKEN_EXPIRY))
                        .languageCode(resultObject.getText(LANGUAGE_CODE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }
}
