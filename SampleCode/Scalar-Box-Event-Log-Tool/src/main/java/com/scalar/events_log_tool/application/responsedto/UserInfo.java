package com.scalar.events_log_tool.application.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private String userEmail;
    private Long id;
    private String name;
    private String password;
    private List<String> roleJson;
    private String organizationName;
    private String imageUrl;
}
