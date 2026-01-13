package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuditorLogs {
    private String auditSetId;
    private Long itemId;
    private String userEmail;
    private String eventType;
    private Long eventDate;
    private String customJsonEventDetails;
    private String itemType;


}
