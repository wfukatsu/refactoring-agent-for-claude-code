package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditSet {
    private String auditSetId;
    private String auditSetName;
    private String description;
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;
    private String aclJson;
    private Boolean isDeleted;
    private Long createdAt;
    private String auditGroupListJson;

}
