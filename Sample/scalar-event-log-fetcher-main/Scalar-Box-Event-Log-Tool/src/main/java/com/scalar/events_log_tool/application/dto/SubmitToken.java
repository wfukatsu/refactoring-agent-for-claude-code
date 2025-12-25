package com.scalar.events_log_tool.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Abhishek
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitToken {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private Integer expiresIn = 0;

}
