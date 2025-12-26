package com.scalar.events_log_tool.application.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ItemRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "item";
    private static final String ID = "item_id";
    private static final String ITEM_TYPE = "item_type";
    private static final String ITEM_HASH = "item_hash";
    private static final String IS_DELETED = "is_deleted";
    private static final String SIZE = "size";
    private static final String AUDITED_BY = "audited_by";
    private static final String AUDITED_AT = "audited_at";
    private static final String AUDIT_STATUS = "audit_status";
    private static final String STORAGE_PROVIDER = "storage_provider";

}
