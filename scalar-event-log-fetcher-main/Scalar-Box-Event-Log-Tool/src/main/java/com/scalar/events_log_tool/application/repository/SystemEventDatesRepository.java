package com.scalar.events_log_tool.application.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SystemEventDatesRepository {

    private static final String NAMESPACE = "scalar_box";
    private static final String TABLE_NAME = "system_event_dates";
    private static final String ID = "id";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";
}
