package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuditGroup {
    private String userEmail;
    private String auditGroupId;
    private String auditGroupName;
    private String privilege;
}
