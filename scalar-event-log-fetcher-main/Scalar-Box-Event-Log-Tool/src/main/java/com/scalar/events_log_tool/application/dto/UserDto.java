package com.scalar.events_log_tool.application.dto;


import com.scalar.events_log_tool.application.constant.UserRoles;
import com.scalar.events_log_tool.application.utility.Translator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotEmpty(message = "{com.email.notempty}")
    @Email(message = "{com.email.notValid}", regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$")
    private String userEmail;
    @NotEmpty(message = "{com.notEmpty.name}")
    @Size(max = 64)

    @Pattern(regexp = "^[\\p{IsLatin}\\p{IsHiragana}\\p{IsKatakana}\\p{InCJKUnifiedIdeographs}ー・様\\s]*$", message = "{com.Name.notChar}")
    private String name;



    @NotEmpty
    @Size(min = 8, max = 15, message = "{com.pass.Contain}")
    private String password;
    @NotEmpty(message = "{com.org.nameEmpty}")
    @Size(max = 64)
    private String organizationName;
    @Builder.Default()
    private String role = UserRoles.GENERAL_USER.toString();

}
