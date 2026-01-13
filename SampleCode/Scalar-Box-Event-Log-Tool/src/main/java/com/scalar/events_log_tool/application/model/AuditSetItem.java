package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditSetItem {

    private String auditSetId;
    private Long itemId;
    private String itemName;
    private String itemType;
    private String accessList;
    private String listJson;
    private Long createdAt;
    private Long assignedByUserId;
}
