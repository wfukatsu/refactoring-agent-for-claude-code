package com.scalar.events_log_tool.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditGroupInfo {
    private String auditGroupId;
    private String auditGroupName;
    private String description;
    private Integer memberCount;
    private Long createdAt;
    private String ownedBy;

}
