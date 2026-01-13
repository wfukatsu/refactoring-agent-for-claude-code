package com.scalar.events_log_tool.application.business;


import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.*;
import com.scalar.events_log_tool.application.dto.*;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.ItemStatus;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.ItemStatusRepository;
import com.scalar.events_log_tool.application.responsedto.*;
import com.scalar.events_log_tool.application.security.JwtHelper;
import com.scalar.events_log_tool.application.service.UserService;
import com.scalar.events_log_tool.application.utility.BoxUtility;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserBusiness {

    private final UserService userService;
    private final DistributedTransactionManager transactionManager;
    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final ItemStatusRepository itemStatusRepository;
    private final BoxUtility boxUtility;
    private final ObjectMapper objectMapper;
    private final Integer jwtRefreshValidity;
    private final Integer jwtAccessValidity;

    @Qualifier("box-connection-for-operation")
    @Autowired
    private BoxAPIConnection boxEnterpriseConnection;


    public UserBusiness(UserService userService, DistributedTransactionManager transactionManager, JwtHelper jwtHelper, AuthenticationManager authenticationManager, UserDetailsService userDetailsService, ItemStatusRepository itemStatusRepository, BoxUtility boxUtility, ObjectMapper objectMapper,
                        @Value("${jwt.refresh.token.validity}") Integer jwtRefreshValidity, @Value("${jwt.access.token.validity}") Integer jwtAccessValidity) {
        this.userService = userService;
        this.transactionManager = transactionManager;
        this.jwtHelper = jwtHelper;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.itemStatusRepository = itemStatusRepository;
        this.boxUtility = boxUtility;
        this.objectMapper = objectMapper;
        this.jwtRefreshValidity = jwtRefreshValidity;
        this.jwtAccessValidity = jwtAccessValidity;
    }

    /**
     * Description: This API used to create user
     */
    public ApiResponse createUser(UserDto userDto) {
        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.createUser(userDto, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }


    public ApiResponse getManagedUsers(String orgId) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.getManagedUsers(orgId, transaction);
            transaction.commit();
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }


    /**
     * Description: This API delete user using emailId.
     */
    public ApiResponse deleteUser(String userEmailId, String email) {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {

            ApiResponse apiResponse = userService.deleteUser(userEmailId, email, transaction);
            transaction.commit();
            return apiResponse;

        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse login(LoginRequest loginRequest) {


        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }
        try {
            userService.login(loginRequest, transaction);
            User byUserName = userService.getByUserName(loginRequest.getUserEmail(), transaction);

            log.info("Login: " + loginRequest.getUserEmail() + " Password:" + loginRequest.getPassword());
            authenticate(loginRequest.getUserEmail(), loginRequest.getPassword());

            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(loginRequest.getUserEmail());

            final String token = jwtHelper.generateToken(userDetails);

            Date expiryDate = new Date(byUserName.getRefreshTokenExpiry());
            Date currentDate = new Date();
            String newRefreshToken = byUserName.getRefreshToken();
            if (currentDate.after(expiryDate)) {
                newRefreshToken = GenericUtility.generateUUID();
                Long refreshTokenExpire = System.currentTimeMillis() + jwtRefreshValidity * 1000;
                byUserName.setRefreshToken(newRefreshToken);
                byUserName.setRefreshTokenExpiry(refreshTokenExpire);
                userService.createUser(byUserName, transaction);
            }

            transaction.commit();

            log.info("Transaction Committed");

            boolean needsRefresh = boxEnterpriseConnection.needsRefresh();
            log.info("Does Box Connection needs Refresh Call :" + needsRefresh + " Expire time was :" + boxEnterpriseConnection.getExpires());

            if (needsRefresh) {
                boxEnterpriseConnection = boxUtility.getBoxEnterpriseConnection();
                log.info("After Box Connection Refresh Status :" + boxEnterpriseConnection.needsRefresh());
                log.info("After Box Connection Expires  :" + boxEnterpriseConnection.getExpires());

            }
            return ApiResponse.builder()
                    .status(true)
                    .message("")
                    .httpStatus(HttpStatus.OK)
                    .data(UserResponse.builder()
                            .userRoles(userDetails.getAuthorities()
                                    .stream()
                                    .map(e -> e.getAuthority())
                                    .collect(Collectors.toList()))
                            .jwtToken(token)
                            .userEmail(userDetails.getUsername())
                            .name(byUserName.getName())
                            .refreshToken(boxEnterpriseConnection.getRefreshToken())
                            .accessToken(boxEnterpriseConnection.getAccessToken())
                            .serviceAccAccessToken(boxEnterpriseConnection.getAccessToken())
                            .jwtTokenRefreshToken(newRefreshToken)
                            .jwtTokenExpiresIn(jwtAccessValidity)
                            .languageCode(byUserName.getLanguageCode() == null ? LanguageSupported.English.getCode() : byUserName.getLanguageCode())
                            .build())
                    .build();
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            throw new GenericException(Translator.toLocale("com.incorrect.emailPassword"));
        }
    }


    public ApiResponse updateRole(String userEmail, List<String> newRoles) {
        int retryCount = 0;
        while (retryCount < 3) {
            DistributedTransaction transaction;
            try {
                // Start transaction
                transaction = transactionManager.start();
            } catch (TransactionException e) {
                throw new GenericException(Translator.toLocale("com.unable.transaction"));
            }

            try {
                ApiResponse apiResponse = userService.updateUserRole(userEmail, newRoles, transaction);
                transaction.commit();
                log.info("Transaction Committed");
                return apiResponse;
            } catch (TransactionException e) {
                log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
                try {
                    transaction.rollback();
                } catch (RollbackException ex) {
                    ex.printStackTrace();
                }
                // Increment retry count
                retryCount++;
                if (retryCount >= 3) {
                    throw new GenericException(Translator.toLocale("com.something.wrong"));
                }
            }
        }
        throw new GenericException(Translator.toLocale("com.unexpected.error")); // In case the loop somehow exits without returning
    }

    public ApiResponse submitToken(SubmitToken submitToken) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        BoxAPIConnection connection = new BoxAPIConnection(submitToken.getAccessToken());
        BoxUser user = BoxUser.getCurrentUser(connection);
        BoxUser.Info info = user.getInfo("id", "type", "avatar_url", "enterprise", "name", "role", "login");

        try {
            userService.registerUserAndSaveToken(info, submitToken, transaction);
            transaction.commit();
            ApiResponse apiResponse = login(LoginRequest.builder()
                    .userEmail(info.getLogin())
                    .password("password@123")
                    .build());

            if (apiResponse.getStatus()) {
                UserResponse userResponse = (UserResponse) apiResponse.getData();
                userResponse.setOrgId(info.getEnterprise().getID());
                userResponse.setAccessToken(submitToken.getAccessToken());
                userResponse.setRefreshToken(submitToken.getRefreshToken());
                apiResponse.setData(userResponse);
            }
            return apiResponse;

        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    public ApiResponse getListOfExternalAuditors() {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.getListOfExternalAuditors(transaction);
            transaction.commit();
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    public ApiResponse getToken(String username) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        User user = userService.getByUserName(username, transaction);
        if (user == null) {
            throw new GenericException(Translator.toLocale("com.incorrect.userEmail"));
        }
        List<String> roleList;
        try {

            roleList = objectMapper.readValue(user.getRoleJson(), new TypeReference<List<String>>() {
            });
            log.info("Roles: {}", roleList);
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting JSON to Object");
        }

        if (roleList.contains(UserRoles.EXTERNAL_AUDITOR.toString())) {
            throw new GenericException(Translator.toLocale("com.invalid.boxUser"));
        }
        try {
            ApiResponse response = userService.getToken(username, transaction);
            transaction.commit();
            return response;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse getOrgList() {

        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.getOrgList(transaction);
            transaction.commit();
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    public ApiResponse getIntegratedItemDetails(Long itemId, Long userId, String itemType) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        User user = userService.getByUserId(userId, transaction);
        if (user == null || user.getIsDeleted().equals(true)) {
            return new ApiResponse(false, Translator.toLocale("com.user.notFound"), HttpStatus.OK, null);
        }
        Object itemDetails;

        SubmitToken submitToken = userService.updateLatestToken(user.getUserEmail(), transaction);

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(user.getUserEmail());

        final String token = jwtHelper.generateToken(userDetails);

        BoxAPIConnection connection = new BoxAPIConnection(submitToken.getAccessToken());

        SimpleDateFormat utcFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();

        User userObject = userService.getByUserName(user.getUserEmail(), transaction);

        Date expiryDate = new Date(userObject.getRefreshTokenExpiry());
        Date currentDate = new Date();
        String newRefreshToken = userObject.getRefreshToken();
        if (currentDate.after(expiryDate)) {
            newRefreshToken = GenericUtility.generateUUID();
            Long refreshTokenExpire = System.currentTimeMillis() + jwtRefreshValidity * 1000;
            userObject.setRefreshToken(newRefreshToken);
            userObject.setRefreshTokenExpiry(refreshTokenExpire);
            userService.createUser(userObject, transaction);
        }


        if (itemType != null && itemType.equalsIgnoreCase(ItemType.FILE.toString())) {

            BoxFile file = new BoxFile(connection, String.valueOf(itemId));
            BoxFile.Info info = file.getInfo();

            List<String> paths = info.getPathCollection().stream()
                    .map(path -> path.getName())
                    .collect(Collectors.toList());
            paths.add(info.getName());


            String filePath = paths.stream().collect(Collectors.joining("/"));

            Date createdAt = info.getCreatedAt();
            String formattedUtcCreatedAt = utcFormat.format(createdAt);

            //covert data into utc format(modified at)
            Date modifiedAt = info.getModifiedAt();
            String formattedUtcModifiedAt = utcFormat.format(modifiedAt);

            //set owned by object
            OwnedBy ownedBy = new OwnedBy();
            ownedBy.setId(info.getOwnedBy().getID());
            ownedBy.setName(info.getOwnedBy().getName());
            ownedBy.setLogin(info.getOwnedBy().getLogin());
            ownedBy.setType(String.valueOf(info.getOwnedBy().getType()));

            //set modified by object
            OwnedBy modifiedBy = new OwnedBy();
            modifiedBy.setId(info.getModifiedBy().getID());
            modifiedBy.setName(info.getModifiedBy().getName());
            modifiedBy.setLogin(info.getModifiedBy().getLogin());
            modifiedBy.setType(String.valueOf(info.getModifiedBy().getType()));

            //get item from item table for getting details of particular item
            ItemStatus itemStatus = itemStatusRepository.get(itemId, transaction);

            log.info("Item Status: {}", itemStatus);
            // Convert size from bytes to megabytes
            String sizeInMegabytes = GenericUtility.getStringSizeLengthFile(info.getSize());
            itemDetails = FileDetailsDto.builder()
                    .id(itemId)
                    .name(info.getName())
                    .type(info.getType())
                    .description(info.getDescription())
                    .createdAt(formattedUtcCreatedAt)
                    .modifiedAt(formattedUtcModifiedAt)
                    .size(sizeInMegabytes)
                    .sha1(info.getSha1())
                    .ownedBy(ownedBy)
                    .tamperedStatus(itemStatus == null ? TamperingStatusType.NOT_MONITORED.toString() : itemStatus.getMonitoredStatus())
                    .modifiedBy(modifiedBy)
                    .path(filePath)
                    .build();
        } else if (itemType != null && itemType.equalsIgnoreCase(ItemType.FOLDER.toString())) {

            BoxFolder folder = new BoxFolder(connection, String.valueOf(itemId));
            BoxFolder.Info info = folder.getInfo();

            //covert data into utc format(created at)
            Date createdAt = info.getCreatedAt();
            String formattedUtcCreatedAt = utcFormat.format(createdAt);
            // Convert size from bytes to megabytes
            String sizeInMegabytes = GenericUtility.getStringSizeLengthFile(info.getSize());

            //covert data into utc format(modified at)
            Date modifiedAt = info.getModifiedAt();
            String formattedUtcModifiedAt = utcFormat.format(modifiedAt);

            //set owned by object
            OwnedBy ownedBy = new OwnedBy();
            ownedBy.setId(info.getOwnedBy().getID());
            ownedBy.setName(info.getOwnedBy().getName());
            ownedBy.setLogin(info.getOwnedBy().getLogin());
            ownedBy.setType(String.valueOf(info.getOwnedBy().getType()));

            //set modified by object
            OwnedBy modifiedBy = new OwnedBy();
            modifiedBy.setId(info.getModifiedBy().getID());
            modifiedBy.setName(info.getModifiedBy().getName());
            modifiedBy.setLogin(info.getModifiedBy().getLogin());
            modifiedBy.setType(String.valueOf(info.getModifiedBy().getType()));

            List<String> paths = info.getPathCollection().stream()
                    .map(path -> path.getName())
                    .collect(Collectors.toList());
            paths.add(info.getName());


            String folderPath = paths.stream().collect(Collectors.joining("/"));

            //get data in folder details dto
            itemDetails = FolderDetailsDto.builder()
                    .id(Long.valueOf(info.getID()))
                    .name(info.getName())
                    .type(info.getType())
                    .description(info.getDescription())
                    .createdAt(formattedUtcCreatedAt)
                    .modifiedAt(formattedUtcModifiedAt)
                    .size(sizeInMegabytes)
                    .ownedBy(ownedBy)
                    .path(folderPath)
                    .modifiedBy(modifiedBy)
                    .build();

        } else {
            throw new GenericException(Translator.toLocale("com.invalid.itemType"));
        }
        try {
            transaction.commit();
            IntegratedResponse integratedResponse = IntegratedResponse.builder()
                    .itemDetails(itemDetails)
                    .userResponse(UserResponse.builder()
                            .refreshToken(submitToken.getRefreshToken())
                            .accessToken(submitToken.getAccessToken())
                            .userRoles(userDetails.getAuthorities()
                                    .stream()
                                    .map(e -> e.getAuthority())
                                    .collect(Collectors.toList()))
                            .userEmail(userDetails.getUsername())
                            .name(userObject.getName())
                            .jwtToken(token)
                            .jwtTokenRefreshToken(newRefreshToken)
                            .jwtTokenExpiresIn(jwtAccessValidity)
                            .languageCode(userObject.getLanguageCode() == null ? LanguageSupported.English.getCode() : userObject.getLanguageCode())
                            .build())
                    .build();


            return ApiResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .status(true)
                    .message("")
                    .data(integratedResponse)
                    .build();
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse sendResetPasswordOTP(String email) {
        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.sendResetPasswordOTP(email, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse forgotPassword(UpdatePasswordDTO updatePasswordDTO) {
        //Start Transaction
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
            log.info("Transaction Started");
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }
        ApiResponse apiResponse = userService.forgotPassword(updatePasswordDTO, transaction);
        try {
            transaction.commit();
            log.info("Transaction Committing");
            return apiResponse;

        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }


    public ApiResponse editUser(EditUserEmail editUserEmail, String previousMailId, String currentUser) {


        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.editUser(editUserEmail, previousMailId, currentUser, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    public ApiResponse getServiceAccToken() {

        boolean needsRefresh = boxEnterpriseConnection.needsRefresh();
        long lastRefresh = boxEnterpriseConnection.getLastRefresh();
        log.info("Does Box Connection needs Refresh Call :" + needsRefresh + "  Last Refresh was done at  " + new Date(lastRefresh));

        if (needsRefresh) {
            boxEnterpriseConnection = boxUtility.getBoxEnterpriseConnection();
            log.info("After Box Connection Refresh Status :" + boxEnterpriseConnection.needsRefresh());
        }

        return ApiResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("")
                .status(true)
                .data(SubmitToken.builder()
                        .accessToken(boxEnterpriseConnection.getAccessToken())
                        .refreshToken(boxEnterpriseConnection.getRefreshToken())
                        .expiresIn(Integer.parseInt(String.valueOf(boxEnterpriseConnection.getExpires())))
                        .build())
                .build();

    }

    public ApiResponse getNewAccessToken(RefreshToken refreshToken) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.getNewAccessToken(refreshToken, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse updateLanguageForUser(String lang) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException e) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            ApiResponse apiResponse = userService.updateLanguageForUser(lang, transaction);
            transaction.commit();
            log.info("Transaction Committed");
            return apiResponse;
        } catch (TransactionException e) {
            log.info(ErrorLogMessages.ERROR_WHILE_COMMITTING_TRANSACTION);
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }
}