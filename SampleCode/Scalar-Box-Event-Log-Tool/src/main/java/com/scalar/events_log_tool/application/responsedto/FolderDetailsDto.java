package com.scalar.events_log_tool.application.responsedto;

import com.scalar.events_log_tool.application.dto.OwnedBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FolderDetailsDto {
    private Long id;
    private String name;
    private String type;
    private String description;
    private String createdAt;
    private String modifiedAt;
    private String size;
    private OwnedBy ownedBy;
    private OwnedBy modifiedBy;
    private String path;

}
