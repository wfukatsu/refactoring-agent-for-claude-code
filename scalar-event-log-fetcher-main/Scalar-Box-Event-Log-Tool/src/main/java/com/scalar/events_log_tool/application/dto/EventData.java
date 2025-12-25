package com.scalar.events_log_tool.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventData {
    private String type;
    private String id;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("item_version_id")
    private long itemVersionId;

    private String sha1;
    private String name;
    private String description;
    private int size;

    @JsonProperty("created_by_user_id")
    private String createdByUserId;

    @JsonProperty("created_by_user_name")
    private String createdByUserName;

    @JsonProperty("created_by_user_email")
    private String createdByUserEmail;

    @JsonProperty("created_at_date")
    private String createdAtDate;

    @JsonProperty("modified_by_user_id")
    private String modifiedByUserId;

    @JsonProperty("modified_by_user_name")
    private String modifiedByUserName;

    @JsonProperty("modified_by_user_email")
    private String modifiedByUserEmail;

    @JsonProperty("modified_at_date")
    private String modifiedAtDate;


}