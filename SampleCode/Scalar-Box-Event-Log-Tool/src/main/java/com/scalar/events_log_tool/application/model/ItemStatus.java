package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemStatus {
    private Long itemId;
    private String status;
    private String lastValidatedAt;
    private String itemType;
    private String monitoredStatus;
    private String listOfAuditsetJson;

}
