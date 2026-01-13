package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItem {

    private Long itemId;
    private String itemType;
    private String itemName;
    private String accessListType;
    private List<BasicItemInfo> items;

}
