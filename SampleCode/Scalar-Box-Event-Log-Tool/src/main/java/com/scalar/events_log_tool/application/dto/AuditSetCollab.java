package com.scalar.events_log_tool.application.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditSetCollab {

    private String auditSetId;
    private String userEmail;
    private String auditSetRole;
}
