package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EventDetails {
    private String eventId;
    private String eventType;
    private String eventCreatedUserName;
    private Long eventCreatedUserId;
    private String eventCreatedAt;
    private Long itemId;
    private String itemName;

}
