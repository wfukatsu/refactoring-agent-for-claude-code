package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomEventData {


    private String type;
    private String id;
    private String eventId;
    private Long itemVersionId;
    private String itemCreatedAtDate;
    private String itemModifiedAtDate;
    private String sha1;
    private String name;
    private String description;
    private int size;
    private User createdByUser;
    private User modifiedByUser;
    private Long eventCreatedUserId;
    private String eventCreatedUserEmail;
    private String eventCreatedUserName;


}
