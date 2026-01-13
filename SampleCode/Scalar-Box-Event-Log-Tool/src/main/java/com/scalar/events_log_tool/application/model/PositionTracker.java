package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Abhishek
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionTracker {

    private Long userId;
    private String position;
}
