package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditGrpAuditSetMapping {
    private String AuditGroupId;
    private String AuditGroupName;
    private String auditSetId;
    private String auditSetName;
}
