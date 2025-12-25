package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ItemsBySha1 {

    private String sha1Hash;
    private long itemId;
    private long itemVersionId;
    private Integer itemVersionNumber;
    private String itemName;
    private String ownerByJson;
    private String path;
    private Long createdAt;
}
