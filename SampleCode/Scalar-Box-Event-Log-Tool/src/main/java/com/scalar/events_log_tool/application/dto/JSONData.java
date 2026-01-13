package com.scalar.events_log_tool.application.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ToString
public class JSONData {

    private Map<String, List<ItemEntry>> itemsMap = new HashMap<>();

    @JsonAnyGetter
    public Map<String, List<ItemEntry>> getItemsMap() {
        return itemsMap;
    }

    @JsonAnySetter
    public void setItemsMap(String key, List<ItemEntry> value) {
        itemsMap.put(key, value);
    }
}
