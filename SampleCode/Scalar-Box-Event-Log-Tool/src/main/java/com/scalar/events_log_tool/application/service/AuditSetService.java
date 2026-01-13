package com.scalar.events_log_tool.application.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.events_log_tool.application.constant.AccessStatus;
import com.scalar.events_log_tool.application.constant.CollaboratorUserRoles;
import com.scalar.events_log_tool.application.constant.UserRoles;
import com.scalar.events_log_tool.application.dto.*;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.exception.NotFoundException;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.model.*;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.AuditSetLists;
import com.scalar.events_log_tool.application.responsedto.AuditSetResponse;
import com.scalar.events_log_tool.application.responsedto.ExtAuditorAccessLog;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuditSetService {
    private final AuditSetRepository auditSetRepository;
    private final AuditSetCollaboratorsRepository auditSetCollaboratorsRepository;
    private final UserRepository userRepository;
    private final AuditorLogsRepository auditorLogsRepository;
    private final AuditGroupRepository auditGroupRepository;
    private final UserAuditGroupRepository userAuditGroupRepository;
    private final AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository;
    private final ObjectMapper objectMapper;
    private final AuditSetItemRepository auditSetItemRepository;
    private final CommonService commonService;

    public AuditSetService(AuditSetRepository auditSetRepository, AuditSetCollaboratorsRepository auditSetCollaboratorsRepository, UserRepository userRepository, AuditorLogsRepository auditorLogsRepository, AuditGroupRepository auditGroupRepository, UserAuditGroupRepository userAuditGroupRepository, AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository, ObjectMapper objectMapper, AuditSetItemRepository auditSetItemRepository, CommonService commonService) {
        this.auditSetRepository = auditSetRepository;
        this.auditSetCollaboratorsRepository = auditSetCollaboratorsRepository;
        this.userRepository = userRepository;
        this.auditorLogsRepository = auditorLogsRepository;
        this.auditGroupRepository = auditGroupRepository;
        this.userAuditGroupRepository = userAuditGroupRepository;
        this.auditGrpAuditSetMappingRepository = auditGrpAuditSetMappingRepository;
        this.objectMapper = objectMapper;
        this.auditSetItemRepository = auditSetItemRepository;
        this.commonService = commonService;

    }

    public ApiResponse createAuditSet(AuditSetInputDto auditSetInputDto, String userEmail, DistributedTransaction transaction) {
        try {
            //get user by mail Id
            User user = userRepository.getByUserEmail(userEmail, transaction);

            // Create a SimpleDateFormat object for UTC with the format
            SimpleDateFormat utcFormat = GenericUtility.getUTCDateFormatWithoutMilliseconds();

            // Format the date in UTC
            String formattedUtcDate = utcFormat.format(new Date());
            if (auditSetInputDto.getAuditName() == null || auditSetInputDto.getAuditName().trim().length() == 0) {
                return new ApiResponse(false, Translator.toLocale("com.name.NotEmpty"), HttpStatus.BAD_REQUEST, null);
            }
            //Check for duplicate name
            List<AuditSet> auditSetList = auditSetRepository.getAuditSetList(transaction);
            if (auditSetList != null) {


                List<AuditSet> duplicateAuditSetName = auditSetList.stream()
                        .filter(e -> !e.getIsDeleted())
                        .filter(e -> e.getAuditSetName() != null)
                        .filter(n -> n.getAuditSetName().equals(auditSetInputDto.getAuditName()))
                        .collect(Collectors.toList());

                if (!duplicateAuditSetName.isEmpty())
                    return new ApiResponse(false, Translator.toLocale("com.name.alreadyExits"), HttpStatus.BAD_REQUEST, null);
            }
            //fetch authority from token
            List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream()
                    .map(e -> e.getAuthority())
                    .collect(Collectors.toList());

            //check role for creating audit set
            if (authorities.contains(UserRoles.AUDIT_ADMIN.toString())) {

                //set data in audit set table
                AuditSet auditSet = AuditSet.builder()
                        .auditSetId(GenericUtility.generateUUID())
                        .auditSetName(auditSetInputDto.getAuditName())
                        .description(auditSetInputDto.getDescription())
                        .ownerId(user.getId())
                        .ownerName(user.getName())
                        .ownerEmail(user.getUserEmail())
                        .isDeleted(false)
                        .createdAt(Long.parseLong(formattedUtcDate))
                        .build();


                Collaborator collaboratorObject = new Collaborator();


                CollaboratorUser collaboratorUser = new CollaboratorUser();
                collaboratorUser.setUserId(user.getId());
                collaboratorUser.setUserName(user.getName());
                collaboratorUser.setEmailId(user.getUserEmail());
                collaboratorUser.setRole(CollaboratorUserRoles.OWNER.toString());

                collaboratorObject.setOwnedBy(collaboratorUser);
                collaboratorObject.setCoOwners(new ArrayList<>());
                collaboratorObject.setMembers(new ArrayList<>());
                collaboratorObject.setReviewers(new ArrayList<>());

                //for owner:

                AuditSetCollaborators existingOwnerCollaborator =
                        auditSetCollaboratorsRepository.get(auditSet.getAuditSetId(), CollaboratorUserRoles.OWNER.toString(), user.getUserEmail(), transaction);
                if (existingOwnerCollaborator == null) {
                    AuditSetCollaborators auditSetCollaborators = AuditSetCollaborators.builder()
                            .auditSetId(auditSet.getAuditSetId())
                            .userEmail(user.getUserEmail())
                            .auditSetName(auditSet.getAuditSetName())
                            .userName(user.getName())
                            .auditSetRole(CollaboratorUserRoles.OWNER.toString())
                            .accessStatus(AccessStatus.NEWLY_ADDED.toString())
                            .isFavourite(false)
                            .build();
                    auditSetCollaboratorsRepository.create(auditSetCollaborators, transaction);
                }


                // Iterate over each AuditSetCollab in the list
                for (AddUserAuditSet collab : auditSetInputDto.getAuditSetCollab()) {
                    // check userObject
                    User userObject = userRepository.getByUserEmail(collab.getUserEmail(), transaction);
                    if (userObject == null) {
                        throw new GenericException(Translator.toLocale("com.user.notFound"));
                    }

                    // Update ACL JSON in AuditSet
                    collaboratorObject = updateAclJson(userObject, collab.getAuditSetRole(), collaboratorObject);

                    AuditSetCollaborators existingCollaborator =
                            auditSetCollaboratorsRepository.get(auditSet.getAuditSetId(), collab.getAuditSetRole(), userObject.getUserEmail(), transaction);
                    if (existingCollaborator == null) {
                        AuditSetCollaborators auditSetCollaborators = AuditSetCollaborators.builder()
                                .auditSetId(auditSet.getAuditSetId())
                                .userEmail(userObject.getUserEmail())
                                .auditSetName(auditSet.getAuditSetName())
                                .userName(userObject.getName())
                                .auditSetRole(collab.getAuditSetRole())
                                .accessStatus(AccessStatus.NEWLY_ADDED.toString())
                                .isFavourite(false)
                                .build();
                        auditSetCollaboratorsRepository.create(auditSetCollaborators, transaction);
                    }
                }
                //group

                List<AuditGroupListForAuditSet> groupListForAuditSets = new ArrayList<>();
                for (String grpId : auditSetInputDto.getGrpIds()) {
                    groupListForAuditSets = addGroupToAuditSet(groupListForAuditSets, auditSet.getAuditSetId(), auditSet.getAuditSetName(), grpId, transaction);
                }


                auditSet.setAclJson(GenericUtility.convertObjectToStringJson(collaboratorObject));
                auditSet.setAuditGroupListJson(GenericUtility.convertObjectToStringJson(groupListForAuditSets));
                auditSetRepository.create(auditSet, transaction);


                //api response
                AuditSetResponse auditSetResponse = AuditSetResponse.builder()
                        .auditSetId(auditSet.getAuditSetId())
                        .auditSetName(auditSet.getAuditSetName())
                        .description(auditSet.getDescription())
                        .ownerId(auditSet.getOwnerId())
                        .ownerName(auditSet.getOwnerName())
                        .collaborator(collaboratorObject)
                        .isDeleted(auditSet.getIsDeleted()).build();

                return new ApiResponse(true, Translator.toLocale("com.audit.create"), HttpStatus.OK, auditSetResponse);
            } else {
                return new ApiResponse(false, Translator.toLocale("com.userNot.role.auditSet"), HttpStatus.BAD_REQUEST, null);
            }
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            throw new GenericException(ex.getMessage());
        }
    }

    public List<AuditGroupListForAuditSet> addGroupToAuditSet(List<AuditGroupListForAuditSet> groupListForAuditSets, String auditSetId, String auditSetName, String auditGroupId, DistributedTransaction transaction) {

        AuditGroup auditGroup = auditGroupRepository.getAuditGroup(auditGroupId, transaction);

        // Check if AuditSet and AuditGroup exist
        if (auditGroup == null) {
            throw new GenericException(Translator.toLocale("com.auditSetAuditGroup.NotFound"));
        }

        // Create a mapping entry
        AuditGrpAuditSetMapping auditGrpAuditSetMapping = new AuditGrpAuditSetMapping();
        auditGrpAuditSetMapping.setAuditGroupId(auditGroup.getAuditGroupId());
        auditGrpAuditSetMapping.setAuditGroupName(auditGroup.getAuditGroupName());
        auditGrpAuditSetMapping.setAuditSetId(auditSetId);
        auditGrpAuditSetMapping.setAuditSetName(auditSetName);


        AuditGroupListForAuditSet newAuditGroup = AuditGroupListForAuditSet.builder()
                .auditGroupId(auditGroup.getAuditGroupId())
                .auditGroupName(auditGroup.getAuditGroupName())
                .description(auditGroup.getDescription())
                .build();

        groupListForAuditSets.add(newAuditGroup);

        AuditGrpAuditSetMapping auditGroupAndAuditSetMapping = auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGrpAuditSetMapping.getAuditGroupId(), auditGrpAuditSetMapping.getAuditSetId(), transaction);
        if (auditGroupAndAuditSetMapping == null) {
            auditGrpAuditSetMappingRepository.create(auditGrpAuditSetMapping, transaction);
        }
        return groupListForAuditSets;
    }

    Collaborator updateAclJson(User user, String role, Collaborator collaborator) {
        return appendUserToList(collaborator, user, role);
    }

    /**
     * Appends a user to the Collaborator list with the specified role.
     */
    private Collaborator appendUserToList(Collaborator collaborator, User user, String role) {

        //build collaborator Object
        CollaboratorUser auditSetUserDto = CollaboratorUser.builder()
                .emailId(user.getUserEmail())
                .userName(user.getName())
                .userId(user.getId())
                .role(CollaboratorUserRoles.valueOf(role).toString())
                .build();

        // Check if the user is already an owner
        CollaboratorUser owner = collaborator.getOwnedBy();
        if (owner != null && owner.getEmailId().equals(user.getUserEmail())) {
            throw new GenericException(Translator.toLocale("com.auditSet.alreadyOwner"));
        }

        Set<CollaboratorUser> allUsers = new HashSet<>();
        allUsers.add(collaborator.getOwnedBy()); // Add owner
        allUsers.addAll(collaborator.getCoOwners() == null ? new ArrayList<>() : collaborator.getCoOwners());
        allUsers.addAll(collaborator.getMembers() == null ? new ArrayList<>() : collaborator.getMembers());
        allUsers.addAll(collaborator.getReviewers() == null ? new ArrayList<>() : collaborator.getReviewers());

        Optional<CollaboratorUser> collaboratorUser = allUsers.stream().filter(e -> e.getEmailId().equals(user.getUserEmail())).findFirst();

        // Check if the user is already present in the Collaborator list with a different role
        if (collaboratorUser.isPresent()) {
            throw new GenericException(Translator.toLocale("com.user.already.AuditSet") + collaboratorUser.get().getRole());
        }

        List<String> roleSystem;
        try {
            // Convert JSON string to a list of roles
            roleSystem = objectMapper.readValue(user.getRoleJson(), new TypeReference<List<String>>() {
            });
            log.info("Collaborator: {}", roleSystem);
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting Json to string list");
        }

        // Add the user to the appropriate role based on their assigned roles
        if (roleSystem.contains(UserRoles.AUDIT_ADMIN.toString())) {
            if (CollaboratorUserRoles.CO_OWNER.toString().equals(role)) {
                collaborator.getCoOwners().add(auditSetUserDto);
            } else {
                throw new GenericException(Translator.toLocale("com.user.role.auditAdmin"));
            }
        } else if (roleSystem.contains(UserRoles.GENERAL_USER.toString())) {
            if (CollaboratorUserRoles.MEMBER.toString().equals(role)) {
                collaborator.getMembers().add(auditSetUserDto);
            } else {
                throw new GenericException(Translator.toLocale("com.user.role.generalUser"));
            }
        } else if (roleSystem.contains(UserRoles.EXTERNAL_AUDITOR.toString())) {
            if (CollaboratorUserRoles.REVIEWER.toString().equals(role)) {
                collaborator.getReviewers().add(auditSetUserDto);
            } else {
                throw new GenericException(Translator.toLocale("com.user.role.externalAuditor"));
            }
        } else {
            throw new GenericException(Translator.toLocale("com.given.user") + roleSystem + Translator.toLocale("com.notAdd.role") + role);
        }
        return collaborator;

    }

    public ApiResponse deleteAuditSet(String auditSetId, String userEmail, DistributedTransaction transaction) {
        try {
            // Get user by email
            User user = userRepository.getByUserEmail(userEmail, transaction);

            // Check if user is present
            if (user == null) {
                throw new NotFoundException(Translator.toLocale("com.user.notFound"));
            }

            // Get audit set by id
            AuditSet auditSet = auditSetRepository.get(auditSetId, transaction);

            // Check if audit set is present
            if (auditSet == null) {
                throw new NotFoundException(Translator.toLocale("com.auditSet.notFound"));
            }

            // Check if audit set is already deleted
            if (auditSet.getIsDeleted()) {
                return new ApiResponse(true, Translator.toLocale("com.already.deletedAuditSet"), HttpStatus.OK, null);

            }

            // Convert JSON string to object for fetching role
            Collaborator collaborator;
            try {
                collaborator = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
            } catch (JsonProcessingException e) {
                throw new GenericException("Error converting JSON to Object");
            }

            Optional<CollaboratorUser> user1 = collaborator.getCoOwners()
                    .stream()
                    .filter(e -> e.getEmailId().equals(userEmail))
                    .findFirst();

            //check owner and co-owner
            boolean isOwner = user1.isPresent() ||
                    (collaborator.getOwnedBy().getRole().equalsIgnoreCase(CollaboratorUserRoles.OWNER.toString()) &&
                            collaborator.getOwnedBy().getEmailId().equals(userEmail));
            // Check owner or co-owner role and delete audit set
            if (isOwner) {
                String ownerEmail = collaborator.getOwnedBy().getEmailId();
                String ownerRole = collaborator.getOwnedBy().getRole();

                AuditSetCollaborators auditSetCollaboratorss = auditSetCollaboratorsRepository.get(auditSetId, ownerRole, ownerEmail, transaction);
                if (auditSetCollaboratorss != null) {
                    auditSetCollaboratorsRepository.delete(auditSetId, ownerRole, ownerEmail, transaction);
                }

                for (CollaboratorUser collaborator1 : collaborator.getReviewers()) {
                    String emailId = collaborator1.getEmailId();
                    String role = collaborator1.getRole();

                    AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSetId, role, emailId, transaction);
                    if (auditSetCollaborators != null) {
                        auditSetCollaboratorsRepository.delete(auditSetId, role, emailId, transaction);
                    }
                }
                // Iterate over coOwner
                for (CollaboratorUser coOwner : collaborator.getCoOwners()) {
                    String emailId = coOwner.getEmailId();
                    String role = coOwner.getRole();


                    AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSetId, role, emailId, transaction);
                    if (auditSetCollaborators != null) {
                        auditSetCollaboratorsRepository.delete(auditSetId, role, emailId, transaction);
                    }
                }

                for (CollaboratorUser member : collaborator.getMembers()) {
                    String emailId = member.getEmailId();
                    String role = member.getRole();


                    AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSetId, role, emailId, transaction);
                    if (auditSetCollaborators != null) {
                        auditSetCollaboratorsRepository.delete(auditSetId, role, emailId, transaction);
                    }
                }
                // Check if user is owner or co-owner
                //get list of collaborator user

                // Convert JSON string to a list of AuditGroupListForAuditSet
                List<AuditGroupListForAuditSet> groupListForAuditSets;
                try {
                    groupListForAuditSets = objectMapper.readValue(auditSet.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
                    });

                } catch (JsonProcessingException e) {
                    log.error("Error converting JSON to AuditGroupListForAuditSet list", e);
                    throw new GenericException("Error converting JSON to AuditGroupListForAuditSet list");
                }

                // Iterate over the list and get the group IDs
                for (AuditGroupListForAuditSet auditGroup : groupListForAuditSets) {
                    String groupId = auditGroup.getAuditGroupId();
                    auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(groupId, auditSet.getAuditSetId(), transaction);
                    auditGrpAuditSetMappingRepository.delete(groupId, auditSet.getAuditSetId(), transaction);

                    // You can use 'groupId' as needed.
                }

                groupListForAuditSets.clear();
                String removedListOfAuditGroup = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
                auditSet.setAuditGroupListJson(removedListOfAuditGroup);

                auditSet.setIsDeleted(true);
                auditSetRepository.create(auditSet, transaction);

                return new ApiResponse(true, Translator.toLocale("com.auditSet.delete"), HttpStatus.OK, null);
            } else {
                return new ApiResponse(false, Translator.toLocale("com.userNot.role.deleteAuditSet"), HttpStatus.BAD_REQUEST, null);
            }
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            log.error("Error while deleting audit set", ex);
            return new ApiResponse(false, Translator.toLocale("com.error.deleteAuditSet"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    public ApiResponse getMyAuditSetList(String currentEmail, DistributedTransaction transaction) {
        try {
            User user = userRepository.getByUserEmail(currentEmail, transaction);

            if (user == null) {
                throw new NotFoundException(Translator.toLocale("com.user.notFound"));
            }

            List<AuditSetLists> auditSetLists = new ArrayList<>();

            List<String> collect = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());
            if (collect.contains(UserRoles.AUDIT_ADMIN.toString())) {

                auditSetLists.addAll(getAuditSetListsForAdmin(transaction));

            } else if (collect.contains(UserRoles.GENERAL_USER.toString())) {

                auditSetLists.addAll(getAuditSetListsForCollaborators(currentEmail, transaction));

            } else if (collect.contains(UserRoles.EXTERNAL_AUDITOR.toString())) {

                auditSetLists.addAll(getAuditSetListsForCollaborators(currentEmail, transaction));


                List<String> allAuditSets = auditSetLists.stream().map(e -> e.getAuditSetId())
                        .collect(Collectors.toList());

                auditSetLists.addAll(getAuditSetListsForUserGroups(currentEmail, allAuditSets, transaction));
            }

            if (!auditSetLists.isEmpty()) {
                return new ApiResponse(true, "", HttpStatus.OK, auditSetLists);
            } else {
                return new ApiResponse(true, "", HttpStatus.OK, auditSetLists);
            }

        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            log.error("Error in getMyAuditSetList", ex);
            return new ApiResponse(false, Translator.toLocale("com.myAuditSet.error"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private List<AuditSetLists> getAuditSetListsForAdmin(DistributedTransaction transaction) throws CrudException {
        List<AuditSetLists> auditSetLists = new ArrayList<>();
        List<AuditSet> auditSetList = auditSetRepository.getAuditSetList(transaction);

        for (AuditSet auditSet : auditSetList) {
            if (auditSet.getIsDeleted()) {
                // Skip deleted entries
                continue;
            }
            AuditSetLists auditSetList1 = AuditSetLists.builder()
                    .auditSetId(auditSet.getAuditSetId())
                    .auditSetName(auditSet.getAuditSetName())
                    .description(auditSet.getDescription())
                    .createdAt(auditSet.getCreatedAt())
                    .ownedBy(auditSet.getOwnerEmail()).build();
            auditSetLists.add(auditSetList1);
        }

        return auditSetLists;
    }


    private List<AuditSetLists> getAuditSetListsForCollaborators(String currentEmail, DistributedTransaction transaction) throws CrudException {
        List<AuditSetLists> auditSetLists = new ArrayList<>();
        List<AuditSetCollaborators> auditSetCollaboratorList = auditSetCollaboratorsRepository.getAuditSetCollaboratorList(currentEmail, transaction);
        for (AuditSetCollaborators auditSetCollaborators : auditSetCollaboratorList) {

            AuditSet auditSet = auditSetRepository.get(auditSetCollaborators.getAuditSetId(), transaction);
            if (auditSet != null) {
                AuditSetLists auditSetList2 = AuditSetLists.builder()
                        .auditSetId(auditSetCollaborators.getAuditSetId())
                        .auditSetName(auditSet.getAuditSetName())
                        .description(auditSet.getDescription())
                        .createdAt(auditSet.getCreatedAt())
                        .ownedBy(auditSet.getOwnerEmail())
                        .accessStatus(auditSetCollaborators.getAccessStatus())
                        .isFavourite(auditSetCollaborators.getIsFavourite() != null ? auditSetCollaborators.getIsFavourite() : false)
                        .build();
                auditSetLists.add(auditSetList2);
            }
        }
        return auditSetLists;
    }

    private List<AuditSetLists> getAuditSetListsForUserGroups(String currentEmail, List<String> auditSetList, DistributedTransaction transaction) throws CrudException {

        List<AuditSetLists> auditSetLists = new ArrayList<>();
        List<UserAuditGroup> userAuditList = userAuditGroupRepository.getUserGroupList(currentEmail, transaction);

        if (!userAuditList.isEmpty()) {
            for (UserAuditGroup userAuditGroup : userAuditList) {
                String auditGroupId = userAuditGroup.getAuditGroupId();
                List<AuditGrpAuditSetMapping> auditGrpAuditSetMappingList = auditGrpAuditSetMappingRepository.getUserGrpAuditSetList(auditGroupId, transaction);

                if (!auditGrpAuditSetMappingList.isEmpty()) {
                    for (AuditGrpAuditSetMapping auditGrpAuditSetMapping : auditGrpAuditSetMappingList) {
                        if (!auditSetList.contains(auditGrpAuditSetMapping.getAuditSetId())) {

                            AuditSet auditSet = auditSetRepository.get(auditGrpAuditSetMapping.getAuditSetId(), transaction);
                            // need to change for further implementation
                            AuditSetLists auditSetList3 = AuditSetLists.builder()
                                    .auditSetId(auditGrpAuditSetMapping.getAuditSetId())
                                    .auditSetName(auditGrpAuditSetMapping.getAuditSetName())
                                    .description(auditSet.getDescription())
                                    .createdAt(auditSet.getCreatedAt())
                                    .ownedBy(auditSet.getOwnerEmail())
                                    .accessStatus(AccessStatus.UNDER_REVIEW.toString())
                                    .isFavourite(false)
                                    .build();
                            auditSetLists.add(auditSetList3);
                        }
                    }
                }
            }
        }

        return auditSetLists;
    }

    public ApiResponse viewExtAuditorEventLog(String auditSetId, Long itemId, String userEmail, DistributedTransaction transaction) {
        try {
            List<AuditorLogs> auditorLogs = auditorLogsRepository.getExtAuditorAccessLog(auditSetId, itemId, transaction);

            List<ExtAuditorAccessLog> extAuditorAccessLogs = auditorLogs.stream()
                    .map(auditorLog -> ExtAuditorAccessLog.builder()
                            .ownerName(auditorLog.getUserEmail())
                            .eventType(auditorLog.getEventType())
                            .itemType(auditorLog.getItemType())
                            .eventDate(auditorLog.getEventDate())
                            .build())
                    .filter(e -> userEmail == null || e.getOwnerName().equals(userEmail))
                    .sorted(Comparator.comparingLong(ExtAuditorAccessLog::getEventDate))
                    .collect(Collectors.toList());

            if (!extAuditorAccessLogs.isEmpty()) {
                return new ApiResponse(true, "", HttpStatus.OK, extAuditorAccessLogs);
            } else {
                return new ApiResponse(true, Translator.toLocale("com.event.NotFound"), HttpStatus.OK, extAuditorAccessLogs);
            }
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            return new ApiResponse(false, Translator.toLocale("com.auditorAccessLog.error"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public ApiResponse updateAuditSetInfo(String auditSetId, UpdateAuditSet updateAuditSetInfo, String currentEmail, DistributedTransaction transaction) throws CrudException {
        try {
            //get user by mail Id
            User user = userRepository.getByUserEmail(currentEmail, transaction);

            //check user is present or not
            if (user == null) {
                throw new NotFoundException((Translator.toLocale("com.user.notFound")));
            }

            AuditSet auditSet = auditSetRepository.get(auditSetId, transaction);
            //check audit set is present or not
            if (auditSet == null) {
                throw new NotFoundException(Translator.toLocale("com.auditSet.notFound"));
            }

            //Check for duplicate name
            List<AuditSet> auditSetList = auditSetRepository.getAuditSetList(transaction);

            if (auditSetList != null && auditSetList.stream()
                    .filter(e -> !e.getAuditSetId().equals(auditSet.getAuditSetId()))
                    .anyMatch(e -> !e.getIsDeleted() && e.getAuditSetName() != null && e.getAuditSetName().equals(updateAuditSetInfo.getUpdateAuditSetInfo().getAuditSetName()))) {
                return new ApiResponse(false, Translator.toLocale("com.name.alreadyExits"), HttpStatus.BAD_REQUEST, null);
            }

            Collaborator aclJson = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
            //check role for creating audit set


            if ((commonService.isOwner(aclJson, currentEmail) || commonService.isCoOwner(aclJson, currentEmail))) {
                auditSet.setAuditSetName(updateAuditSetInfo.getUpdateAuditSetInfo().getAuditSetName());
                auditSet.setDescription(updateAuditSetInfo.getUpdateAuditSetInfo().getDescription());
                // Convert JSON string to a list of AuditGroupListForAuditSet

                List<AuditGroupListForAuditSet> auditGroupList;
                Collaborator collaboratorList;
                try {
                    collaboratorList = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
                    log.info("Old collaborates : {}", collaboratorList);
                    auditGroupList = objectMapper.readValue(auditSet.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
                    });
                } catch (Exception e) {
                    throw new GenericException("Error parsing JSON");
                }

                // Update collaborator lists
                updateCollaborators(collaboratorList.getOwnedBy().getEmailId(), collaboratorList.getCoOwners(), updateAuditSetInfo.getUpdateAuditSetCollaborators().getCoOwners(), auditSet, CollaboratorUserRoles.CO_OWNER.toString(), transaction);
                updateCollaborators(collaboratorList.getOwnedBy().getEmailId(), collaboratorList.getMembers(), updateAuditSetInfo.getUpdateAuditSetCollaborators().getMembers(), auditSet, CollaboratorUserRoles.MEMBER.toString(), transaction);
                updateCollaborators(collaboratorList.getOwnedBy().getEmailId(), collaboratorList.getReviewers(), updateAuditSetInfo.getUpdateAuditSetCollaborators().getReviewers(), auditSet, CollaboratorUserRoles.REVIEWER.toString(), transaction);

                List<AuditGroupListForAuditSet> auditGroupListForAuditSets = updateGrpCollaborator(auditGroupList, updateAuditSetInfo.getUpdateAuditSetCollaborators().getGrpIDs(), auditSet, transaction);
                collaboratorList.setMembers(updateAuditSetInfo.getUpdateAuditSetCollaborators().getMembers());
                collaboratorList.setCoOwners(updateAuditSetInfo.getUpdateAuditSetCollaborators().getCoOwners());
                collaboratorList.setReviewers(updateAuditSetInfo.getUpdateAuditSetCollaborators().getReviewers());

                String updatedAclJson;
                String updateGroupJson;
                try {
                    updatedAclJson = objectMapper.writeValueAsString(collaboratorList);
                    updateGroupJson = objectMapper.writeValueAsString(auditGroupListForAuditSets);
                } catch (JsonProcessingException e) {
                    e.printStackTrace(); // Handle the exception appropriately
                    return new ApiResponse(false, "Error converting Collaborator to JSON", HttpStatus.INTERNAL_SERVER_ERROR, null);
                }

                // Update the aclJson field of the auditSet
                auditSet.setAclJson(updatedAclJson);
                auditSet.setAuditGroupListJson(updateGroupJson);

                // Persist the updated auditSet
                auditSetRepository.create(auditSet, transaction);

                // Iterate over the list and get the group IDs
                for (AuditGroupListForAuditSet auditGroup : auditGroupList) {
                    String groupId = auditGroup.getAuditGroupId();
                    AuditGrpAuditSetMapping auditGrpAuditSetMapping = auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(groupId, auditSet.getAuditSetId(), transaction);
                    if (auditGrpAuditSetMapping != null) {
                        auditGrpAuditSetMapping.setAuditSetName(auditSet.getAuditSetName());
                        auditGrpAuditSetMappingRepository.create(auditGrpAuditSetMapping, transaction);
                    }

                }
                return new ApiResponse(true, Translator.toLocale("com.auditSetInfo.update"), HttpStatus.OK, null);
            } else {
                return new ApiResponse(false, Translator.toLocale("com.userNot.haveRole"), HttpStatus.BAD_REQUEST, null);
            }
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (JsonProcessingException e) {
            throw new GenericException("Something went wrong with praising json");
        }
    }


    private void updateCollaborators(String auditSetOwner, List<CollaboratorUser> existingList, List<CollaboratorUser> updatedList,
                                     AuditSet auditSet, String role, DistributedTransaction transaction) {
        for (CollaboratorUser user : updatedList) {

            User byUserEmail = userRepository.getByUserEmail(user.getEmailId(), transaction);

            if (!existingList.stream().anyMatch(existingUser -> existingUser.getEmailId().equals(user.getEmailId()))) {

                // Retrieve user's roles from JSON
                List<String> roleSystem;
                try {
                    roleSystem = objectMapper.readValue(byUserEmail.getRoleJson(), new TypeReference<List<String>>() {
                    });
                    log.info("Collaborator role: {}", roleSystem);
                } catch (JsonProcessingException e) {
                    throw new GenericException("Error converting Json to string list");
                }

                // Add the user to the appropriate role based on their assigned roles
                if (roleSystem.contains(UserRoles.AUDIT_ADMIN.toString())) {
                    if (CollaboratorUserRoles.CO_OWNER.toString().equals(role)) {


                        if (user.getEmailId().equals(auditSetOwner)) {
                            throw new GenericException(Translator.toLocale("com.ownerNot.AddCoOwner"));

                        }
                        AuditSetCollaborators newCollaborator = new AuditSetCollaborators();
                        newCollaborator.setAuditSetId(auditSet.getAuditSetId());
                        newCollaborator.setUserEmail(user.getEmailId());
                        newCollaborator.setAuditSetName(auditSet.getAuditSetName());
                        newCollaborator.setUserName(user.getUserName());
                        newCollaborator.setAuditSetRole(role);

                        AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSet.getAuditSetId(), role, user.getEmailId(), transaction);

                        if (auditSetCollaborators == null) {
                            auditSetCollaboratorsRepository.create(newCollaborator, transaction);

                        }

                    } else {
                        throw new GenericException(Translator.toLocale("com.user.role.auditAdmin"));
                    }
                } else if (roleSystem.contains(UserRoles.GENERAL_USER.toString())) {
                    if (CollaboratorUserRoles.MEMBER.toString().equals(role)) {
                        if (user.getEmailId().equals(auditSetOwner)) {
                            throw new GenericException(Translator.toLocale("com.Owner.NotAddAsMember"));

                        }
                        AuditSetCollaborators newCollaborator = new AuditSetCollaborators();
                        newCollaborator.setAuditSetId(auditSet.getAuditSetId());
                        newCollaborator.setUserEmail(user.getEmailId());
                        newCollaborator.setAuditSetName(auditSet.getAuditSetName());
                        newCollaborator.setUserName(user.getUserName());
                        newCollaborator.setAuditSetRole(role);
                        newCollaborator.setAccessStatus(AccessStatus.NEWLY_ADDED.toString());
                        newCollaborator.setIsFavourite(false);


                        AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSet.getAuditSetId(), role, user.getEmailId(), transaction);

                        if (auditSetCollaborators == null) {
                            auditSetCollaboratorsRepository.create(newCollaborator, transaction);

                        }
                    } else {
                        throw new GenericException(Translator.toLocale("com.user.role.generalUser"));
                    }
                } else if (roleSystem.contains(UserRoles.EXTERNAL_AUDITOR.toString())) {
                    if (CollaboratorUserRoles.REVIEWER.toString().equals(role)) {
                        if (user.getEmailId().equals(auditSetOwner)) {
                            throw new GenericException(Translator.toLocale("com.Owner.NotAddAsReviewer"));

                        }
                        AuditSetCollaborators newCollaborator = new AuditSetCollaborators();
                        newCollaborator.setAuditSetId(auditSet.getAuditSetId());
                        newCollaborator.setUserEmail(user.getEmailId());
                        newCollaborator.setAuditSetName(auditSet.getAuditSetName());
                        newCollaborator.setUserName(user.getUserName());
                        newCollaborator.setAuditSetRole(role);
                        newCollaborator.setAccessStatus(AccessStatus.NEWLY_ADDED.toString());
                        newCollaborator.setIsFavourite(false);



                        AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSet.getAuditSetId(), role, user.getEmailId(), transaction);

                        if (auditSetCollaborators == null) {
                            auditSetCollaboratorsRepository.create(newCollaborator, transaction);
                        }
                    } else {
                        throw new GenericException(Translator.toLocale("com.user.role.externalAuditor"));
                    }
                } else {
                    throw new GenericException(Translator.toLocale("com.given.user") + roleSystem + Translator.toLocale("com.notAdd.role") + role);
                }
            }
        }

        for (CollaboratorUser user : existingList) {

            if (!updatedList.stream().anyMatch(updatedUser -> updatedUser.getEmailId().equals(user.getEmailId()))) {

                AuditSetCollaborators auditSetCollaborators = auditSetCollaboratorsRepository.get(auditSet.getAuditSetId(), role, user.getEmailId(), transaction);

                if (auditSetCollaborators != null) {
                    auditSetCollaboratorsRepository.delete(auditSet.getAuditSetId(), role, user.getEmailId(), transaction);
                }
            }
        }
    }


    private List<AuditGroupListForAuditSet> updateGrpCollaborator(List<AuditGroupListForAuditSet> auditGroupList, List<String> grpIDs, AuditSet auditSet, DistributedTransaction transaction) {

        // Create a set of existing group IDs for faster lookup
        Set<String> existingGrpIds = auditGroupList.stream()
                .map(AuditGroupListForAuditSet::getAuditGroupId)
                .collect(Collectors.toSet());

        List<AuditGroupListForAuditSet> updatedList = new ArrayList<>();

        // Iterate over grpIDs to add new groups and update mappings
        for (String id : grpIDs) {
            if (!existingGrpIds.contains(id)) {
                // Add new group
                AuditGroup auditGrp = auditGroupRepository.getAuditGroup(id, transaction);
                auditGroupList.add(AuditGroupListForAuditSet.builder()
                        .auditGroupId(auditGrp.getAuditGroupId())
                        .auditGroupName(auditGrp.getAuditGroupName())
                        .description(auditGrp.getDescription())
                        .build());

                // Create new mapping if not already exists
                if (auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGrp.getAuditGroupId(), auditSet.getAuditSetId(), transaction) == null) {
                    AuditGrpAuditSetMapping newObject = AuditGrpAuditSetMapping.builder()
                            .AuditGroupId(auditGrp.getAuditGroupId())
                            .AuditGroupName(auditGrp.getAuditGroupName())
                            .auditSetId(auditSet.getAuditSetId())
                            .auditSetName(auditSet.getAuditSetName())
                            .build();
                    auditGrpAuditSetMappingRepository.create(newObject, transaction);
                }
            }
        }

        // Iterate over auditGroupList to remove deleted groups and mappings
        for (AuditGroupListForAuditSet auditGrp : auditGroupList) {
            if (!grpIDs.contains(auditGrp.getAuditGroupId())) {
                // Remove deleted mapping
                AuditGrpAuditSetMapping auditGroupAndAuditSetMapping = auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGrp.getAuditGroupId(), auditSet.getAuditSetId(), transaction);
                if (auditGroupAndAuditSetMapping != null) {
                    auditGrpAuditSetMappingRepository.delete(auditGrp.getAuditGroupId(), auditSet.getAuditSetId(), transaction);
                }
            } else {
                updatedList.add(auditGrp);
            }
        }

        return updatedList;
    }

    public Boolean isItemExistInAuditSet(Long itemId, String auditSetId, DistributedTransaction transaction) {

        AuditSetItem auditSetItem = auditSetItemRepository.get(auditSetId, itemId, transaction);
        if (auditSetItem == null) {
            return false;
        }
        return true;
    }
}