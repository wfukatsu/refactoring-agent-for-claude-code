package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.PositionTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Abhishek
 */
@Repository
@Slf4j
public class PositionTrackerRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "position_tracker";
    private static final String ID = "user_id";
    private static final String POSITION = "position";


    public PositionTracker create(PositionTracker positionTracker, DistributedTransaction transaction) {

        Key key = Key.ofBigInt(ID, positionTracker.getUserId());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .textValue(POSITION, positionTracker.getPosition())
                    .build();
            transaction.put(put);
            return positionTracker;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }


    public PositionTracker getPositionByUserId(DistributedTransaction transaction, Long userId) {

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(Key.ofBigInt(ID, userId))
                .build();


        try {
            Optional<Result> result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return PositionTracker.builder()
                        .userId(resultObject.getBigInt(ID))
                        .position(resultObject.getText(POSITION))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }

}
