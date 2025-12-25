package com.scalar.events_log_tool.application.dto;

import com.scalar.events_log_tool.application.responsedto.AuditSetLists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAuditSetItem {

    private String itemName;
    private List<AuditSetLists> auditSetLists;

}
