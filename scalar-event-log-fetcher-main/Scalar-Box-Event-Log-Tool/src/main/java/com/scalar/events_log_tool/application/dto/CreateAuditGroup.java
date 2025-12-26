package com.scalar.events_log_tool.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAuditGroup {
    @NotEmpty(message = "{com.notEmpty.nameAuditGroup}")
    @Size(max = 64)
    private String auditGroupName;
    private String description;
    private List<AuditGroupUser> auditGroupUserList;
}
