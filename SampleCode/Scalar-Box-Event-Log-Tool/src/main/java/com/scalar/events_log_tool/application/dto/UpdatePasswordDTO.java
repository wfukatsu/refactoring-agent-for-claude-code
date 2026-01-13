package com.scalar.events_log_tool.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordDTO {
    private String otp;
    private String userEmail;
    @NotBlank(message = "{com.password.notEmpty}")
    @Size(min = 8, message = "{com.password.contains}")
    private String newPassword;
}
