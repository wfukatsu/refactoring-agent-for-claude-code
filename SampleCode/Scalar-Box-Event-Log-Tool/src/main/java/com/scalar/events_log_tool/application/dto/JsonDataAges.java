package com.scalar.events_log_tool.application.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

public class JsonDataAges {

    private Map<String, Integer> itemsMap = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Integer> getItemsMap() {
        return itemsMap;
    }

    @JsonAnySetter
    public void setItemsMap(String key, Integer value) {
        itemsMap.put(key, value);
    }
}
