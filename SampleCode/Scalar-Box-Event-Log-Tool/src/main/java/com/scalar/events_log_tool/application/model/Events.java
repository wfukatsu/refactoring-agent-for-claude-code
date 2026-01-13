package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Events {
    private String yyyyMMdd;
    private String eventId;
    private String timestamp;
    private Long userId;
    private String userName;
    private String userEmail;
    private String assetId;
    private Integer assetAge;
    private Long itemId;
    private Long itemVersionId;
    private String sha1Hash;
    private String eventType;
    private Long createdAt;
    private String eventOccuredOn;
    private Long parentFolderId;
    private String sourceJson;
}
