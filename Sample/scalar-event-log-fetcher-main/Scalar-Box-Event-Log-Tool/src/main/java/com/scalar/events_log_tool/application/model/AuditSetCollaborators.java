package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditSetCollaborators {

    private  String auditSetId;
    private String userEmail;
    private  String auditSetName;
    private String userName;
    private String auditSetRole;
    private String accessStatus;
    private Boolean isFavourite;
}
