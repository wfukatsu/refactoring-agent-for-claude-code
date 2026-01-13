package com.scalar.events_log_tool.application.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAuditSetInfo {
    @Size(max = 64)
    private String auditSetName;
    private String description;
}
