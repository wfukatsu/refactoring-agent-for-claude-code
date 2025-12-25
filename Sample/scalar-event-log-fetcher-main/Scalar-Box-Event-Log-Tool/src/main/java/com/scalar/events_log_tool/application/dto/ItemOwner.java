package com.scalar.events_log_tool.application.dto;

import com.scalar.events_log_tool.application.responsedto.ItemCollaborator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemOwner {
    private String ownerName;
    private String ownerEmail;
    private String onwerId;
    private List<ItemCollaborator> itemCollaborators;

}
