package com.scalar.events_log_tool.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserToken {

    private String userEmail;
    private String refreshToken;
    private String accessToken;
    private String accessTokenExpiryDate;
}
