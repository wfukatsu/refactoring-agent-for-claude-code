package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SourceObject {
    private String item_type;
    private String item_id;
    private String item_name;
    private Parent parent;
    private OwnedBy owned_by;
}
