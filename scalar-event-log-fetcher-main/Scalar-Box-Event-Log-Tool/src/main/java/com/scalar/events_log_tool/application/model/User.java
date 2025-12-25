package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private String userEmail;
    private Long id;
    private String name;
    private String password;
    private String roleJson;
    private String orgId;
    private String organizationName;
    private String imageUrl;
    private Boolean isDeleted;
    private Boolean isBoxAdmin;
    private String refreshToken;
    private Long refreshTokenExpiry;
    private String languageCode;
}
