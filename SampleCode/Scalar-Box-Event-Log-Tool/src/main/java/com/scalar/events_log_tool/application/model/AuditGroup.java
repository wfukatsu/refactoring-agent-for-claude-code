package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditGroup {
    private String userEmail;
    private String auditGroupId;
    private String auditGroupName;
    private String description;
    private Long ownerId;
    private String ownerName;
    private String memberListJson;
    private Long createdAt;
    private Boolean isDeleted;


}
