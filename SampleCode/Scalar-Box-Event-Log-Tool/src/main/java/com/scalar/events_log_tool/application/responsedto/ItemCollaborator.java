package com.scalar.events_log_tool.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemCollaborator {
    private String name;
    private String userEmail;
    private String userId;

}
