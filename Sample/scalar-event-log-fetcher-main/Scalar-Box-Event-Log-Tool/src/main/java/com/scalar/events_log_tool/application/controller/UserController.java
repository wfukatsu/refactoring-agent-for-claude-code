package com.scalar.events_log_tool.application.controller;


import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.business.UserBusiness;
import com.scalar.events_log_tool.application.constant.LanguageSupported;
import com.scalar.events_log_tool.application.dto.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/box/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserBusiness userBusiness;

    public UserController(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    /**
     * Author: Mayuri
     * Description: This API enables to create user
     */
    @Operation(summary = "Create API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/createUser")
    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("create User called  ");
        ApiResponse apiResponse = userBusiness.createUser(userDto);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    /**
     * Author: Jayesh
     * Description: This API is designed for to get users of specific organization using orgId.
     */
    @Operation(summary = "getManagedUsers API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getManagedUsers/{orgId}")
    public ResponseEntity<ApiResponse> getManagedUsers(@PathVariable("orgId") String orgId) throws TransactionException {
        log.info("Get managed user's called");
        ApiResponse apiResponse = userBusiness.getManagedUsers(orgId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    /**
     * Author: Mayuri
     * Description: This API deletes user using emailId.
     */
    @Operation(summary = "deleteUser API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/deleteUser")
    public ResponseEntity<ApiResponse> deleteUser(@RequestParam("user_email_Id") String toBeDeletedUser) {
        log.info("delete user called");
        ApiResponse apiResponse = userBusiness.deleteUser(toBeDeletedUser, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("Login API Called");
        ApiResponse apiResponse = userBusiness.login(loginRequest);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    /**
     * Author: Jayesh
     * Description: This API is designed for updating user roles.
     * Updates the roles of a user and performs role addition or deletion based on the provided new roles.
     */
    @PutMapping("/updateUserRole/{userEmail}")
    @Operation(summary = "updateUserRole API endpoint", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable("userEmail") String userEmail, @RequestBody ListNewRoles newRoles) {
        log.info("update User Role called  ");
        ApiResponse apiResponse = userBusiness.updateRole(userEmail, newRoles.getNewRoles());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Jayesh
     * Description: This API is designed for get list of ExternalAuditors
     */
    @Operation(summary = "getListOfExternalAuditors API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getListOfExternalAuditors")
    public ResponseEntity<ApiResponse> getListOfExternalAuditors() {

        log.info("Get list of external auditors called");
        ApiResponse apiResponse = userBusiness.getListOfExternalAuditors();
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Operation(summary = "submitToken API endpoint")
    @PostMapping("/submitToken")
    public ResponseEntity<ApiResponse> submitToken(@RequestBody SubmitToken submitToken) {
        log.info("submitToken API called  ");
        ApiResponse apiResponse = userBusiness.submitToken(submitToken);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Operation(summary = "getToken API endpoint")
    @PostMapping("/userSignIn/{username}")
    public ResponseEntity<ApiResponse> userSignIn(@PathVariable("username") String username) {
        log.info("getToken API called  ");
        ApiResponse apiResponse = userBusiness.getToken(username);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Jayesh
     * Description: This API is designed for get list of organization
     */
    @Operation(summary = "getOrgList API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getOrgList")
    public ResponseEntity<ApiResponse> getOrgList() {

        log.info("Get list of organization called");
        ApiResponse apiResponse = userBusiness.getOrgList();
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Jayesh
     * Description: This API is designed for to send reset password otp on email
     */

    @GetMapping("/sendResetPasswordOTP")
    public ResponseEntity<ApiResponse> sendResetPasswordOTP(@Email(message = "Enter valid email") @RequestParam("email") String email) {
        log.info("Request to Reset password..");
        ApiResponse apiResponse = userBusiness.sendResetPasswordOTP(email);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }

    /**
     * Author: Jayesh
     * Description: This API is designed for to update password
     */

    @PostMapping("/forgotPassword")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody @Valid UpdatePasswordDTO updatePasswordDTO) {
        log.info("forgotPassword api called");
        ApiResponse apiResponse = userBusiness.forgotPassword(updatePasswordDTO);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }

    /**
     * Author: Mayuri
     * Description: This API edit user using emailId.
     */
    @Operation(summary = "editUser API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/editUser")
    public ResponseEntity<ApiResponse> editUserEmail(@Valid @RequestBody EditUserEmail editUserEmail,
                                                     @RequestParam("previous_email_id") String previousMailId) {
        log.info("edit User email called  ");
        ApiResponse apiResponse = userBusiness.editUser(editUserEmail, previousMailId, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Operation(summary = "getServiceAccToken API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getServiceAccToken")
    public ResponseEntity<ApiResponse> getServiceAccToken() {
        log.info("Request to getServiceAccToken");
        ApiResponse apiResponse = userBusiness.getServiceAccToken();
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }

    @PostMapping("/getNewAccessToken")
    public ResponseEntity<ApiResponse> getNewAccessToken(@RequestBody RefreshToken refreshToken) {
        log.info("Request to getNewAccessToken");
        ApiResponse apiResponse = userBusiness.getNewAccessToken(refreshToken);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }


    @Operation(summary = "Get All Languages Supported By Application", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getAllLanguagesSupported")
    public ResponseEntity<ApiResponse> getLanguageSupported() {


        ApiResponse apiResponse = ApiResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("")
                .status(true)
                .data(Arrays.stream(LanguageSupported.values())
                        .map(lang -> {
                                    return Language.builder()
                                            .language(lang.name())
                                            .code(lang.getCode())
                                            .build();
                                }
                        ).collect(Collectors.toList()))
                .build();

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }


    @Operation(summary = "update Language for user", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/updateLanguageForUser")
    public ResponseEntity<ApiResponse> updateLanguageForUser(@RequestParam("lang") String lang) {
        log.info("Request to update the language ");
        ApiResponse apiResponse = userBusiness.updateLanguageForUser(lang);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }




}


