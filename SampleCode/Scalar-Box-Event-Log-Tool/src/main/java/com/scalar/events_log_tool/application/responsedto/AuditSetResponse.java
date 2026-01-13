package com.scalar.events_log_tool.application.responsedto;


import com.scalar.events_log_tool.application.dto.Collaborator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditSetResponse {

    private String auditSetId;
    private String auditSetName;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Collaborator collaborator;
    private Boolean isDeleted;
}
