package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.UserToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserTokenRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "user_token";
    private static final String USER_EMAIL = "user_email";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String ACCESS_TOKEN_EXPIRY_DATE = "access_token_expiry_date";

    public UserToken create(UserToken userToken, DistributedTransaction transaction) {
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(USER_EMAIL, userToken.getUserEmail()))
                    .textValue(REFRESH_TOKEN, userToken.getRefreshToken())
                    .textValue(ACCESS_TOKEN, userToken.getAccessToken())
                    .textValue(ACCESS_TOKEN_EXPIRY_DATE, userToken.getAccessTokenExpiryDate())
                    .build();
            transaction.put(put);
            return userToken;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }

    public UserToken getUserToken(String userEmail, DistributedTransaction transaction) {
        Optional<Result> result;
        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofText(USER_EMAIL, userEmail))
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return UserToken.builder()
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .refreshToken(resultObject.getText(REFRESH_TOKEN))
                        .accessToken(resultObject.getText(ACCESS_TOKEN))
                        .accessTokenExpiryDate(resultObject.getText(ACCESS_TOKEN_EXPIRY_DATE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


}
