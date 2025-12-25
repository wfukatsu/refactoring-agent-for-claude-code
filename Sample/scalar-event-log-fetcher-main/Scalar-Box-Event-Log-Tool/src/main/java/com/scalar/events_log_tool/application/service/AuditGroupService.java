package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.AuditGroupPrivileges;
import com.scalar.events_log_tool.application.constant.CollaboratorUserRoles;
import com.scalar.events_log_tool.application.constant.UserRoles;
import com.scalar.events_log_tool.application.dto.AuditGroupListForAuditSet;
import com.scalar.events_log_tool.application.dto.CreateAuditGroup;
import com.scalar.events_log_tool.application.dto.GroupUserPrivileges;
import com.scalar.events_log_tool.application.dto.UpdateAuditGroup;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.exception.NotFoundException;
import com.scalar.events_log_tool.application.model.*;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.AuditGroupInfo;
import com.scalar.events_log_tool.application.responsedto.AuditGroupResponse;
import com.scalar.events_log_tool.application.responsedto.AuditorGroupMemberList;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuditGroupService {
    private final AuditGroupRepository auditGroupRepository;

    private final AuditSetRepository auditSetRepository;

    private final AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    private final UserAuditGroupRepository userAuditGroupRepository;

    public AuditGroupService(AuditGroupRepository auditGroupRepository, AuditSetRepository auditSetRepository, AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository, UserRepository userRepository, ObjectMapper objectMapper, UserAuditGroupRepository userAuditGroupRepository) {
        this.auditGroupRepository = auditGroupRepository;
        this.auditSetRepository = auditSetRepository;
        this.auditGrpAuditSetMappingRepository = auditGrpAuditSetMappingRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.userAuditGroupRepository = userAuditGroupRepository;
    }

    public ApiResponse createAuditGroup(CreateAuditGroup createAuditGroup, String currentEmail, DistributedTransaction transaction) throws TransactionException {

        try {
            User user = userRepository.getByUserEmail(currentEmail, transaction);

            if (user == null) {
                throw new NotFoundException(Translator.toLocale("com.user.notFound"));
            }

            if (createAuditGroup.getAuditGroupName() == null || createAuditGroup.getAuditGroupName().trim().length() == 0) {
                return new ApiResponse(false, Translator.toLocale("com.groupName.empty"), HttpStatus.BAD_REQUEST, null);
            }
            //Check for duplicate name
            List<AuditGroup> groupList = auditGroupRepository.getgroupList(transaction);
            if (groupList != null) {
                List<AuditGroup> duplicateGroup = groupList.stream()
                        .filter(e -> !e.getIsDeleted())
                        .filter(n -> n.getAuditGroupName().equals(createAuditGroup.getAuditGroupName()))
                        .collect(Collectors.toList());
                if (!duplicateGroup.isEmpty()) {
                    return new ApiResponse(false, Translator.toLocale("com.groupName.exits"), HttpStatus.BAD_REQUEST, null);

                }
            }

            //fetch authority from token
            List<String> collect = getAuthorities();

            //check role for creating audit set
            if (collect.contains(UserRoles.AUDIT_ADMIN.toString())) {
                // Set owner
                GroupUserPrivileges ownerPrivileges = new GroupUserPrivileges
                        (user.getUserEmail(), user.getName(),
                                AuditGroupPrivileges.OWNER.toString());

                // Set members from auditGroupUserList
                List<GroupUserPrivileges> members = createAuditGroup.getAuditGroupUserList().stream()
                        .map(auditGroupUser -> new GroupUserPrivileges(auditGroupUser.getUserEmail(),
                                auditGroupUser.getUserName(),
                                AuditGroupPrivileges.MEMBER.toString()))
                        .collect(Collectors.toList());

                // Combine owner and members
                List<GroupUserPrivileges> userPrivilegesList = new ArrayList<>();
                userPrivilegesList.add(ownerPrivileges);
                userPrivilegesList.addAll(members);


                // Convert object to JSON
                String membersJson = objectMapper.writeValueAsString(userPrivilegesList);

                // Create a SimpleDateFormat object for UTC with the format
                SimpleDateFormat utcFormat = GenericUtility.getUTCDateFormatWithMilliseconds();

                // Format the date in UTC
                String formattedUtcDate = utcFormat.format(new Date());
                String[] split = formattedUtcDate.split("-");
                String withoutMillis = split[1].substring(0, 6);

                //set data in audit group table
                AuditGroup auditGroup = AuditGroup.builder()
                        .userEmail(user.getUserEmail())
                        .auditGroupId(GenericUtility.generateUUID())
                        .auditGroupName(createAuditGroup.getAuditGroupName())
                        .description(createAuditGroup.getDescription())
                        .ownerId(user.getId())
                        .ownerName(user.getName())
                        .memberListJson(membersJson)
                        .createdAt(Long.parseLong((split[0] + withoutMillis)))
                        .isDeleted(false)
                        .build();

                auditGroupRepository.create(auditGroup, transaction);

                List<GroupUserPrivileges> memberList = getGroupUserPrivileges(auditGroup);
                for (GroupUserPrivileges member : memberList) {

                    //set data in userAuditGroup table
                    UserAuditGroup userAuditGroup = UserAuditGroup.builder()
                            .userEmail(member.getUserEmail())
                            .auditGroupId(auditGroup.getAuditGroupId())
                            .auditGroupName(auditGroup.getAuditGroupName())
                            .privilege(AuditGroupPrivileges.MEMBER.toString())
                            .build();
                    userAuditGroupRepository.create(userAuditGroup, transaction);
                }
                AuditGroupResponse auditGroupResponse = AuditGroupResponse.
                        builder()
                        .auditGroupId(auditGroup.getAuditGroupId())
                        .auditSetName(auditGroup.getAuditGroupName())
                        .description(auditGroup.getDescription())
                        .build();
                return new ApiResponse(true, Translator.toLocale("com.auditGroup.create"), HttpStatus.OK, auditGroupResponse);
            } else {

                return new ApiResponse(false, Translator.toLocale("com.userNot.createGroupRole"), HttpStatus.BAD_REQUEST, null);
            }
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            return new ApiResponse(false, Translator.toLocale("com.error.groupCreate"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private List<GroupUserPrivileges> getGroupUserPrivileges(AuditGroup auditGroup) {
        List<GroupUserPrivileges> memberList;
        try {
            // Convert JSON string to a list of AuditGroupUser
            memberList = objectMapper.readValue(auditGroup.getMemberListJson(), new TypeReference<List<GroupUserPrivileges>>() {
            });
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting Json to AuditGroupUser list");
        }
        return memberList;
    }


    public ApiResponse getListOfAuditGroup(String currentEmail, DistributedTransaction transaction) throws CrudException {
        try {
            User user = userRepository.getByUserEmail(currentEmail, transaction);

            if (user == null) {
                throw new NotFoundException(Translator.toLocale("com.user.notFound"));
            }

            // get list of audit group
            List<AuditGroup> auditGroupList = auditGroupRepository.getgroupList(transaction);

            List<AuditGroupInfo> auditGroupLists = new ArrayList<>();

            List<AuditGroup> auditGroups = auditGroupList.stream()
                    .filter(e -> !e.getIsDeleted())
                    .collect(Collectors.toList());

            if (auditGroups.isEmpty()) {
                // Return early if there are no audit groups
                return new ApiResponse(true, Translator.toLocale("com.groupListEmpty"), HttpStatus.OK, auditGroupLists);
            }

            for (AuditGroup auditGroup : auditGroups) {
                List<GroupUserPrivileges> memberList = getGroupUserPrivileges(auditGroup);
                //owner is get list audit group
                for (GroupUserPrivileges member : memberList) {
                    if (member.getPrivileges().contains(CollaboratorUserRoles.OWNER.toString())) {

                        String userEmail = member.getUserEmail();

                        AuditGroupInfo auditGroupList1 = AuditGroupInfo.builder()
                                .auditGroupId(auditGroup.getAuditGroupId())
                                .auditGroupName(auditGroup.getAuditGroupName())
                                .description(auditGroup.getDescription())
                                .memberCount(memberList.size() - 1)
                                .createdAt(auditGroup.getCreatedAt())
                                .ownedBy(userEmail)
                                .build();

                        auditGroupLists.add(auditGroupList1);
                        // Assuming you want to break out of the loop after finding the first owner
                        break;
                    }
                }
            }

            return new ApiResponse(true, "", HttpStatus.OK, auditGroupLists);
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            // Log the error for debugging purposes
            log.error("Error when fetching audit group list", ex);
            return new ApiResponse(false, Translator.toLocale("com.auditGroupFetching.error"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    public ApiResponse updateAuditGroup(String auditGroupId, UpdateAuditGroup updateAuditGroup, DistributedTransaction transaction) {
        try {
            AuditGroup auditGroup = auditGroupRepository.getAuditGroup(auditGroupId, transaction);

            if (auditGroup.getIsDeleted()) {
                throw new NotFoundException(Translator.toLocale("com.auditGroup.notFound"));
            }

            if (updateAuditGroup.getAuditGroupName() == null || updateAuditGroup.getAuditGroupName().trim().isEmpty()) {
                return new ApiResponse(false, Translator.toLocale("com.groupName.empty"), HttpStatus.BAD_REQUEST, null);
            }

            List<AuditGroup> groupList = auditGroupRepository.getgroupList(transaction);
            if (groupList != null && groupList.stream()
                    .filter(e -> !e.getAuditGroupId().equals(auditGroup.getAuditGroupId()))
                    .anyMatch(e -> !e.getIsDeleted() && e.getAuditGroupName() != null && e.getAuditGroupName().equals(updateAuditGroup.getAuditGroupName()))) {
                return new ApiResponse(false, Translator.toLocale("com.groupName.exits"), HttpStatus.BAD_REQUEST, null);
            }

            List<String> collect = getAuthorities();

            List<GroupUserPrivileges> existingMembers = getGroupUserPrivileges(auditGroup);

            List<String> removeMemberList = existingMembers.stream()
                    .map(GroupUserPrivileges::getUserEmail)
                    .filter(userEmail -> !updateAuditGroup.getUserEmailList().contains(userEmail))
                    .collect(Collectors.toList());


            if (collect.contains(UserRoles.AUDIT_ADMIN.toString())) {
                auditGroup.setAuditGroupName(updateAuditGroup.getAuditGroupName());
                auditGroup.setDescription(updateAuditGroup.getDescription());


                removeMemberList.forEach(toRemove -> {

                    UserAuditGroup userAuditGroup = userAuditGroupRepository.get(auditGroup.getAuditGroupId(), toRemove, transaction);
                    if (userAuditGroup != null) {
                        userAuditGroupRepository.delete(auditGroup.getAuditGroupId(), toRemove, transaction);
                    }


                });


                for (String newMember : updateAuditGroup.getUserEmailList()) {
                    Optional<GroupUserPrivileges> isExist = existingMembers.stream()
                            .filter(member -> member.getUserEmail().equals(newMember))
                            .findFirst();

                    if (!isExist.isPresent()) {
                        UserAuditGroup userAuditGroup = UserAuditGroup.builder()
                                .userEmail(newMember)
                                .auditGroupId(auditGroup.getAuditGroupId())
                                .auditGroupName(auditGroup.getAuditGroupName())
                                .privilege(AuditGroupPrivileges.MEMBER.toString())
                                .build();
                        UserAuditGroup userAuditGroupObject = userAuditGroupRepository.get(auditGroup.getAuditGroupId(), newMember, transaction);
                        if (userAuditGroupObject == null) {
                            userAuditGroupRepository.create(userAuditGroup, transaction);
                        } else {
                            userAuditGroupObject.setAuditGroupName(auditGroup.getAuditGroupName());
                            userAuditGroupRepository.create(userAuditGroupObject, transaction);
                        }

                        User user = userRepository.getByUserEmail(newMember, transaction);
                        GroupUserPrivileges groupUserPrivileges = GroupUserPrivileges.builder()
                                .userEmail(newMember)
                                .userName(user.getName())
                                .privileges(AuditGroupPrivileges.MEMBER.toString())
                                .build();
                        existingMembers.add(groupUserPrivileges);
                    }
                }

                existingMembers.removeIf(existingMember -> !updateAuditGroup.getUserEmailList().contains(existingMember.getUserEmail()) && !existingMember.getPrivileges().equalsIgnoreCase(AuditGroupPrivileges.OWNER.toString()));

                String jsonString = objectMapper.writeValueAsString(existingMembers);
                auditGroup.setMemberListJson(jsonString);

                auditGroupRepository.create(auditGroup, transaction);

                List<AuditGrpAuditSetMapping> userGrpAuditSetList = auditGrpAuditSetMappingRepository.getUserGrpAuditSetList(auditGroupId, transaction);

                for (AuditGrpAuditSetMapping auditGrpAuditSetMapping : userGrpAuditSetList) {
                    auditGrpAuditSetMapping.setAuditGroupName(auditGroup.getAuditGroupName());
                    auditGrpAuditSetMappingRepository.create(auditGrpAuditSetMapping, transaction);

                    AuditSet auditSet = auditSetRepository.get(auditGrpAuditSetMapping.getAuditSetId(), transaction);

                    List<AuditGroupListForAuditSet> groupListForAuditSets = objectMapper.readValue(auditSet.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
                    });

                    if (groupListForAuditSets != null && !groupListForAuditSets.isEmpty()) {
                        groupListForAuditSets.forEach(auditGroup1 -> {
                            auditGroup1.setAuditGroupName(updateAuditGroup.getAuditGroupName());
                            auditGroup1.setDescription(updateAuditGroup.getDescription());
                        });

                        String updatedJson = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
                        auditSet.setAuditGroupListJson(updatedJson);
                        auditSetRepository.create(auditSet, transaction);
                    }
                }
                return new ApiResponse(true, Translator.toLocale("com.auditGroup.update"), HttpStatus.OK, null);
            } else {
                return new ApiResponse(false, Translator.toLocale("com.userRole.forUpdateAuditGroup"), HttpStatus.BAD_REQUEST, null);
            }
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ApiResponse(false, Translator.toLocale("com.error.updateAuditGroup"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private List<String> getAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().map(e -> e.getAuthority()).collect(Collectors.toList());
    }


    public ApiResponse deleteAuditGroup(String auditGroupId, DistributedTransaction transaction) throws CrudException {

        AuditGroup auditGroup = auditGroupRepository.getAuditGroup(auditGroupId, transaction);

        String memberListJson = auditGroup.getMemberListJson();

        List<GroupUserPrivileges> memberEmails = new ArrayList<>();
        // Convert JSON string to List<String>
        try {
            memberEmails = objectMapper.readValue(memberListJson, new TypeReference<List<GroupUserPrivileges>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //fetch authority from token
        List<String> authorities = getAuthorities();

        //check role for creating audit set
        if (authorities.contains(UserRoles.AUDIT_ADMIN.toString())) {

            // Iterate through each member email
            for (GroupUserPrivileges memberEmail : memberEmails) {

                UserAuditGroup userAuditGroup = userAuditGroupRepository.get(auditGroupId, memberEmail.getUserEmail(), transaction);

                if (userAuditGroup != null) {
                    userAuditGroupRepository.delete(userAuditGroup.getAuditGroupId(), userAuditGroup.getUserEmail(), transaction);
                }

            }

            List<AuditGrpAuditSetMapping> allAuditSetByGroupId = auditGrpAuditSetMappingRepository.getAllAuditSetByGroupId(auditGroupId, transaction);

            allAuditSetByGroupId.forEach(e -> {
                auditGrpAuditSetMappingRepository.delete(auditGroupId, e.getAuditSetId(), transaction);
            });

            auditGroup.setIsDeleted(true);
            auditGroupRepository.create(auditGroup, transaction);
        } else {
            throw new GenericException(Translator.toLocale("com.userRole.deleteDenied"));
        }


        return new ApiResponse(true, Translator.toLocale("com.auditGroup.delete"), HttpStatus.OK, null);
    }


    public ApiResponse getListOfAuditGroupMembers(String auditGroupId, DistributedTransaction transaction) throws CrudException {
        try {
            AuditGroup auditGroup = auditGroupRepository.getAuditGroup(auditGroupId, transaction);

            if (auditGroupId == null) {
                throw new NotFoundException(Translator.toLocale("com.auditGroup.notFound"));
            }
            List<AuditorGroupMemberList> auditorGroupMemberList = new ArrayList<>();

            List<GroupUserPrivileges> memberList = getGroupUserPrivileges(auditGroup);

            memberList.stream()
                    .filter(e -> e.getPrivileges().equals(AuditGroupPrivileges.MEMBER.toString()))
                    .forEach(member -> {
                                AuditorGroupMemberList auditorGroupMemberList1 = AuditorGroupMemberList.builder()
                                        .userEmail(member.getUserEmail())
                                        .userName(member.getUserName())
                                        .build();
                                auditorGroupMemberList.add(auditorGroupMemberList1);
                            }

                    );

            return new ApiResponse(true, "", HttpStatus.OK, auditorGroupMemberList);
        } catch (NotFoundException ex) {
            return new ApiResponse(false, ex.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception ex) {
            // Log the error for debugging purposes
            log.error("Error when fetching audit group info", ex);
            return new ApiResponse(false, Translator.toLocale("com.auditGroupInfoFetching.error"), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}