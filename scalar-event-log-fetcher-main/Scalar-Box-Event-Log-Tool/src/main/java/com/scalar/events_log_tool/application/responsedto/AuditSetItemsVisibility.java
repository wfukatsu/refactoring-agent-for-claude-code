package com.scalar.events_log_tool.application.responsedto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  AuditSetItemsVisibility {
    private Long itemId;
    private String itemName;
    private String itemType;
    private Boolean isAllowed;
    private Long auditSetRootItemId;
    private String createdAt;
    private String modifiedAt;
    private String createdBy;
    private String modifiedBy;
    private String size;
    private String createdByEmail;
    private String modifiedByEmail;
    private String sha1Hash;

}
