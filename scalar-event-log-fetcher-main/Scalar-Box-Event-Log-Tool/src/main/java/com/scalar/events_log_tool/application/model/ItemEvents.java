package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemEvents {
    private Long itemId;
    private Long itemVersionId;
    private String eventId;
    private String eventType;
    private Long eventDate;
    private String eventJsonData;
}
