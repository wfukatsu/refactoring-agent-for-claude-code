package com.scalar.events_log_tool.application.service;


import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.CollaboratorUserRoles;
import com.scalar.events_log_tool.application.constant.LanguageSupported;
import com.scalar.events_log_tool.application.constant.UserRoles;
import com.scalar.events_log_tool.application.dto.*;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.exception.NotFoundException;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.model.*;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.UserInfo;
import com.scalar.events_log_tool.application.responsedto.UserResponse;
import com.scalar.events_log_tool.application.security.JwtHelper;
import com.scalar.events_log_tool.application.security.UserInfoDetails;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.EmailUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final Long otpValidityTime;
    private final UserRepository userRepository;
    private final DistributedTransactionManager transactionManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleUserRepository roleUserRepository;
    private final UserTokenRepository userTokenRepository;
    private final String clientId;
    private final String clientSecret;
    private final UserDetailsService userDetailsService;
    private final JwtHelper jwtHelper;
    private final OrganizationRepository organizationRepository;
    private final EmailUtility emailUtility;
    private final AuditSetRepository auditSetRepository;
    private final AuditSetCollaboratorsRepository auditSetCollaboratorsRepository;
    private final AuditGroupRepository auditGroupRepository;
    private final UserAuditGroupRepository userAuditGroupRepository;
    private final ObjectMapper objectMapper;
    private final AuditorLogsRepository auditorLogsRepository;
    private final UserOptRepository userOptRepository;
    private final BoxUtility boxUtility;
    private final Integer jwtAccessValidity;
    private final Integer jwtRefreshValidity;


    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection connection;

    public UserService(@Value("${otp.validity.time}") Long otpValidityTime, UserRepository userRepository, DistributedTransactionManager transactionManager,
                       PasswordEncoder passwordEncoder, RoleUserRepository roleUserRepository, UserTokenRepository userTokenRepository, @Value("${box.web-app.integration.client-id}") String clientId,
                       @Value("${box.web-app.integration.secret-id}") String clientSecret, UserDetailsService userDetailsService, JwtHelper jwtHelper, OrganizationRepository organizationRepository,
                       EmailUtility emailUtility, AuditSetRepository auditSetRepository, AuditSetCollaboratorsRepository auditSetCollaboratorsRepository, AuditGroupRepository auditGroupRepository,
                       UserAuditGroupRepository userAuditGroupRepository, ObjectMapper objectMapper, AuditorLogsRepository auditorLogsRepository,
                       UserOptRepository userOptRepository, BoxUtility boxUtility,
                       @Value("${jwt.access.token.validity}") Integer jwtAccessValidity, @Value("${jwt.refresh.token.validity}") Integer jwtRefreshValidity) {
        this.otpValidityTime = otpValidityTime;
        this.userRepository = userRepository;
        this.transactionManager = transactionManager;
        this.passwordEncoder = passwordEncoder;
        this.roleUserRepository = roleUserRepository;
        this.userTokenRepository = userTokenRepository;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.userDetailsService = userDetailsService;
        this.jwtHelper = jwtHelper;
        this.organizationRepository = organizationRepository;
        this.emailUtility = emailUtility;
        this.auditSetRepository = auditSetRepository;
        this.auditSetCollaboratorsRepository = auditSetCollaboratorsRepository;
        this.auditGroupRepository = auditGroupRepository;
        this.userAuditGroupRepository = userAuditGroupRepository;
        this.objectMapper = objectMapper;
        this.auditorLogsRepository = auditorLogsRepository;
        this.userOptRepository = userOptRepository;
        this.boxUtility = boxUtility;
        this.jwtAccessValidity = jwtAccessValidity;
        this.jwtRefreshValidity = jwtRefreshValidity;
    }

    public static void commitTransaction(DistributedTransaction transaction) {

        try {
            log.info("Committing transaction...");
            transaction.commit();
        } catch (TransactionException te) {
            log.info("Error While committing transaction");
            te.printStackTrace();
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(Translator.toLocale("com.unable.transactionCommit"));

        }
    }

    private RoleUser createRoleUser(User user, UserDto userDto, DistributedTransaction transaction) {
        RoleUser roleUser = new RoleUser();
        roleUser.setUserEmail(user.getUserEmail());
        roleUser.setUserId(user.getId());
        roleUser.setUserName(user.getName());
        roleUser.setRoleName(userDto.getRole());

        roleUserRepository.create(roleUser, transaction);

        return roleUser;
    }

    public ApiResponse createUser(UserDto userDto, DistributedTransaction transaction) {
        // check user is select role
        if (userDto.getRole().contains("string")) {
            throw new GenericException(Translator.toLocale("com.invalid.role"));
        }

        try {
            // get user by mail
            User existingUser = userRepository.getByUserEmail(userDto.getUserEmail(), transaction);

            // fetch user role from token for authority to create user
            List<String> authorities = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities()
                    .stream()
                    .map(e -> e.getAuthority())
                    .collect(Collectors.toList());

            if (existingUser == null || existingUser.getIsDeleted().equals(true)) {


                // check role for creating user
                if (authorities.contains(UserRoles.AUDIT_ADMIN.toString())) {
                    // Check if the userDto.getName() is non-null and doesn't exceed 64 chars
                    if (userDto.getName() == null || userDto.getName().isEmpty() || userDto.getName().length() > 64) {
                        throw new GenericException(Translator.toLocale("com.invalid.userName"));
                    }

                    // Check if the password in userDto is non-null and not empty
                    if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                        throw new GenericException(Translator.toLocale("com.invalid.password"));
                    }

                    if (existingUser != null) {
                        // If the user exists and is deleted, update the existing user
                        existingUser.setIsDeleted(false);
                        existingUser.setName(userDto.getName());
                        existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
                        existingUser.setRoleJson(GenericUtility.convertObjectToStringJson(Collections.singletonList(userDto.getRole())));
                        existingUser.setOrganizationName(userDto.getOrganizationName());
                        existingUser.setLanguageCode(LanguageSupported.English.getCode());

                        createRoleUser(existingUser, userDto, transaction);
                        // Update the existing user in the repository
                        userRepository.create(existingUser, transaction);
                        // set data in role user table

                    } else {
                        // If the user doesn't exist, create a new one
                        List<String> generalUser = Collections.singletonList(userDto.getRole());
                        String userJsonObject = GenericUtility.convertObjectToStringJson(generalUser);

                        // set data in user table
                        User user = User.builder()
                                .userEmail(userDto.getUserEmail())
                                .id(null)
                                .name(userDto.getName())
                                .password(passwordEncoder.encode(userDto.getPassword()))
                                .roleJson(userJsonObject)
                                .organizationName(userDto.getOrganizationName())
                                .imageUrl("https://sample-test2-images.s3.ap-south-1.amazonaws.com/user.png")
                                .isDeleted(false)
                                .isBoxAdmin(false)
                                .languageCode(LanguageSupported.English.getCode())
                                .build();

                        createRoleUser(user, userDto, transaction);
                        // set data in user table
                        userRepository.create(user, transaction);

                    }

                    return new ApiResponse(true, Translator.toLocale("com.user.save"), HttpStatus.OK, null);
                } else {
                    return new ApiResponse(false, Translator.toLocale("com.userNot.role"), HttpStatus.BAD_REQUEST, null);
                }
            } else {
                return new ApiResponse(false, Translator.toLocale("user.already.exit"), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            log.error("Error creating user: " + e.getMessage(), e);
            return new ApiResponse(false, Translator.toLocale("user.failed.create"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    public ApiResponse getManagedUsers(String orgId, DistributedTransaction transaction) throws TransactionException {

        List<User> userList = userRepository.getOrgUserList(orgId, transaction);

        // Filter out deleted users
        userList = userList.stream()
                .filter(user -> !user.getIsDeleted())
                .filter(user -> !user.getRoleJson().contains("EXTERNAL_AUDITOR"))
                .collect(Collectors.toList());

        List<UserInfo> users = new ArrayList<>();

        List<String> roleList = new ArrayList<>();
        for (User user : userList) {
            // Convert JSON to object for fetching roles
            try {
                String roleJson = user.getRoleJson();
                {
                    // JSON array
                    roleList = objectMapper.readValue(roleJson, new TypeReference<List<String>>() {
                    });
                }
            } catch (JsonProcessingException e) {
                throw new GenericException("Error converting JSON to Object");
            }

            UserInfo userInfo = UserInfo.builder()
                    .id(user.getId())
                    .userEmail(user.getUserEmail())
                    .name(user.getName())
                    .password(user.getPassword())
                    .roleJson(roleList)
                    .organizationName(user.getOrganizationName())
                    .imageUrl(user.getImageUrl())
                    .build();

            users.add(userInfo);
        }

        if (!users.isEmpty())
            return new ApiResponse(true, "", HttpStatus.OK, users);
        else
            return new ApiResponse(false, Translator.toLocale("com.userList.empty"), HttpStatus.OK, users);
    }


    public ApiResponse deleteUser(String toBeDeletedUser, String adminEmail, DistributedTransaction transaction) throws CrudException {

        User user = userRepository.getByUserEmail(toBeDeletedUser, transaction);

        if (user == null) {
            throw new GenericException(Translator.toLocale("com.user.notFound"));
        }
        //get user by get user email
        User user1 = userRepository.getByUserEmail(adminEmail, transaction);
        //check user is present or not
        if (user1 == null) {
            throw new GenericException(Translator.toLocale("com.user.notFound"));
        }
        //fetch role from token for authority to delete user
        List<String> collect = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());
        // Convert JSON to object for fetching roles
        List<String> roleList;
        try {

            roleList = objectMapper.readValue(user.getRoleJson(), new TypeReference<List<String>>() {
            });

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to Object", e);
        }

        for (String roleName : roleList) {
            // Perform some operation with the roleName
            roleUserRepository.get(roleName, toBeDeletedUser, transaction);
            roleUserRepository.delete(roleName, toBeDeletedUser, transaction);
        }

        //check role for delete user
        if (collect.contains(UserRoles.AUDIT_ADMIN.toString())) {
            user.setIsDeleted(true);
            userRepository.create(user, transaction);

            // Process audit sets and groups
            try {
                processAuditSetsForUser(toBeDeletedUser, transaction);
                processUserGroupsForUser(toBeDeletedUser, transaction);

            } catch (Exception e) {
                throw new GenericException(Translator.toLocale("com.unexpected.error"));
            }
            return new ApiResponse(true, Translator.toLocale("com.user.deleted"), HttpStatus.OK, null);
        } else {
            throw new GenericException(Translator.toLocale("com.userNot.role.delete"));
        }

    }

    private void processAuditSetsForUser(String toBeDeletedUser, DistributedTransaction transaction) throws JsonProcessingException, CrudException {
        List<AuditSetCollaborators> auditSetCollaboratorList = auditSetCollaboratorsRepository.getAuditSetCollaboratorList(toBeDeletedUser, transaction);
        for (AuditSetCollaborators auditSetCollaborator : auditSetCollaboratorList) {
            processCollaboratorForAuditSet(toBeDeletedUser, auditSetCollaborator.getAuditSetId(), transaction);
        }
    }

    private void processCollaboratorForAuditSet(String toBeDeletedUser, String auditSetId, DistributedTransaction transaction) throws JsonProcessingException {
        AuditSet auditSet = auditSetRepository.get(auditSetId, transaction);
        processCollaboratorForAuditSet(toBeDeletedUser, auditSet, transaction);
    }

    private void processCollaboratorForAuditSet(String toBeDeletedUser, AuditSet auditSet, DistributedTransaction transaction) throws JsonProcessingException {
        Collaborator collaborator = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);

        if (collaborator.getReviewers() != null && collaborator.getReviewers().stream().anyMatch(reviewer -> toBeDeletedUser.equals(reviewer.getEmailId()))) {
            collaborator.getReviewers().removeIf(reviewer -> toBeDeletedUser.equals(reviewer.getEmailId()));
            String collaboratorJson = GenericUtility.convertObjectToStringJson(collaborator);
            auditSet.setAclJson(collaboratorJson);
            auditSetRepository.create(auditSet, transaction);
            auditSetCollaboratorsRepository.delete(auditSet.getAuditSetId(), CollaboratorUserRoles.REVIEWER.toString(), toBeDeletedUser, transaction);
        }
    }

    private void processUserGroupsForUser(String toBeDeletedUser, DistributedTransaction transaction) throws JsonProcessingException, CrudException {
        List<UserAuditGroup> userGroupList = userAuditGroupRepository.getUserGroupList(toBeDeletedUser, transaction);
        for (UserAuditGroup userGroup : userGroupList) {
            processAuditGroupForUser(toBeDeletedUser, userGroup.getAuditGroupId(), transaction);
        }
    }

    private void processAuditGroupForUser(String toBeDeletedUser, String auditGroupId, DistributedTransaction transaction) throws JsonProcessingException {
        AuditGroup auditGroup = auditGroupRepository.getAuditGroup(auditGroupId, transaction);

        if (auditGroup != null) {
            List<GroupUserPrivileges> memberEmails = objectMapper.readValue(auditGroup.getMemberListJson(), new TypeReference<List<GroupUserPrivileges>>() {
                    }
            );

            if (memberEmails != null) {
                memberEmails.removeIf(privileges -> toBeDeletedUser.equals(privileges.getUserEmail()));
                String collaboratorJson = GenericUtility.convertObjectToStringJson(memberEmails);
                auditGroup.setMemberListJson(collaboratorJson);
                auditGroupRepository.create(auditGroup, transaction);
                userAuditGroupRepository.delete(auditGroupId, toBeDeletedUser, transaction);
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        DistributedTransaction transaction = getNewTransaction();

        User userDetail = userRepository.getByUserEmail(username, transaction);
        commitTransaction(transaction);
        if (userDetail == null) {
            throw new UsernameNotFoundException(Translator.toLocale("com.user.notFound"));
        }
        return new UserInfoDetails(userDetail);
    }

    public DistributedTransaction getNewTransaction() {
        try {
            log.info("Starting transaction...");
            return transactionManager.start();
        } catch (TransactionException te) {
            te.printStackTrace();
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }
    }

    public Boolean login(LoginRequest loginRequest, DistributedTransaction transaction) {

        User user = userRepository.getByUserEmail(loginRequest.getUserEmail(), transaction);

        if (user == null || user.getIsDeleted().equals(true)) {

            throw new GenericException(Translator.toLocale("com.invalid.emailPassword"));
        }
        return true;
    }


    public ApiResponse updateUserRole(String userEmail, List<String> newRoles, DistributedTransaction transaction) {

        // Retrieve the existing user based on the provided email
        User existingUser = userRepository.getByUserEmail(userEmail, transaction);
        // Extract existing roles from the stored JSON
        String roleJson = existingUser.getRoleJson();

        List<String> existingRoles = new ArrayList<>();
        try {
            existingRoles = objectMapper.readValue(roleJson, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // Lists to track roles to be added and deleted
        List<String> addRoleList = new ArrayList<>();
        List<String> deleteRoleList = new ArrayList<>();

        //fetch role from token for authority
        List<String> authorities = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .map(e -> e.getAuthority())
                .collect(Collectors.toList());

        //check access for update user role
        if (authorities.contains(UserRoles.AUDIT_ADMIN.toString())) {
            // Iterate through new roles

            if (existingUser.getIsBoxAdmin()) {
                if (existingRoles.contains(UserRoles.AUDIT_ADMIN.toString()) && !newRoles.contains(UserRoles.AUDIT_ADMIN.toString())) {
                    throw new GenericException(Translator.toLocale("com.auditAdmin.notRemoved"));
                }
            }

            for (String newRole : newRoles) {
                if (!existingRoles.contains(newRole)) {

                    RoleUser roleUser = RoleUser.builder()
                            .roleName(newRole)
                            .userEmail(existingUser.getUserEmail())
                            .userName(existingUser.getName())
                            .build();
                    RoleUser isExist = roleUserRepository.get(roleUser.getRoleName(), roleUser.getUserEmail(), transaction);
                    if (isExist == null) {
                        roleUserRepository.create(roleUser, transaction);
                    }
                    // Update the list of added roles
                    addRoleList.add(newRole);
                }
            }
            // Iterate through existing roles
            for (String existingUserRole : existingRoles) {
                if (!newRoles.contains(existingUserRole)) {

                    // Retrieve the RoleUser for the existing role
                    RoleUser roleUser = roleUserRepository.get(existingUserRole, existingUser.getUserEmail(), transaction);
                    if (roleUser != null) {
                        roleUserRepository.delete(existingUserRole, existingUser.getUserEmail(), transaction);
                    }
                    // Update the list of deleted roles
                    deleteRoleList.add(existingUserRole);
                }
            }
            // Update the user's role JSON representation with the new roles
            String jsonString = null;
            try {
                jsonString = objectMapper.writeValueAsString(newRoles);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            existingUser.setRoleJson(jsonString);
            userRepository.create(existingUser, transaction);
            return new ApiResponse(true, Translator.toLocale("com.userRole.update"), HttpStatus.OK, null);
        }
        return new ApiResponse(true, Translator.toLocale("com.notAccess.updateRole"), HttpStatus.BAD_REQUEST, null);
    }

    public ApiResponse getListOfExternalAuditors(DistributedTransaction transaction) throws TransactionException {

        List<RoleUser> roleUsers = roleUserRepository.getByRole("EXTERNAL_AUDITOR", transaction);

        if (roleUsers.isEmpty()) {
            return new ApiResponse(true, Translator.toLocale("com.userList.empty"), HttpStatus.OK, roleUsers);
        }
        List<UserInfo> collect = roleUsers.stream()
                .map(e -> {
                    return userRepository.getByUserEmail(e.getUserEmail(), transaction);
                })
                .filter(e -> e != null)
                .map(user -> {
                    return UserInfo.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .userEmail(user.getUserEmail())
                            .organizationName(user.getOrganizationName())
                            .build();
                }).collect(Collectors.toList());

        return new ApiResponse(true, "", HttpStatus.OK, collect);

    }

    public User getByUserName(String username, DistributedTransaction transaction) {

        return userRepository.getByUserEmail(username, transaction);
    }

    public ApiResponse registerUserAndSaveToken(BoxUser.Info info, SubmitToken submitToken, DistributedTransaction transaction) {

        //do check if Organization is Registered or not.

        List<String> generalUser = new ArrayList<>();
        generalUser.add(UserRoles.GENERAL_USER.toString());

        if (info.getRole() != null && ("ADMIN".equalsIgnoreCase(info.getRole().toString()))) {
            generalUser.add(UserRoles.AUDIT_ADMIN.toString());
        }

        User userEmail = userRepository.getByUserEmail(info.getLogin(), transaction);
        if (userEmail == null || userEmail.getIsDeleted().equals(true)) {
            User user = User.builder()
                    .userEmail(info.getLogin())
                    .id(Long.parseLong(info.getID()))
                    .name(info.getName())
                    .password(passwordEncoder.encode("password@123"))
                    .roleJson(GenericUtility.convertObjectToStringJson(generalUser))
                    .organizationName(info.getEnterprise().getName())
                    .orgId(info.getEnterprise().getID())
                    .imageUrl("https://sample-test2-images.s3.ap-south-1.amazonaws.com/user.png")
                    .isDeleted(false)
                    .isBoxAdmin("ADMIN".equalsIgnoreCase(info.getRole().toString()))
                    .languageCode(LanguageSupported.English.getCode())
                    .refreshTokenExpiry(System.currentTimeMillis() + jwtRefreshValidity * 1000)
                    .refreshToken(GenericUtility.generateUUID())
                    .build();

            userRepository.create(user, transaction);
        }

        RoleUser roleUserObject = roleUserRepository.get(UserRoles.GENERAL_USER.toString(), info.getLogin(), transaction);
        if (roleUserObject == null) {

            RoleUser roleUser = RoleUser.builder()
                    .userName(info.getName())
                    .userId(Long.parseLong(info.getID()))
                    .roleName(UserRoles.GENERAL_USER.toString())
                    .userEmail(info.getLogin())
                    .build();

            roleUserRepository.create(roleUser, transaction);
        }

        if (info.getEnterprise() == null) {
            throw new GenericException(Translator.toLocale("com.userNotAsso.withOrg"));
        }

        Organization organization = organizationRepository.getOrganization(transaction, info.getEnterprise().getID());

        if (organization == null) {
            organizationRepository.create(Organization.builder()
                    .orgId(info.getEnterprise().getID())
                    .organizationName(info.getEnterprise().getName())
                    .build(), transaction);
        }

        //save Token Info of user.
        // Calculate expiry date and format it
        LocalDateTime expiryDateTime = LocalDateTime.now().plusSeconds(submitToken.getExpiresIn());
        String expiryDateOfAccessToken = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(expiryDateTime);
        UserToken userToken = userTokenRepository.getUserToken(info.getLogin(), transaction);
        if (userToken == null) {

            userTokenRepository.create(UserToken.builder()
                    .refreshToken(submitToken.getRefreshToken())
                    .accessToken(submitToken.getAccessToken())
                    .accessTokenExpiryDate(expiryDateOfAccessToken)
                    .userEmail(info.getLogin())
                    .build(), transaction);

        } else {
            userToken.setRefreshToken(submitToken.getRefreshToken());
            userToken.setAccessToken(submitToken.getAccessToken());
            userToken.setAccessTokenExpiryDate(expiryDateOfAccessToken);
            userTokenRepository.create(userToken, transaction);
        }

        return ApiResponse.builder()
                .status(true)
                .message("")
                .httpStatus(HttpStatus.OK)
                .data(info)
                .build();

    }


    public SubmitToken updateLatestToken(String username, DistributedTransaction transaction) {
        UserToken userToken = userTokenRepository.getUserToken(username, transaction);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        // Parse the formatted date string
        LocalDateTime expiryDateTime = LocalDateTime.parse(userToken.getAccessTokenExpiryDate(), formatter);

        // Get the current date-time
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (currentDateTime.isBefore(expiryDateTime)) {
            //token is still valid
            return SubmitToken.builder()
                    .accessToken(userToken.getAccessToken())
                    .refreshToken(userToken.getRefreshToken())
                    .build();

        } else {

            ResponseEntity<String> responseEntity = GenericUtility.refreshToken(userToken.getRefreshToken(), clientId, clientSecret);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                String responseBody = responseEntity.getBody();
                try {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);

                    // Get the access_token and expiry details from the JSON response
                    String refreshTokenResponse = jsonNode.get("refresh_token").asText();
                    String accessTokenResponse = jsonNode.get("access_token").asText();
                    int expiresIn = jsonNode.get("expires_in").asInt();

                    // Update user token details
                    userToken.setRefreshToken(refreshTokenResponse);
                    userToken.setAccessToken(accessTokenResponse);

                    // Calculate expiry date and format it
                    LocalDateTime expiryDateTime1 = LocalDateTime.now().plusSeconds(expiresIn);
                    String expiryDateOfAccessToken = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(expiryDateTime1);
                    userToken.setAccessTokenExpiryDate(expiryDateOfAccessToken);

                    // Save updated token in the database
                    userTokenRepository.create(userToken, transaction);

                    return SubmitToken.builder()
                            .refreshToken(refreshTokenResponse)
                            .accessToken(accessTokenResponse)
                            .expiresIn(expiresIn)
                            .build();
                } catch (Exception e) {
                    throw new GenericException("Error parsing JSON response");
                }
            } else {
                throw new GenericException("Something Went Wrong !!");
            }
        }
    }


    public ApiResponse getToken(String username, DistributedTransaction transaction) {

        SubmitToken submitToken = updateLatestToken(username, transaction);

        User byUserEmail = userRepository.getByUserEmail(username, transaction);

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);

        final String token = jwtHelper.generateToken(userDetails);
        log.info("submit token" + submitToken);

        boolean needsRefresh = connection.needsRefresh();
        log.info("Does Box Connection needs Refresh Call :" + needsRefresh);

        if (needsRefresh) {
            connection = boxUtility.getBoxEnterpriseConnection();
            log.info("After Box Connection Refresh Status :" + connection.needsRefresh());
        }

        Long refreshTokenExpiry = byUserEmail.getRefreshTokenExpiry();
        Date expiryDate = null;
        if (refreshTokenExpiry != null) {
            expiryDate = new Date(refreshTokenExpiry);
        }
        Date currentDate = new Date();
        String newRefreshToken = byUserEmail.getRefreshToken();
        if (newRefreshToken == null || expiryDate == null || currentDate.after(expiryDate)) {
            newRefreshToken = GenericUtility.generateUUID();
            refreshTokenExpiry = System.currentTimeMillis() + jwtRefreshValidity * 1000;
            byUserEmail.setRefreshToken(newRefreshToken);
            byUserEmail.setRefreshTokenExpiry(refreshTokenExpiry);
            userRepository.create(byUserEmail, transaction);
        }

        return ApiResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("")
                .status(true)
                .data(UserResponse.builder()
                        .jwtToken(token)
                        .refreshToken(submitToken.getRefreshToken())
                        .userEmail(username)
                        .name(byUserEmail.getName())
                        .accessToken(submitToken.getAccessToken())
                        .serviceAccAccessToken(connection.getAccessToken())
                        .userRoles(userDetails.getAuthorities()
                                .stream()
                                .map(e -> e.getAuthority())
                                .collect(Collectors.toList()))
                        .orgId(byUserEmail.getOrgId())
                        .jwtTokenExpiresIn(jwtAccessValidity)
                        .jwtTokenRefreshToken(newRefreshToken)
                        .languageCode(byUserEmail.getLanguageCode() == null ? LanguageSupported.English.getCode() : byUserEmail.getLanguageCode())
                        .build())
                .build();

    }

    public ApiResponse getOrgList(DistributedTransaction transaction) throws TransactionException {

        List<Organization> organizationList = organizationRepository.getOrganizationList(transaction);

        if (organizationList.isEmpty()) {
            return new ApiResponse(true, Translator.toLocale("com.empty.orgList"), HttpStatus.OK, null);
        }
        return new ApiResponse(true, "", HttpStatus.OK, organizationList);
    }

    public User getByUserId(Long userId, DistributedTransaction transaction) {
        return userRepository.getByUserID(userId, transaction);
    }

    public ApiResponse sendResetPasswordOTP(String email, DistributedTransaction transaction) {

        User user = userRepository.getByUserEmail(email, transaction);
        if (user == null || user.getIsDeleted()) {
            return new ApiResponse(false, Translator.toLocale("com.check.validEmail"), HttpStatus.BAD_REQUEST, null);
        }

        List<String> roleList = new ArrayList<>();
        // Convert JSON to object for fetching roles
        try {
            String roleJson = user.getRoleJson();
            {
                // JSON array
                roleList = objectMapper.readValue(roleJson, new TypeReference<List<String>>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting JSON to Object");
        }
        // Check if user has required roles
        if ((roleList.contains(UserRoles.AUDIT_ADMIN.toString()) || roleList.contains(UserRoles.GENERAL_USER.toString()))) {
            return new ApiResponse(false, Translator.toLocale("com.externalAuditor.resetPassword"), HttpStatus.BAD_REQUEST, null);
        }
        // Generate OTP
        String otp = GenericUtility.generateOtp();
        //Send Email
        log.info("sending mail...");

        try {

            emailUtility.sendEmail(email, Translator.toLocale("com.password.reset"), Translator.toLocale("com.onetime.pass") + otp + " " + Translator.toLocale("com.reset.pass"));
        } catch (MessagingException e) {

            e.printStackTrace();
            throw new GenericException("Unable to send email..");
        }

        UserOtp otpObject = userOptRepository.getUserOtp(user.getUserEmail(), transaction);

        if (otpObject != null) {
            otpObject.setOtp(otp);
            otpObject.setExpiryDate(System.currentTimeMillis() + otpValidityTime * 60000);
            userOptRepository.create(otpObject, transaction);
        } else {
            UserOtp otpObj = new UserOtp();
            otpObj.setUserEmail(user.getUserEmail());
            otpObj.setOtp(otp);
            otpObj.setExpiryDate(System.currentTimeMillis() + otpValidityTime * 60000);
            userOptRepository.create(otpObj, transaction);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("userId", user.getId());
        data.put("EmailId", user.getUserEmail());
        data.put("OtpValidityTime", otpValidityTime);
        data.put("OTP", otp);

        return new ApiResponse(true, "", HttpStatus.OK, data);

    }

    public ApiResponse forgotPassword(UpdatePasswordDTO updatePasswordDTO, DistributedTransaction transaction) {
        User user = userRepository.getByUserEmail(updatePasswordDTO.getUserEmail(), transaction);

        if (user == null || user.getIsDeleted()) {

            throw new NotFoundException(Translator.toLocale("com.user.notFound"));
        }

        List<String> roleList = new ArrayList<>();
        // Convert JSON to object for fetching roles
        try {
            String roleJson = user.getRoleJson();
            {
                // JSON array
                roleList = objectMapper.readValue(roleJson, new TypeReference<List<String>>() {
                });
            }
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting JSON to Object");
        }
        // Check if user has required roles
        if ((roleList.contains(UserRoles.AUDIT_ADMIN.toString()) || roleList.contains(UserRoles.GENERAL_USER.toString()))) {
            return new ApiResponse(false, Translator.toLocale("com.externalAuditor.changePassword"), HttpStatus.BAD_REQUEST, null);
        }
        UserOtp userOtp = userOptRepository.getUserOtp(user.getUserEmail(), transaction);

        if (userOtp == null || !updatePasswordDTO.getOtp().equals(userOtp.getOtp())) {
            throw new GenericException(Translator.toLocale("com.invalid.otp"));
        }

        long currentDate = new Date().getTime();

        if (userOtp.getExpiryDate().compareTo(currentDate) < 0) {
            throw new GenericException(Translator.toLocale("com.expired.otp"));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
        userRepository.create(user, transaction);

        return new ApiResponse(true, Translator.toLocale("com.update.password"), HttpStatus.OK, null);
    }


    public ApiResponse editUser(EditUserEmail editUserEmail, String previousMailId, String currentUser, DistributedTransaction transaction) throws CrudException {
        try {
            User currentUserObject = userRepository.getByUserEmail(currentUser, transaction);

            // Check current user
            if (currentUserObject == null) {
                throw new GenericException(Translator.toLocale("com.user.notFound"));
            }

            //fetch authority for updating user details
            List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream()
                    .map(e -> e.getAuthority())
                    .collect(Collectors.toList());

            // Get user to be edited using mailId
            User previousUserObject = userRepository.getByUserEmail(previousMailId, transaction);
            if (previousUserObject == null) {
                throw new GenericException(Translator.toLocale("com.user.notFound"));
            }

            if (editUserEmail.getUserEmail() != null && !previousMailId.equals(editUserEmail.getUserEmail())) {
                User existingUserWithEmail = userRepository.getByUserEmail(editUserEmail.getUserEmail(), transaction);
                if (existingUserWithEmail != null) {
                    return new ApiResponse(false, Translator.toLocale("com.email.alreadyExits"), HttpStatus.BAD_REQUEST, null);
                }
            }

            List<String> roleList;
            try {
                roleList = objectMapper.readValue(previousUserObject.getRoleJson(), new TypeReference<List<String>>() {
                });

            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting JSON to Object", e);
            }

            if (authorities.contains(UserRoles.AUDIT_ADMIN.toString())) {
                boolean isPreviousMailSame = previousMailId.equals(editUserEmail.getUserEmail());
                if (editUserEmail.getUserEmail() != null && editUserEmail.getName() != null) {

                    previousUserObject.setUserEmail(editUserEmail.getUserEmail());
                    previousUserObject.setName(editUserEmail.getName());
                    previousUserObject.setOrganizationName(editUserEmail.getOrganizationName());
                    if (isPreviousMailSame) {
                        userRepository.create(previousUserObject, transaction);
                    } else {
                        userRepository.createAndDelete(previousUserObject, previousMailId, transaction);
                    }
                    for (String roleName : roleList) {
                        RoleUser roleUser = roleUserRepository.get(roleName, previousMailId, transaction);

                        if (isPreviousMailSame) {
                            roleUser.setUserEmail(editUserEmail.getUserEmail());
                            roleUserRepository.create(roleUser, transaction);
                        } else {
                            roleUserRepository.delete(roleName, previousMailId, transaction);
                            roleUser.setUserEmail(editUserEmail.getUserEmail());
                            roleUserRepository.create(roleUser, transaction);
                        }
                    }

                    List<AuditSetCollaborators> auditSetCollaboratorList = auditSetCollaboratorsRepository.getAuditSetCollaboratorList(previousMailId, transaction);

                    for (AuditSetCollaborators auditSetCollaborator : auditSetCollaboratorList) {
                        AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSetCollaborator.getAuditSetId(), auditSetCollaborator.getAuditSetRole(), auditSetCollaborator.getUserEmail(), transaction);

                        if (!isPreviousMailSame) {
                            auditSetCollaboratorsRepository.createAndDeleteUserEmail(auditSetCollaborators, editUserEmail, transaction);
                        }

                        AuditSet auditSet1 = auditSetRepository.get(auditSetCollaborator.getAuditSetId(), transaction);

                        Collaborator collaborator;
                        try {
                            collaborator = objectMapper.readValue(auditSet1.getAclJson(), Collaborator.class);

                            updateCollaboratorEmail(collaborator, previousMailId, editUserEmail);
                            String updatedCollaboratorJson = GenericUtility.convertObjectToStringJson(collaborator);
                            auditSet1.setAclJson(updatedCollaboratorJson);
                            auditSetRepository.create(auditSet1, transaction);

                        } catch (JsonProcessingException e) {
                            throw new GenericException("Error converting JSON to Object");
                        }
                    }
                    List<UserAuditGroup> userGroupList = userAuditGroupRepository.getUserGroupList(previousMailId, transaction);

                    for (UserAuditGroup auditGroup : userGroupList) {
                        UserAuditGroup userAuditGroup = userAuditGroupRepository.get(auditGroup.getAuditGroupId(), previousMailId, transaction);
                        if (!isPreviousMailSame) {
                            userAuditGroupRepository.createAndDeleteUserEmail(userAuditGroup, editUserEmail, transaction);
                        }

                        AuditGroup auditGroup2 = auditGroupRepository.getAuditGroup(userAuditGroup.getAuditGroupId(), transaction);

                        List<GroupUserPrivileges> memberList = getMemberListFromJson(auditGroup2.getMemberListJson());

                        for (GroupUserPrivileges members : memberList) {
                            updateMemberEmail(members, previousMailId, editUserEmail);
                        }

                        String updatedJson = GenericUtility.convertObjectToStringJson(memberList);
                        auditGroup2.setMemberListJson(updatedJson);
                        auditGroupRepository.create(auditGroup2, transaction);
                    }


                    if (!isPreviousMailSame) {
                        List<AuditorLogs> listOfAuditorLog = auditorLogsRepository.getListOfAuditorLog(previousMailId, transaction);
                        for (AuditorLogs auditorLogs : listOfAuditorLog) {
                            AuditorLogs auditorLogsObj = auditorLogsRepository.getAuditorLogsByPrimaryKey(auditorLogs.getAuditSetId(), auditorLogs.getItemId(), auditorLogs.getUserEmail(), auditorLogs.getEventDate(), transaction);
                            if (auditorLogsObj != null) {
                                auditorLogsRepository.createAnddeleteByUserMail(auditorLogsObj, editUserEmail.getUserEmail(), transaction);
                            }
                        }
                    }

                }
                return new ApiResponse(true, Translator.toLocale("com.user.update"), HttpStatus.OK, null);
            } else {
                return new ApiResponse(false, Translator.toLocale("com.userNot.role.update"), HttpStatus.BAD_REQUEST, null);
            }
        } catch (Exception e) {
            log.error("Error updating user: " + e.getMessage(), e);
            return new ApiResponse(false, Translator.toLocale("com.user.failed"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private void updateCollaboratorEmail(Collaborator collaborator, String previousMailId, EditUserEmail editUserEmail) {
        updateEmail(collaborator.getOwnedBy(), previousMailId, editUserEmail);

        for (CollaboratorUser coOwner : collaborator.getCoOwners()) {
            updateEmail(coOwner, previousMailId, editUserEmail);
        }

        for (CollaboratorUser member : collaborator.getMembers()) {
            updateEmail(member, previousMailId, editUserEmail);
        }

        for (CollaboratorUser reviewer : collaborator.getReviewers()) {
            updateEmail(reviewer, previousMailId, editUserEmail);
        }

    }

    private void updateEmail(CollaboratorUser user, String previousMailId, EditUserEmail editUserEmail) {
        if (user.getEmailId().equals(previousMailId)) {
            user.setEmailId(editUserEmail.getUserEmail());
            user.setUserName(editUserEmail.getName());
        }
    }

    private List<GroupUserPrivileges> getMemberListFromJson(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<GroupUserPrivileges>>() {
            });
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting Json to AuditGroupUser list");
        }
    }

    private void updateMemberEmail(GroupUserPrivileges member, String previousMailId, EditUserEmail editUserEmail) {
        if (member.getUserEmail().equals(previousMailId)) {
            member.setUserEmail(editUserEmail.getUserEmail());
            member.setUserName(editUserEmail.getName());
            log.info("updated member: {}", member);
        }
    }

    public ApiResponse getNewAccessToken(RefreshToken refreshToken, DistributedTransaction transaction) {

        User user = userRepository.getByUserEmail(refreshToken.getUserName(), transaction);
        if (user == null) {
            throw new GenericException("User Not Found!");
        }
        if (!user.getRefreshToken().equals(refreshToken.getRefreshToken())) {
            throw new GenericException("Refresh Token does not match..");
        }
        if (new Date().after(new Date(user.getRefreshTokenExpiry()))) {
            throw new GenericException("Refresh Token Expired, Please login again");
        }

        List<String> roleList;
        try {

            roleList = objectMapper.readValue(user.getRoleJson(), new TypeReference<List<String>>() {
            });
            log.info("Roles: {}", roleList);
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting JSON to Object");
        }

        String newRefreshToken = user.getRefreshToken();

        UserResponse userResponse;

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getUserEmail());

        final String token = jwtHelper.generateToken(userDetails);

        BoxAPIConnection boxAPIConnection = boxUtility.getBoxEnterpriseConnection();
        if (roleList.contains(UserRoles.EXTERNAL_AUDITOR.toString())) {

            userResponse = UserResponse.builder()
                    .userRoles(userDetails.getAuthorities()
                            .stream()
                            .map(e -> e.getAuthority())
                            .collect(Collectors.toList()))
                    .jwtToken(token)
                    .userEmail(userDetails.getUsername())
                    .name(user.getName())
                    .refreshToken(connection.getRefreshToken())
                    .accessToken(connection.getAccessToken())
                    .serviceAccAccessToken(boxAPIConnection.getAccessToken())
                    .jwtTokenRefreshToken(newRefreshToken)
                    .jwtTokenExpiresIn(jwtAccessValidity)
                    .build();
        } else {

            SubmitToken submitToken = updateLatestToken(user.getUserEmail(), transaction);

            userResponse = UserResponse.builder()
                    .jwtToken(token)
                    .refreshToken(submitToken.getRefreshToken())
                    .userEmail(user.getUserEmail())
                    .name(user.getName())
                    .accessToken(submitToken.getAccessToken())
                    .serviceAccAccessToken(boxAPIConnection.getAccessToken())
                    .userRoles(userDetails.getAuthorities()
                            .stream()
                            .map(e -> e.getAuthority())
                            .collect(Collectors.toList()))
                    .orgId(user.getOrgId())
                    .jwtTokenRefreshToken(newRefreshToken)
                    .jwtTokenExpiresIn(jwtAccessValidity)
                    .build();

        }

        return ApiResponse.builder()
                .status(true)
                .message("")
                .httpStatus(HttpStatus.OK)
                .data(userResponse)
                .build();

    }

    public User createUser(User user, DistributedTransaction transaction) {
        return userRepository.create(user, transaction);
    }


    public ApiResponse updateLanguageForUser(String lang, DistributedTransaction transaction) {

        // get user by mail
        User existingUser = userRepository.getByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName(), transaction);

        if (existingUser == null || existingUser.getIsDeleted().equals(true)) {
            throw new GenericException(Translator.toLocale("com.user.deleted1"));
        }
        existingUser.setLanguageCode(lang);
        userRepository.create(existingUser, transaction);

        return new ApiResponse(true, Translator.toLocale("com.userLang.updated"), HttpStatus.OK, existingUser.getLanguageCode());

    }

}

