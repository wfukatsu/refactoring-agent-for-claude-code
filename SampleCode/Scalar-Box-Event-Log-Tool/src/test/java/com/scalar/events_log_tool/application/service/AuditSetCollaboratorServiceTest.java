package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.CollaboratorUserRoles;
import com.scalar.events_log_tool.application.dto.AuditSetCollab;
import com.scalar.events_log_tool.application.dto.Collaborator;
import com.scalar.events_log_tool.application.dto.CollaboratorUser;
import com.scalar.events_log_tool.application.model.AuditSet;
import com.scalar.events_log_tool.application.model.AuditSetCollaborators;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuditSetCollaboratorServiceTest {

    @MockBean
    DistributedTransactionManager manager;
    @Autowired
    private AuditSetCollaboratorService auditSetCollaboratorService;
    @MockBean
    private AuditSetRepository auditSetRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CommonService commonService;
    @MockBean
    private DistributedTransaction tx;
    @MockBean
    private AuditSetCollaboratorsRepository auditSetCollaboratorRepository;
    @SpyBean
    private AuditSetCollaboratorService realService;

    @MockBean
    private AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository;
    @MockBean
    private AuditGroupRepository auditGroupRepository;
//    @MockBean
//    private ObjectMapper objectMapper;

    @BeforeEach
    private void setUp() throws TransactionException {
        when(manager.start()).thenReturn(tx);
    }

    @Test
    @DisplayName("Test Add User To AuditSet Success Scenario ")
    void testAddUserToAuditSet() throws Exception {

//        ObjectMapper objectMapper = new ObjectMapper();
//        String currentUser = "currentuser@gmail.com";
//        //  String auditGroupId = "12344557890";
//        // Arrange mock input data
//        List<AddUserAuditSet> list = new ArrayList<>();
//
//        List<String> role = Arrays.asList("AUDIT_ADMIN");
//
//        String roleString = objectMapper.writeValueAsString(role);
//
//
//        User user1 = new User();
//        user1.setUserEmail(currentUser);
//        user1.setName("current user");
//        user1.setRoleJson(roleString);
//
//        AddUserAuditSet auditSetCollab = new AddUserAuditSet();
//        auditSetCollab.setAuditSetRole("CO_OWNER");
//        auditSetCollab.setUserEmail("jayesh@gmail.com");
//        list.add(auditSetCollab);
//        List<String> grpIds = Arrays.asList("12347890");
//        ListAuditSetCollab listAuditSetCollab = ListAuditSetCollab.builder()
//                .auditSetId("8888888")
//                .auditSetCollab(list)
//                .grpIds(grpIds)
//                .build();
//
//
//        //set list of groups
//        List<AuditGroupListForAuditSet> auditGroupList = new ArrayList<>();
//
//        AuditGroupListForAuditSet auditGroupListForAuditSet = new AuditGroupListForAuditSet();
//        auditGroupListForAuditSet.setAuditGroupId("1234567734448");
//        auditGroupListForAuditSet.setAuditGroupName("audit group old");
//        auditGroupListForAuditSet.setDescription("audit group old");
//
//
//        AuditGroupListForAuditSet auditGroupListForAuditSet1 = new AuditGroupListForAuditSet();
//        auditGroupListForAuditSet1.setAuditGroupId("123456778888");
//        auditGroupListForAuditSet1.setAuditGroupName("audit group old1");
//        auditGroupListForAuditSet1.setDescription("audit group old1");
//
//        auditGroupList.add(auditGroupListForAuditSet);
//        auditGroupList.add(auditGroupListForAuditSet1);
//
//        String groupListJson = objectMapper.writeValueAsString(auditGroupList);
//
//
//        //set collaborators
//        CollaboratorUser collaboratorUser = new CollaboratorUser();
//        collaboratorUser.setUserId(123L);
//        collaboratorUser.setUserName("old user");
//        collaboratorUser.setEmailId("adduser@gmail.com");
//        collaboratorUser.setRole("CO_OWNER");
//
//        CollaboratorUser collaboratorOwner = new CollaboratorUser();
//        collaboratorOwner.setUserId(6789L);
//        collaboratorOwner.setUserName("old owner");
//        collaboratorOwner.setEmailId("olduserowner@gmail.com");
//        collaboratorOwner.setRole("OWNER");
//
//        List<CollaboratorUser> coOwners = new ArrayList<>();
//        coOwners.add(collaboratorUser);
//
//        Collaborator aclJson = new Collaborator();
//        aclJson.setOwnedBy(collaboratorOwner);
//        aclJson.setCoOwners(coOwners);
//        aclJson.setMembers(Collections.emptyList());
//        aclJson.setReviewers(Collections.emptyList());
//
//        String aclJsonInString = GenericUtility.convertObjectToStringJson(aclJson);
//
//        AuditSet auditSet = new AuditSet();
//        auditSet.setAuditSetId("5555555");
//        auditSet.setAuditSetName("AuditSet_name");
//        auditSet.setAclJson(aclJsonInString);
//        auditSet.setAuditGroupListJson(groupListJson);
//        auditSet.setOwnerEmail(user1.getUserEmail());
//        auditSet.setOwnerName(user1.getName());
//
//        //read old json
//        Collaborator oldAclJson = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
//        List<AuditGroupListForAuditSet> oldGroupList;
//        oldGroupList = objectMapper.readValue(auditSet.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
//        });
//        List<String> roleSystem;
//        // Convert JSON string to a list of roles
//        roleSystem = objectMapper.readValue(user1.getRoleJson(), new TypeReference<List<String>>() {
//        });
//
//        String jsonMock = objectMapper.writeValueAsString(Arrays.asList("EXTERNAL_AUDITOR"));
//        User user = new User();
//        user.setId(123L);
//        user.setName("raj");
//        user.setUserEmail(currentUser);
//        user.setRoleJson(user1.getRoleJson());
//
//        User userOwner = new User();
//        userOwner.setId(6789L);
//        userOwner.setName("priya owner");
//        userOwner.setUserEmail("priyaowner@gmail.com");
//        userOwner.setRoleJson(jsonMock);
//        //set  new collaborators
//        CollaboratorUser collaboratorNewUser = new CollaboratorUser();
//        collaboratorNewUser.setUserId(123L);
//        collaboratorNewUser.setUserName("jiya");
//        collaboratorNewUser.setEmailId("jiyauser@gmail.com");
//        collaboratorNewUser.setRole("CO_OWNER");
//
//        CollaboratorUser collaboratorNewOwner = new CollaboratorUser();
//        collaboratorNewOwner.setUserId(6789L);
//        collaboratorNewOwner.setUserName("riya owner");
//        collaboratorNewOwner.setEmailId("riyaowner@gmail.com");
//        collaboratorNewOwner.setRole("OWNER");
//
//        List<CollaboratorUser> coOwnersNew = new ArrayList<>();
//        coOwnersNew.add(collaboratorUser);
//
//        Collaborator newAclJson = new Collaborator();
//        aclJson.setOwnedBy(collaboratorOwner);
//        aclJson.setCoOwners(coOwners);
//        aclJson.setMembers(Collections.emptyList());
//        aclJson.setReviewers(Collections.emptyList());
//
//
//        String newAclJsonString = objectMapper.writeValueAsString(newAclJson);
//
//
//        CollaboratorUser owner = aclJson.getOwnedBy();
//        //   auditSet.setAclJson(objectMapper.writeValueAsString(newAclJsonString));
//
//
//        //set audit set collaborator
//        AuditSetCollaborators auditSetCollaborators = AuditSetCollaborators.builder()
//                .auditSetId(listAuditSetCollab.getAuditSetId())
//                .userEmail(user.getUserEmail())
//                .auditSetName(auditSet.getAuditSetName())
//                .userName(user.getName())
//                .auditSetRole(auditSetCollab.getAuditSetRole())
//                .accessStatus(AccessStatus.NEWLY_ADDED.toString())
//                .isFavourite(false)
//                .build();
//
//
//        //set audit group
//        AuditGroup auditGroup = new AuditGroup();
//        auditGroup.setAuditGroupId("12347890");
//        auditGroup.setAuditGroupName("audit group");
//        auditGroup.setDescription("audit group for audit set");
//
//
//        AuditSet updatedAuditSet = new AuditSet();
//        updatedAuditSet.setAuditSetId(listAuditSetCollab.getAuditSetId());
//        updatedAuditSet.setAuditSetName("AuditSet_name");
//        updatedAuditSet.setAclJson(aclJsonInString);
//        updatedAuditSet.setAuditGroupListJson(groupListJson);
//        updatedAuditSet.setOwnerEmail(user1.getUserEmail());
//        updatedAuditSet.setOwnerName(user1.getName());
//
//
//        //set new audit group
//        AuditGroup newAuditGroup = new AuditGroup();
//        newAuditGroup.setAuditGroupId("453544");
//        newAuditGroup.setAuditGroupName("audit group");
//        newAuditGroup.setDescription("audit group for audit set");
//
//
//        //set new audit group
//        AuditGroupListForAuditSet updatedAuditGroup = AuditGroupListForAuditSet.builder()
//                .auditGroupId(newAuditGroup.getAuditGroupId())
//                .auditGroupName(newAuditGroup.getAuditGroupName())
//                .description(newAuditGroup.getDescription())
//                .build();
//
//
//        String auditGroupListJson = updatedAuditSet.getAuditGroupListJson();
//        List<AuditGroupListForAuditSet> auditGroupList1;
//        auditGroupList1 = objectMapper.readValue(auditGroupListJson, new TypeReference<List<AuditGroupListForAuditSet>>() {
//        });
//        AuditGroupListForAuditSet updatedAuditGroupListForAuditSet = new AuditGroupListForAuditSet();
//        updatedAuditGroupListForAuditSet.setAuditGroupId(newAuditGroup.getAuditGroupId());
//        updatedAuditGroupListForAuditSet.setAuditGroupName(newAuditGroup.getAuditGroupName());
//        updatedAuditGroupListForAuditSet.setDescription(newAuditGroup.getDescription());
//
//
//        auditGroupList.add(updatedAuditGroupListForAuditSet);
//
//
//        String updatedGroupListJson = objectMapper.writeValueAsString(auditGroupList);
//        updatedAuditSet.setAuditGroupListJson(updatedGroupListJson);
//        auditGroupList1.add(updatedAuditGroup);
//        String updatedAuditGroupListJson = objectMapper.writeValueAsString(auditGroupList1);
//        //set audit group audit set mapping
//        // Create a mapping entry
//        AuditGrpAuditSetMapping auditGrpAuditSetMapping = new AuditGrpAuditSetMapping();
//        auditGrpAuditSetMapping.setAuditGroupId(newAuditGroup.getAuditGroupId());
//        auditGrpAuditSetMapping.setAuditGroupName(newAuditGroup.getAuditGroupName());
//        auditGrpAuditSetMapping.setAuditSetId(updatedAuditSet.getAuditSetId());
//        auditGrpAuditSetMapping.setAuditSetName(updatedAuditSet.getAuditSetName());
//
//        // Update the auditSet with the updated audit group list JSON
//        updatedAuditSet.setAuditGroupListJson(updatedAuditGroupListJson);
//        // Mock repository calls
//        when(commonService.isOwner(any(), anyString())).thenReturn(true);
//        when(commonService.isCoOwner(any(), anyString())).thenReturn(false);
//        when(auditSetRepository.get(listAuditSetCollab.getAuditSetId(), tx))
//                .thenReturn(auditSet);
//        when(auditSetRepository.get(updatedAuditSet.getAuditSetId(), tx))
//                .thenReturn(updatedAuditSet);
//        when(auditSetRepository.create(updatedAuditSet, tx)).thenReturn(updatedAuditSet);
//        when(userRepository.getByUserEmail(auditSetCollab.getUserEmail(), tx))
//                .thenReturn(user);
//        //  when(objectMapper.readValue(anyString(), eq(Collaborator.class))).thenReturn(aclJson);
//        //   when(objectMapper.readValue(anyString(), eq(CollaboratorUserRoles.class))).thenReturn(CollaboratorUserRoles.valueOf(roleString));
//        //  when(commonService.isOwner(aclJson, "adduserowner@gmail.com")).thenReturn(true);
//        //  when(commonService.isCoOwner(aclJson, "adduserowner@gmail.com")).thenReturn(false);
//        when(auditSetCollaboratorRepository.create(auditSetCollaborators, tx)).thenReturn(auditSetCollaborators);
//        when(auditSetRepository.create(auditSet, tx)).thenReturn(auditSet);
//        when(auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGrpAuditSetMapping.getAuditGroupId(), auditGrpAuditSetMapping.getAuditSetId(), tx)).thenReturn(auditGrpAuditSetMapping);
//        when(auditGrpAuditSetMappingRepository.create(auditGrpAuditSetMapping, tx)).thenReturn(auditGrpAuditSetMapping);
//        when(auditGroupRepository.getAuditGroup(auditGroup.getAuditGroupId(), tx)).thenReturn(auditGroup);
//        when(auditGroupRepository.create(auditGroup, tx)).thenReturn(auditGroup);
//        when(auditGroupRepository.getAuditGroup("12347890", tx)).thenReturn(newAuditGroup);
//        when(auditGroupRepository.create(newAuditGroup, tx)).thenReturn(newAuditGroup);
//
//        //  when(auditGroupRepository.getAuditGroup(updatedAuditGroup.getAuditGroupId(),tx)).thenReturn(updatedAuditGroup);
//        // Actual response
//        ApiResponse actualResponse = auditSetCollaboratorService.addUserToAuditSet(listAuditSetCollab, currentUser, tx);
//
//        //verify
//        verify(auditSetRepository, times(1)).get(listAuditSetCollab.getAuditSetId(), tx);
//        verify(userRepository, times(1)).getByUserEmail(auditSetCollab.getUserEmail(), tx);
//        verify(auditSetCollaboratorRepository, times(1)).create(auditSetCollaborators, tx);
//
//        //assert actual_response
//        assertEquals(HttpStatus.OK, actualResponse.getHttpStatus());
//        assertEquals("User added to AuditSet successfully", actualResponse.getMessage());
//        assertEquals(true, actualResponse.getStatus());
    }


    @Test
    public void testChangeAuditSetOwner() throws Exception {
        // Mock input data
        ObjectMapper objectMapper = new ObjectMapper();
        String auditSetId = "auditSet123";
        String newOwnerEmail = "newowner@gmail.com";
        String currentUser = "currentuser@gmail.com";

        CollaboratorUser collaboratorCoOwner = new CollaboratorUser();
        collaboratorCoOwner.setUserId(123L);
        collaboratorCoOwner.setUserName("jayesh");
        collaboratorCoOwner.setEmailId("newowner@gmail.com");
        collaboratorCoOwner.setRole("CO_OWNER");

        CollaboratorUser collaboratorOwner = new CollaboratorUser();
        collaboratorOwner.setUserId(6789L);
        collaboratorOwner.setUserName("jayesh owner");
        collaboratorOwner.setEmailId("currentuser@gmail.com");
        collaboratorOwner.setRole("OWNER");

        List<CollaboratorUser> coOwners = new ArrayList<>();
        coOwners.add(collaboratorCoOwner);

        Collaborator aclJson = new Collaborator();
        aclJson.setOwnedBy(collaboratorOwner);
        aclJson.setCoOwners(coOwners);
        aclJson.setMembers(Collections.emptyList());
        aclJson.setReviewers(Collections.emptyList());

        ObjectMapper objectMappernew = new ObjectMapper();
        String aclJsonInString = objectMappernew.writeValueAsString(aclJson);

        AuditSet auditSet = new AuditSet();
        auditSet.setAuditSetId(auditSetId);
        auditSet.setAuditSetName("AuditSet_name");
        auditSet.setOwnerEmail("currentuser@gmail.com");
        auditSet.setAclJson(aclJsonInString);

        User newOwner = new User();
        newOwner.setUserEmail(newOwnerEmail);

        // Mock calls
        when(auditSetRepository.get(auditSetId, tx)).thenReturn(auditSet);
        when(userRepository.getByUserEmail(newOwnerEmail, tx)).thenReturn(newOwner);
        when(commonService.isOwner(aclJson, currentUser)).thenReturn(true);
        when(commonService.isCoOwner(aclJson, currentUser)).thenReturn(true);

        AuditSetCollaborators auditSetCollaborators = AuditSetCollaborators.builder()
                .auditSetRole(CollaboratorUserRoles.CO_OWNER.toString())
                .build();

        AuditSetCollaborators auditSetCollaboratorsOwner = AuditSetCollaborators.builder()
                .auditSetRole(CollaboratorUserRoles.CO_OWNER.toString())
                .build();

        when(auditSetCollaboratorRepository.get(auditSetId, CollaboratorUserRoles.OWNER.toString(), currentUser, tx)).thenReturn(auditSetCollaborators);
        String previousRoleForCurrentOwner = auditSetCollaborators.getAuditSetRole();
        auditSetCollaborators.setAuditSetRole(CollaboratorUserRoles.CO_OWNER.toString());
        when(auditSetCollaboratorRepository.createAndDelete(auditSetCollaborators, previousRoleForCurrentOwner, tx)).thenReturn(auditSetCollaborators);

        when(auditSetCollaboratorRepository.get(auditSetId, CollaboratorUserRoles.CO_OWNER.toString(), newOwnerEmail, tx)).thenReturn(auditSetCollaborators);
        String previousRoleForwNewowner = auditSetCollaborators.getAuditSetRole();
        auditSetCollaboratorsOwner.setAuditSetRole(CollaboratorUserRoles.OWNER.toString());

        when(auditSetCollaboratorRepository.createAndDelete(auditSetCollaboratorsOwner, previousRoleForwNewowner, tx)).thenReturn(auditSetCollaboratorsOwner);

        // Actual response
        ApiResponse actualResponse = auditSetCollaboratorService.changeAuditSetOwner(auditSetId, newOwnerEmail, currentUser, tx);

        //Assert actual_response
        assertEquals(HttpStatus.OK, actualResponse.getHttpStatus());
        assertEquals("AuditSet owner changed successfully.", actualResponse.getMessage());
        assertEquals(true, actualResponse.getStatus());

        // Verify
        verify(auditSetRepository, times(1)).get(auditSetId, tx);
        verify(userRepository, times(1)).getByUserEmail(newOwnerEmail, tx);
        verify(auditSetCollaboratorRepository, times(1)).get(auditSetId, CollaboratorUserRoles.OWNER.toString(), currentUser, tx);
        verify(auditSetCollaboratorRepository, times(1)).get(auditSetId, CollaboratorUserRoles.CO_OWNER.toString(), newOwnerEmail, tx);
        verify(auditSetCollaboratorRepository, times(2)).createAndDelete(auditSetCollaborators, previousRoleForCurrentOwner, tx);
        verify(auditSetRepository).create(auditSet, tx);

    }


    @Test
    public void testGetCollaboratorForAuditSet() throws JsonProcessingException {
//        // Mock data
//        String auditSetId = "auditSetId";
//
//        CollaboratorUser collaboratorCoOwner = new CollaboratorUser();
//        collaboratorCoOwner.setUserId(123L);
//        collaboratorCoOwner.setUserName("jayesh");
//        collaboratorCoOwner.setEmailId("newowner@gmail.com");
//        collaboratorCoOwner.setRole("CO_OWNER");
//
//        CollaboratorUser collaboratorOwner = new CollaboratorUser();
//        collaboratorOwner.setUserId(6789L);
//        collaboratorOwner.setUserName("jayesh owner");
//        collaboratorOwner.setEmailId("currentuser@gmail.com");
//        collaboratorOwner.setRole("OWNER");
//
//        List<CollaboratorUser> coOwners = new ArrayList<>();
//        coOwners.add(collaboratorCoOwner);
//
//        Collaborator aclJson = new Collaborator();
//        aclJson.setOwnedBy(collaboratorOwner);
//        aclJson.setCoOwners(coOwners);
//        aclJson.setMembers(Collections.emptyList());
//        aclJson.setReviewers(Collections.emptyList());
//
//        ObjectMapper objectMappernew = new ObjectMapper();
//        String aclJsonInString = objectMappernew.writeValueAsString(aclJson);
//
//        AuditSet auditSet = new AuditSet();
//        auditSet.setAuditSetId(auditSetId);
//        auditSet.setAuditSetName("AuditSet_name");
//        auditSet.setAclJson(aclJsonInString);
//        auditSet.setIsDeleted(false);
//
//
//        // Mock calls
//        when(auditSetRepository.get(auditSetId, tx)).thenReturn(auditSet);
//        when(objectMapper.readValue(anyString(), eq(Collaborator.class))).thenReturn(aclJson);
//
//        // Actual response
//        ApiResponse response = auditSetCollaboratorService.getCollaboratorForAuditSet(auditSetId, tx);
//
//        // Verify
//        assertTrue(response.getStatus());
//        assertEquals("", response.getMessage());
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//        assertNotNull(response.getData());
    }

    @Test
    void updateAuditSetCollaborators() throws JsonProcessingException {
//        //mock input
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        List<String> roles = Arrays.asList("AUDIT_ADMIN");
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        String roleString = objectMapper1.writeValueAsString(roles);
//
//
//        String currentEmail = "mayuris@perceptcs.com";
//        User user = new User();
//        user.setUserEmail(currentEmail);
//        user.setName("current user");
//        user.setId(123l);
//        user.setRoleJson(roleString);
//        List<String> roleSystem;
//        roleSystem = objectMapper.readValue(user.getRoleJson(), new TypeReference<List<String>>() {
//        });
//        String auditSetId = "123456789";
//        List<String> groupIds = Arrays.asList("123456", "3456789", "3456790");
//        List<String> role = Arrays.asList("REWIEVER");
//
//        CollaboratorUser owner = new CollaboratorUser();
//        owner.setUserId(user.getId());
//        owner.setUserName(user.getName());
//        owner.setEmailId(user.getUserEmail());
//        owner.setRole("OWNER");
//
//        CollaboratorUser coOwner = new CollaboratorUser();
//        coOwner.setUserId(123L);
//        coOwner.setUserName("mayuri sutar");
//        coOwner.setEmailId("mayuris@perceptcs.com");
//        coOwner.setRole("CO_OWNER");
//
//        List<CollaboratorUser> coOwners = new ArrayList<>();
//        coOwners.add(coOwner);
//
//        Collaborator collaborator = new Collaborator();
//        collaborator.setOwnedBy(owner);
//        collaborator.setCoOwners(coOwners);
//        collaborator.setMembers(Collections.emptyList());
//        collaborator.setReviewers(Collections.emptyList());
//
//        String aclJsonInString = GenericUtility.convertObjectToStringJson(collaborator);
//
//        //set audit group list for audit set
//        AuditGroupListForAuditSet oldAuditGroup = new AuditGroupListForAuditSet();
//        oldAuditGroup.setAuditGroupId("123reerwe456");
//        oldAuditGroup.setAuditGroupName("audit group for audit set old");
//        oldAuditGroup.setDescription("audit group for audit set old");
//
//        AuditGroupListForAuditSet oldAuditGroup1 = new AuditGroupListForAuditSet();
//        oldAuditGroup1.setAuditGroupId("123456990");
//        oldAuditGroup1.setAuditGroupName("audit group for audit set1");
//        oldAuditGroup1.setDescription("audit group for audit set1");
//
//
//        List<AuditGroupListForAuditSet> auditGroupListForAuditSets = new ArrayList<>();
//        auditGroupListForAuditSets.add(oldAuditGroup);
//        auditGroupListForAuditSets.add(oldAuditGroup1);
//
//        String oldAuditGroupList = GenericUtility.convertObjectToStringJson(auditGroupListForAuditSets);
//        //set audit set
//        AuditSet auditSet = new AuditSet();
//        auditSet.setAuditSetId(auditSetId);
//        auditSet.setAuditSetName("audit set old");
//        auditSet.setAclJson(aclJsonInString);
//        auditSet.setAuditGroupListJson(oldAuditGroupList);
//
//
//        List<AuditGroupListForAuditSet> auditGroupList = new ArrayList<>();
//        Collaborator collaboratorList;
//        collaboratorList = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
//        auditGroupList = objectMapper.readValue(auditSet.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
//        });
//
//        //UpdateAuditSetCollaborators
//
//        CollaboratorUser collaboratorUser = new CollaboratorUser();
//        collaboratorUser.setUserId(12L);
//        collaboratorUser.setUserName("owner user");
//        collaboratorUser.setEmailId("owner@gmail.com");
//        collaboratorUser.setRole("OWNER");
//
//        CollaboratorUser collaboratorCoOwner1 = new CollaboratorUser();
//        collaboratorCoOwner1.setUserId(123L);
//        collaboratorCoOwner1.setUserName("new coowner");
//        collaboratorCoOwner1.setEmailId("newcowner@gmail.com");
//        collaboratorCoOwner1.setRole("CO_OWNER");
//
//        CollaboratorUser members = new CollaboratorUser();
//        members.setUserId(6789L);
//        members.setUserName("new member");
//        members.setEmailId("newmember@gmail.com");
//        members.setRole("MEMBER");
//
//
//        CollaboratorUser reviewers = new CollaboratorUser();
//        members.setUserId(6789L);
//        members.setUserName("new reviewer");
//        members.setEmailId("newreviewer@gmail.com");
//        members.setRole("REVIEWERS");
//
//
//        List<CollaboratorUser> coOwners1 = new ArrayList<>();
//        coOwners1.add(collaboratorCoOwner1);
//
//        List<CollaboratorUser> memberss = new ArrayList<>();
//        memberss.add(members);
//        List<CollaboratorUser> reviewers1 = new ArrayList<>();
//        reviewers1.add(reviewers);
//
//
//        Collaborator aclJson1 = new Collaborator();
//        aclJson1.setOwnedBy(collaboratorUser);
//        aclJson1.setCoOwners(coOwners1);
//        aclJson1.setMembers(memberss);
//        aclJson1.setReviewers(reviewers1);
//
//        String updatedAclJSon = GenericUtility.convertObjectToStringJson(aclJson1);
//
//        List<String> groupIds1 = Arrays.asList("123456", "3456789", "3456790");
//
//
//        UpdateAuditSetCollaborators updateAuditSetCollaborators = new UpdateAuditSetCollaborators();
//        updateAuditSetCollaborators.setCoOwners(coOwners1);
//        updateAuditSetCollaborators.setMembers(memberss);
//        updateAuditSetCollaborators.setReviewers(reviewers1);
//        updateAuditSetCollaborators.setGrpIDs(groupIds1);
//
//        //set audit group list for audit set
//        AuditGroupListForAuditSet auditGroupListForAuditSet = new AuditGroupListForAuditSet();
//        auditGroupListForAuditSet.setAuditGroupId("123456");
//        auditGroupListForAuditSet.setAuditGroupName("audit group for audit set");
//        auditGroupListForAuditSet.setDescription("audit group for audit set");
//
//        AuditGroupListForAuditSet auditGroupListForAuditSet1 = new AuditGroupListForAuditSet();
//        auditGroupListForAuditSet1.setAuditGroupId("123456990");
//        auditGroupListForAuditSet1.setAuditGroupName("audit group for audit set1");
//        auditGroupListForAuditSet1.setDescription("audit group for audit set1");
//
//
//        List<AuditGroupListForAuditSet> auditGroupListForAuditSetss = new ArrayList<>();
//        auditGroupListForAuditSetss.add(auditGroupListForAuditSet);
//        auditGroupListForAuditSetss.add(auditGroupListForAuditSet1);
//
//        String auditGroupListJson = GenericUtility.convertObjectToStringJson(auditGroupListForAuditSetss);
////
////
////        //set audit set
//        AuditSet updatedAuditSet = new AuditSet();
//        updatedAuditSet.setAuditSetId(auditSetId);
//        updatedAuditSet.setAuditSetName("audit set old");
//        updatedAuditSet.setAclJson(updatedAclJSon);
//        updatedAuditSet.setAuditGroupListJson(auditGroupListJson);
//
//
//        Collaborator collaboratorLists;
//        collaboratorLists = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
//
//        List<AuditGroupListForAuditSet> auditGroupLists = new ArrayList<>();
//        auditGroupLists = objectMapper.readValue(auditSet.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
//        });
//
//
//        //set audit set collaborators
//        AuditSetCollaborators newCollaborator = new AuditSetCollaborators();
//        newCollaborator.setAuditSetId(updatedAuditSet.getAuditSetId());
//        newCollaborator.setUserEmail(members.getEmailId());
//        newCollaborator.setAuditSetName(updatedAuditSet.getAuditSetName());
//        newCollaborator.setUserName(members.getUserName());
//        newCollaborator.setAuditSetRole("OWNER");
//        List<String> roless = Arrays.asList("EXTERNAL_AUDITOR");
//
//        String roleStrings = GenericUtility.convertObjectToStringJson(roless);
//
//        User user1 = new User();
//        user1.setUserEmail(members.getEmailId());
//        user1.setName(members.getUserName());
//        user1.setRoleJson(roleStrings);
//
//      List<String> roleSystems;
//        roleSystems = objectMapper.readValue(user1.getRoleJson(), new TypeReference<List<String>>() {
//        });
//
//
//
//
//
////        List<String> roleSystems;
////        roleSystems = objectMapper.readValue(user1.getRoleJson(), new TypeReference<List<String>>() {
////        });
//
//        //set audit group
//        AuditGroup auditGroup = new AuditGroup();
//        auditGroup.setAuditGroupId("123456");
//        auditGroup.setAuditGroupName("audit group new");
//        auditGroup.setDescription("audit group new");
//
////        //set audit group list in audit group list of audit set
//        auditGroupLists.add(AuditGroupListForAuditSet.builder()
//                .auditGroupId(auditGroup.getAuditGroupId())
//                .auditGroupName(auditGroup.getAuditGroupName())
//                .description(auditGroup.getDescription())
//                .build());
//
////        //set audit group mapping
//        AuditGrpAuditSetMapping newObject = AuditGrpAuditSetMapping.builder()
//                .AuditGroupId(auditGroup.getAuditGroupId())
//                .AuditGroupName(auditGroup.getAuditGroupName())
//                .auditSetId(updatedAuditSet.getAuditSetId())
//                .auditSetName(updatedAuditSet.getAuditSetName())
//                .build();
//
//        //updated audit set group
//        List<AuditGroupListForAuditSet> auditGroupListForAuditSets1 = new ArrayList<>();
//        collaboratorLists.setMembers(updateAuditSetCollaborators.getMembers());
//        collaboratorLists.setCoOwners(updateAuditSetCollaborators.getCoOwners());
//        collaboratorLists.setReviewers(updateAuditSetCollaborators.getReviewers());
//
//
//        String updatedAclJson = objectMapper.writeValueAsString(updateAuditSetCollaborators);
//        String updateGroupJson = objectMapper.writeValueAsString(auditGroupListForAuditSets1);
//
//
//        // updatedAuditSet.setAclJson(updatedAclJson);
//        updatedAuditSet.setAuditGroupListJson(updateGroupJson);
//
//        //mock repository
//        when(userRepository.getByUserEmail(newCollaborator.getUserEmail(), tx)).thenReturn(user1);
//        when(auditSetCollaboratorRepository.get(auditSetId, roles.toString(), user.getUserEmail(), tx)).thenReturn(newCollaborator);
//        when(auditSetCollaboratorRepository.create(newCollaborator, tx)).thenReturn(newCollaborator);
//        when(auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGroup.getAuditGroupId(), updatedAuditSet.getAuditSetId(), tx)).thenReturn(newObject);
//        when(auditSetRepository.get(auditSet.getAuditSetId(), tx)).thenReturn(auditSet);
//        when(auditSetRepository.create(auditSet, tx)).thenReturn(auditSet);
//        when(commonService.isOwner(any(), anyString())).thenReturn(true);
//        when(commonService.isCoOwner(any(), anyString())).thenReturn(true);
//        //    when(auditSetRepository.get(auditSetId, tx)).thenReturn(updatedAuditSet);
//
//        //  when(auditSetRepository.create(updatedAuditSet, tx)).thenReturn(updatedAuditSet);
//
//        // Call the method
//        ApiResponse response = auditSetCollaboratorService.updateAuditSetCollaborators(auditSetId, updateAuditSetCollaborators, currentEmail, tx);
//
//        // Verify the response
//        assertTrue(response.getStatus());
//        assertEquals("com.auditSet.update", response.getMessage());
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//        assertNull(response.getData());
//
//        //verify repository
//        verify(userRepository,times(2)).getByUserEmail(any(), any());
//        verify(auditSetRepository, times(1)).get(anyString(), any());
//        verify(auditSetRepository, times(1)).create(any(), any());


    }


}