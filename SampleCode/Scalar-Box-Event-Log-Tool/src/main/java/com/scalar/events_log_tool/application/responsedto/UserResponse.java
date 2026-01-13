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
public class UserResponse {

    private String userEmail;
    private String name;
    private String jwtToken;
    private List<String> userRoles;
    private String refreshToken;
    private String accessToken;
    private String serviceAccAccessToken;
    private String orgId;
    private String jwtTokenRefreshToken;
    private Integer jwtTokenExpiresIn;
    private String languageCode;

}
