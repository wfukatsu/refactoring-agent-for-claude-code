package com.scalar.events_log_tool.application.repository;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.Get;
import com.scalar.db.api.Put;
import com.scalar.db.api.Result;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.io.Key;
import com.scalar.events_log_tool.application.model.ItemStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Slf4j
@Repository
public class ItemStatusRepository {
    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "item_status";
    private static final String ID = "item_id";
    private static final String STATUS = "status";
    private static final String LAST_VALIDATED_AT = "last_validated_at";
    private static final String MONITORED_STATUS = "monitored_status";
    private static final String LIST_OF_AUDITSET_JSON = "list_of_auditset_json";
    private static final String ITEM_TYPE = "item_type";


    public ItemStatus create(ItemStatus itemStatus, DistributedTransaction transaction) {

        Key key = Key.ofBigInt(ID, itemStatus.getItemId());
        try {
            Put put = Put.newBuilder()
                    .namespace(NAMESPACE)
                    .table(TABLE_NAME)
                    .partitionKey(key)
                    .textValue(STATUS, itemStatus.getStatus())
                    .textValue(LAST_VALIDATED_AT, itemStatus.getLastValidatedAt())
                    .textValue(MONITORED_STATUS, itemStatus.getMonitoredStatus())
                    .textValue(ITEM_TYPE, itemStatus.getItemType())
                    .textValue(LIST_OF_AUDITSET_JSON, itemStatus.getListOfAuditsetJson())
                    .build();
            transaction.put(put);
            return itemStatus;
        } catch (CrudException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ItemStatus get(Long itemId, DistributedTransaction transaction) {
        Optional<Result> result;
        Key key = Key.ofBigInt(ID, itemId);

        Get get = Get.newBuilder()
                .namespace(NAMESPACE)
                .table(TABLE_NAME)
                .partitionKey(key)
                .build();
        try {
            result = transaction.get(get);
            if (result.isPresent()) {
                Result resultObject = result.get();
                return ItemStatus.builder()
                        .itemId(resultObject.getBigInt(ID))
                        .status(resultObject.getText(STATUS))
                        .lastValidatedAt(resultObject.getText(LAST_VALIDATED_AT))
                        .itemType(resultObject.getText(ITEM_TYPE))
                        .listOfAuditsetJson(resultObject.getText(LIST_OF_AUDITSET_JSON))
                        .monitoredStatus(resultObject.getText(MONITORED_STATUS))
                        .build();
            }
        } catch (CrudException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


}
