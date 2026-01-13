package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnterpriseEventLogs {
    private String eventId;
    private String eventType;
    private String eventCreatedUserId;
    private String eventCreatedUserName;
    private Long createdAt;
    private String eventOccuredOn;
    private String userId;
    private String itemId;
    private String hash;
    private String path;
}
