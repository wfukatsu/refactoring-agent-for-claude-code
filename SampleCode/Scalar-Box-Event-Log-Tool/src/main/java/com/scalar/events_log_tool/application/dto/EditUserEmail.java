package com.scalar.events_log_tool.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditUserEmail {
    @Email(message = "{com.email.notValid}", regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$")
    private String userEmail;

    @Size(max = 64)
    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsHiragana}\\p{IsKatakana}\\p{InCJKUnifiedIdeographs}ー・様\\s]*$", message = "{com.Name.notChar}")
    private String name;

    @Size(max = 64)
    private String organizationName;
}
