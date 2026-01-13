package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ItemEntry {
    private int age;
    private AssetData data;
}
