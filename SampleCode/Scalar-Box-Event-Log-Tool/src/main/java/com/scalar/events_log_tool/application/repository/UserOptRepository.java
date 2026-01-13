package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.model.UserOtp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
public class UserOptRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "user_otp";
    private static final String USER_EMAIL = "user_email";
    private static final String OTP = "otp";
    private static final String EXPIRY_DATE = "expiry_date";

    public UserOtp create(UserOtp userOtp, DistributedTransaction transaction) {
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(Key.ofText(USER_EMAIL, userOtp.getUserEmail()))
                    .textValue(OTP, userOtp.getOtp())
                    .bigIntValue(EXPIRY_DATE, userOtp.getExpiryDate())
                    .build();
            transaction.put(put);
            return userOtp;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;

    }
    public UserOtp getUserOtp(String userEmail, DistributedTransaction transaction) {
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
                return UserOtp.builder()
                        .userEmail(resultObject.getText(USER_EMAIL))
                        .otp(resultObject.getText(OTP))
                        .expiryDate(resultObject.getBigInt(EXPIRY_DATE))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
