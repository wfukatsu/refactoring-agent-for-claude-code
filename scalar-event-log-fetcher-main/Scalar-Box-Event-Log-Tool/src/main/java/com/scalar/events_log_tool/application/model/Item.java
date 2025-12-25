package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Item {

    private String itemId;
    private String itemType;
    private String itemHash;
    private Boolean isDeleted;
    private Long size;
    private String auditedBy;
    private Long auditedAt;
    private Integer auditStatus;
    private String storageProvider;
}
