package com.scalar.events_log_tool.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditSetLists {
    private String auditSetId;
    private String auditSetName;
    private String description;
    private String ownedBy;
    private Long createdAt;
    private String accessStatus;
    private Boolean isFavourite;
    private Boolean isItemIdAdded;
}
