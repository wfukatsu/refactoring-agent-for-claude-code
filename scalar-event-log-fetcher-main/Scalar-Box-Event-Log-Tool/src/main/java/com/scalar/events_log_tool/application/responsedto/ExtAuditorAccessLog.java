package com.scalar.events_log_tool.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExtAuditorAccessLog {
    private String ownerName;
    private String eventType;
    private String itemType;
    private Long eventDate;
}
