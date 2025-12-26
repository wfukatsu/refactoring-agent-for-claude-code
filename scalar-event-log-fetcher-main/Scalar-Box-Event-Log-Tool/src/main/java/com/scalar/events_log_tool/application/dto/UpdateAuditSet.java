package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAuditSet {

    private UpdateAuditSetInfo updateAuditSetInfo;
    private UpdateAuditSetCollaborators updateAuditSetCollaborators;

}
