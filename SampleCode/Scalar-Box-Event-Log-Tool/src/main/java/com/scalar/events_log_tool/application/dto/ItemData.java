package com.scalar.events_log_tool.application.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemData {

    private String type;
    private String id;
    @JsonProperty("file_version")
    private FileVersion fileVersion;
    @JsonProperty("sequence_id")
    private String sequenceId;
    private String etag;
    private String sha1;
    private String name;
    private String description;
    private int size;
    @JsonProperty("path_collection")
    private PathCollection pathCollection;
    @JsonProperty("created_at")
    private Date createdAt;
    @JsonProperty("modified_at")
    private Date modifiedAt;
    @JsonProperty("trashed_at")
    private String trashedAt;
    @JsonProperty("purged_at")
    private String purgedAt;
    @JsonProperty("content_created_at")
    private String contentCreatedAt;
    @JsonProperty("content_modified_at")
    private String contentModifiedAt;
    @JsonProperty("created_by")
    private User createdBy;
    @JsonProperty("modified_by")
    private User modifiedBy;
    @JsonProperty("owned_by")
    private User ownedBy;
    @JsonProperty("shared_link")
    private String sharedLink;
    private Parent parent;
    @JsonProperty("item_status")
    private String itemStatus;
    private boolean synced;
    private String folder_upload_email;

}



