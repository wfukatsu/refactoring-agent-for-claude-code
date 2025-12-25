package com.scalar.events_log_tool.application.responsedto;

import com.scalar.events_log_tool.application.dto.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CopyItems {

    private String sha1Hash;
    private long itemId;
    private long itemVersionId;
    private Integer itemVersionNumber;
    private String itemName;
    private String path;
    private Long createdAt;
    private Boolean isDeleted;
}
