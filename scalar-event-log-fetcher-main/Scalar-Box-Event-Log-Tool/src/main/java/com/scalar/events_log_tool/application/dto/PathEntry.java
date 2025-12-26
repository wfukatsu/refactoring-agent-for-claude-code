package com.scalar.events_log_tool.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathEntry {
    private String type;
    private String id;
    @JsonProperty("sequence_id")
    private String sequenceId;
    private String etag;
    private String name;

}
