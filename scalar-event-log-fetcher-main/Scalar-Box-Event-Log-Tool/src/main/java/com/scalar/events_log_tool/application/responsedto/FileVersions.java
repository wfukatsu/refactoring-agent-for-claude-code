package com.scalar.events_log_tool.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileVersions {

    private String uploaderName;
    private String sha1Hash;
    private String itemVersionId;
    private Long itemVersionNumber;
    private String itemName;
    private Long modifiedAt;
}
