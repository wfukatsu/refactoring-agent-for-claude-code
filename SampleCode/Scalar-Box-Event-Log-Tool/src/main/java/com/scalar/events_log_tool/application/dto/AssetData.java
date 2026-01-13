package com.scalar.events_log_tool.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetData {

    @JsonProperty("file_id")
    private String fileId;

    @JsonProperty("file_version_id")
    private String fileVersionId;

    private String sha1;

    @JsonProperty("event_data")
    private EventData eventData;
}
