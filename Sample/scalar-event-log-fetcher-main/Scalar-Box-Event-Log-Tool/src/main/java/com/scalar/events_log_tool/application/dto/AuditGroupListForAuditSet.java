package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditGroupListForAuditSet {
    private String auditGroupId;
    private String auditGroupName;
    private String description;
}
