package com.scalar.events_log_tool.application.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.CollaboratorUserRoles;
import com.scalar.events_log_tool.application.constant.UserRoles;
import com.scalar.events_log_tool.application.dto.AuditGroupListForAuditSet;
import com.scalar.events_log_tool.application.dto.AuditSetCollaboratorList;
import com.scalar.events_log_tool.application.dto.Collaborator;
import com.scalar.events_log_tool.application.dto.CollaboratorUser;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.AuditSet;
import com.scalar.events_log_tool.application.model.AuditSetCollaborators;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.AuditSetCollaboratorsRepository;
import com.scalar.events_log_tool.application.repository.AuditSetRepository;
import com.scalar.events_log_tool.application.repository.UserRepository;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.UserInfo;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuditSetCollaboratorService {

    private final AuditSetCollaboratorsRepository auditSetCollaboratorRepository;
    private final AuditSetRepository auditSetRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final CommonService commonService;

    public AuditSetCollaboratorService(AuditSetCollaboratorsRepository auditSetCollaboratorRepository, AuditSetRepository auditSetRepository, UserRepository userRepository, ObjectMapper objectMapper, CommonService commonService) {
        this.auditSetCollaboratorRepository = auditSetCollaboratorRepository;
        this.auditSetRepository = auditSetRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.commonService = commonService;
    }

    public ApiResponse changeAuditSetOwner(String auditSetId, String newOwnerEmail, String currentUser, DistributedTransaction transaction)
            throws JsonProcessingException {

        // Fetch the AuditSet
        AuditSet auditSet = auditSetRepository.get(auditSetId, transaction);

        if (auditSet == null) {
            throw new GenericException(Translator.toLocale("com.auditSet.notFound"));
        }
        String previousOwner = auditSet.getOwnerEmail();

        Collaborator aclJson = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
        // Check permission
        if (!(commonService.isOwner(aclJson, currentUser) || commonService.isCoOwner(aclJson, currentUser))) {
            throw new GenericException(Translator.toLocale("com.access.changeOwner.auditSet"));
        }

        // Fetch the user details for the new owner
        User newOwner = userRepository.getByUserEmail(newOwnerEmail, transaction);
        if (newOwner == null) {
            throw new GenericException(Translator.toLocale("com.check.newOwner"));
        }

        // Update the ACL JSON to change the owner and make the current owner a co-owner
        Collaborator updatedAclJson = changeAuditSetOwner(aclJson, newOwner);

        // Update the AuditSet with the new ACL JSON
        auditSet.setAclJson(objectMapper.writeValueAsString(updatedAclJson));
        auditSet.setOwnerEmail(newOwnerEmail);
        auditSet.setOwnerName(newOwner.getName());
        auditSet.setOwnerId(newOwner.getId());
        auditSetRepository.create(auditSet, transaction);

        // Fetch the current owner collaborator
        AuditSetCollaborators currentOwner = auditSetCollaboratorRepository.get(auditSetId, CollaboratorUserRoles.OWNER.toString(), previousOwner, transaction);
        String previousRoleForCurrentOwner = currentOwner.getAuditSetRole();
        currentOwner.setAuditSetRole(CollaboratorUserRoles.CO_OWNER.toString());
        auditSetCollaboratorRepository.createAndDelete(currentOwner, previousRoleForCurrentOwner, transaction);

        // Fetch new owner
        AuditSetCollaborators newOwnerCollaborator = auditSetCollaboratorRepository.get(auditSetId, CollaboratorUserRoles.CO_OWNER.toString(), newOwnerEmail, transaction);
        String previousRoleForNewOwner = newOwnerCollaborator.getAuditSetRole();
        newOwnerCollaborator.setAuditSetRole(CollaboratorUserRoles.OWNER.toString());
        auditSetCollaboratorRepository.createAndDelete(newOwnerCollaborator, previousRoleForNewOwner, transaction);

        return new ApiResponse(true, Translator.toLocale("com.ownerChanged.auditSet"), HttpStatus.OK, null);
    }

    private Collaborator changeAuditSetOwner(Collaborator aclJson, User newOwner) {

        // Find and remove the existing owner from the ownedBy list
        CollaboratorUser existingOwnerDto = aclJson.getOwnedBy();

        if (existingOwnerDto.getEmailId().equals(newOwner.getUserEmail())) {

            throw new GenericException(Translator.toLocale("com.alreadyOwner"));
        }

        boolean isCoOwner = aclJson.getCoOwners().stream().anyMatch(coOwner -> coOwner.getEmailId().equals(newOwner.getUserEmail()));

        // Check if the new owner is in the co-owner list
        if (!isCoOwner) {
            throw new GenericException(Translator.toLocale("com.check.co-owner"));
        }

        // If the new owner is already a co-owner, remove them from the co-owner list
        aclJson.getCoOwners().removeIf(coOwner -> coOwner.getEmailId().equals(newOwner.getUserEmail()));

        CollaboratorUser currentOwnerDto = CollaboratorUser.builder()
                .emailId(existingOwnerDto.getEmailId())
                .userName(existingOwnerDto.getUserName())
                .userId(existingOwnerDto.getUserId())
                .role(CollaboratorUserRoles.CO_OWNER.toString())
                .build();

        aclJson.getCoOwners().add(currentOwnerDto);

        CollaboratorUser newOwnerDto = CollaboratorUser.builder()
                .emailId(newOwner.getUserEmail())
                .userName(newOwner.getName())
                .userId(newOwner.getId())
                .role(CollaboratorUserRoles.OWNER.toString())
                .build();

        aclJson.setOwnedBy(newOwnerDto);

        return aclJson;
    }


    public ApiResponse getCollaboratorForAuditSet(String auditSetId, DistributedTransaction transaction) {

        // Fetch the AuditSet
        AuditSet auditSet = auditSetRepository.get(auditSetId, transaction);

        if (auditSet == null || auditSet.getIsDeleted().equals(true)) {
            throw new GenericException(Translator.toLocale("com.auditSet.notFound"));
        }
        // Fetch the users from AuditSet json
        String aclJson = auditSet.getAclJson();

        Collaborator collaboratorList;
        try {
            collaboratorList = objectMapper.readValue(aclJson, Collaborator.class);

        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception appropriately
            return new ApiResponse(false, "Error parsing JSON", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
        List<AuditGroupListForAuditSet> groupListForAuditSets;
        try {
            groupListForAuditSets = objectMapper.readValue(auditSet.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
            });

        } catch (JsonProcessingException e) {
            log.error("Error converting JSON to AuditGroupListForAuditSet list", e);
            throw new GenericException("Error converting JSON to AuditGroupListForAuditSet list");
        }

        AuditSetCollaboratorList auditSetCollaboratorList = new AuditSetCollaboratorList();

        List<AuditGroupListForAuditSet> auditGroupListForAuditSetList = new ArrayList<>();

        auditGroupListForAuditSetList.addAll(groupListForAuditSets);

        auditSetCollaboratorList.setCollaboratorList(collaboratorList);
        auditSetCollaboratorList.setAuditGroupListForAuditSetList(auditGroupListForAuditSetList);

        return new ApiResponse(true, "", HttpStatus.OK, auditSetCollaboratorList);
    }

    public ApiResponse markIsFavouriteAuditSet(String auditSetId, Boolean status, String name, DistributedTransaction transaction) {
        User user = userRepository.getByUserEmail(name, transaction);
        AuditSetCollaborators auditSetCollaborator = auditSetCollaboratorRepository.get(auditSetId, CollaboratorUserRoles.REVIEWER.toString(), user.getUserEmail(), transaction);
        if (auditSetCollaborator.getAuditSetId() != null) {
            auditSetCollaborator.setIsFavourite(!status);
            auditSetCollaboratorRepository.create(auditSetCollaborator, transaction);

            return new ApiResponse(true, Translator.toLocale("com.auditSet.asFavourite"), HttpStatus.OK, null);
        } else {

            return new ApiResponse(false, Translator.toLocale("com.auditSet.notFound"), HttpStatus.NOT_FOUND, null);
        }
    }

    public ApiResponse getGeneralUserList(DistributedTransaction transaction) throws TransactionException {
        List<User> userList = userRepository.getUserList(transaction);

        // Filter out deleted users
        userList = userList.stream()
                .filter(user -> !user.getIsDeleted())
                .filter(user -> !user.getRoleJson().contains(UserRoles.AUDIT_ADMIN.toString()))
                .filter(user -> user.getRoleJson().contains(UserRoles.GENERAL_USER.toString()))
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


    public ApiResponse getAuditAdminList(DistributedTransaction transaction) throws TransactionException {

        List<User> userList = userRepository.getUserList(transaction);

        // Filter out deleted users
        userList = userList.stream()
                .filter(user -> !user.getIsDeleted())
                .filter(user -> user.getRoleJson().contains(UserRoles.AUDIT_ADMIN.toString()))
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

}
