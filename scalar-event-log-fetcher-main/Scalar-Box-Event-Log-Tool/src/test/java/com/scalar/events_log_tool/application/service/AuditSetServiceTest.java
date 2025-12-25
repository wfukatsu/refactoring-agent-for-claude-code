package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.AccessStatus;
import com.scalar.events_log_tool.application.dto.*;
import com.scalar.events_log_tool.application.model.*;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.AuditSetLists;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuditSetServiceTest {
    @MockBean
    DistributedTransactionManager manager;

    @MockBean
    DistributedTransaction tx;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleUserRepository roleUserRepository;

    @MockBean
    private AuditSetRepository auditSetRepository;

    @MockBean
    private AuditSetCollaboratorsRepository auditSetCollaboratorsRepository;


    @Autowired
    private AuditSetService auditService;
    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;
    @MockBean
    private AuditorLogsRepository auditorLogsRepository;
    @MockBean
    private UserAuditGroupRepository userAuditGroupRepository;
    @MockBean
    private AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository;

    @BeforeEach
    private void setUp() throws TransactionException {
        when(manager.start()).thenReturn(tx);
    }

//    @Test
//    @DisplayName("create audit set")
//    void createAuditSet() throws JsonProcessingException {
//        // Arrange mock input data
//        CreateAuditSet auditSets = CreateAuditSet.builder()
//                .auditName("new audit set 1")
//                .description("new audit set for testing")
//                .build();
//
//
//        List<String> role = Arrays.asList("AUDIT_ADMIN");
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        String roleString = objectMapper1.writeValueAsString(role);
//
//        // Mocking SecurityContextHolder
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
//        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
//
//        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        User user1 = User.builder()
//                .userEmail("mayuris@perceptcs.com")
//                .id(1L)
//                .name("mayuri sutar")
//                .password("mayu")
//                .imageUrl("image_url")
//                .organizationName("kanzen")
//                .roleJson(roleString)
//                .build();
//
//        CollaboratorUser collaboratorUser = CollaboratorUser.builder()
//                .userId(user1.getId())
//                .emailId(user1.getUserEmail())
//                .role("OWNER")
//                .userName(user1.getName())
//                .build();
//
//        Collaborator collaborator = Collaborator.builder()
//                .ownedBy(collaboratorUser)
//                .coOwners(new ArrayList<>())
//                .members(new ArrayList<>())
//                .reviewers(new ArrayList<>())
//                .build();
//
//        String collaboratorJson;
//        ObjectMapper objectMapper = new ObjectMapper();
//        collaboratorJson = objectMapper.writeValueAsString(collaborator);
//
//
//        AuditSet auditSet = AuditSet.builder()
//                .auditSetId("8af4333f-d738-4298-a6a2-2d40e7db5b4f")
//                .auditSetName(auditSets.getAuditName())
//                .description(auditSets.getDescription())
//                .ownerId(user1.getId())
//                .ownerName(user1.getName())
//                .ownerEmail(user1.getUserEmail())
//                .aclJson(collaboratorJson)
//                .isDeleted(false)
//                .build();
//
//        //read data from json
//        Collaborator collaborator1;
//        collaborator1 = objectMapper.readValue(auditSet.getAclJson(), Collaborator.class);
//
//        AuditSetCollaborators auditSetCollaborators = AuditSetCollaborators.builder()
//                .auditSetId(auditSet.getAuditSetId())
//                .userEmail(auditSet.getOwnerEmail())
//                .auditSetName(auditSet.getAuditSetName())
//                .auditSetRole(collaborator1.getOwnedBy().getRole())
//                .userName(auditSet.getOwnerName())
//                .build();
//
//        // Mock repository
//        when(userRepository.getByUserEmail(user1.getUserEmail(), tx)).thenReturn(user1);
//        when(auditSetRepository.create(eq(auditSet), eq(tx))).thenReturn(auditSet);
//        when(auditSetCollaboratorsRepository.create(auditSetCollaborators, tx)).thenReturn(auditSetCollaborators);
//
//        // Actual response
//        ApiResponse result = auditService.createAuditSet(auditSets, user1.getUserEmail(), tx);
//
//        //Assert actual_response
//        assertTrue(result.getStatus());
//        assertEquals("Audit Set created successfully", result.getMessage());
//        assertNotNull(result.getData());
//        assertEquals(HttpStatus.OK, result.getHttpStatus());
//
//        // Verifying repository interactions
//        verify(userRepository, times(1)).getByUserEmail(user1.getUserEmail(), tx);
//        verify(auditSetRepository, times(1)).create(any(AuditSet.class), eq(tx));
//        verify(auditSetCollaboratorsRepository, times(1)).create(any(AuditSetCollaborators.class), eq(tx));
//
//
//    }


//    @Test
//    @DisplayName("Test Delete Audit set")
//    void deleteAuditSet() throws IOException, CrudException {
//
//        // Arrange mock input data
//        String userEmail = "mayuris@perceptcs.com";
//        String auditSetId = "12334324";
//
//        List<String> role = Arrays.asList("OWNER");
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        String roleString = objectMapper1.writeValueAsString(role);
//
//        User user = User.builder()
//                .userEmail(userEmail)
//                .id(1L)
//                .password("mayu")
//                .name("mayuri sutar")
//                .roleJson(roleString)
//                .organizationName("kanzen")
//                .imageUrl("sdfddssd")
//                .build();
//
//
//        CollaboratorUser collaboratorUser = new CollaboratorUser();
//        collaboratorUser.setUserId(user.getId());
//        collaboratorUser.setUserName(user.getName());
//        collaboratorUser.setEmailId(userEmail);
//        collaboratorUser.setRole("OWNER");
//
//        Collaborator aclJson = new Collaborator();
//        aclJson.setOwnedBy(collaboratorUser);
//        aclJson.setCoOwners(Collections.emptyList());
//        aclJson.setMembers(Collections.emptyList());
//        aclJson.setReviewers(Collections.emptyList());
//
//        //convert data into json
//        ObjectMapper objectMappernew = new ObjectMapper();
//        String aclJsonInString = objectMappernew.writeValueAsString(aclJson);
//        // Set JSON AuditGroupListForAuditSet
//        List<AuditGroupListForAuditSet> groupListForAuditSets = new ArrayList<>();
//
//        AuditGroupListForAuditSet auditGroupListForAuditSet = AuditGroupListForAuditSet.builder()
//                .auditGroupId("8797898789")
//                .auditGroupName("my audit group")
//                .description("my audit group")
//                .build();
//
//
//        groupListForAuditSets.add(auditGroupListForAuditSet);
//
//
//        // Convert JSON into string AuditGroupListForAuditSet
//        String s = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
//
//        AuditSet auditSet1 = new AuditSet();
//        auditSet1.setAuditSetId(auditSetId);
//        auditSet1.setAuditSetName("AuditSet_name");
//        auditSet1.setAclJson(aclJsonInString);
//        auditSet1.setIsDeleted(false);
//        auditSet1.setAuditGroupListJson(s);
//
//
//        AuditGrpAuditSetMapping auditGrpAuditSetMapping1 = AuditGrpAuditSetMapping.builder()
//                .AuditGroupId("4334443534")
//                .AuditGroupName("audit group ")
//                .auditSetId(auditSet1.getAuditSetId())
//                .auditSetName(auditSet1.getAuditSetName())
//                .build();
//
//        List<AuditGroupListForAuditSet> groupListForAuditSet;
//        ObjectMapper objectMapper = new ObjectMapper();
//        groupListForAuditSet = objectMapper.readValue(auditSet1.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
//        });
//
//        groupListForAuditSet.clear();
//        String removedListOfAuditGroup = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
//        auditSet1.setAuditGroupListJson(removedListOfAuditGroup);
//        auditSet1.setIsDeleted(false);
//
//        // Mock repository
//        when(userRepository.getByUserEmail(userEmail, tx)).thenReturn(user);
//        when(auditSetRepository.get(auditSetId, tx)).thenReturn(auditSet1);
//        when(auditSetRepository.create(auditSet1, tx)).thenReturn(auditSet1);
//        when(auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGroupListForAuditSet.getAuditGroupId(), auditSet1.getAuditSetId(), tx)).thenReturn(auditGrpAuditSetMapping1);
//
//        // Actual response
//        ApiResponse response = auditService.deleteAuditSet(auditSet1.getAuditSetId(), userEmail, tx);
//
//        //Assert actual_response
//        assertTrue(response.getStatus());
//        assertEquals("The audit set was deleted successfully.", response.getMessage());
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//
//        // Verifying repository interactions
//        verify(userRepository, times(1)).getByUserEmail(userEmail, tx);
//        verify(auditSetRepository, times(1)).get(auditSetId, tx);
//        verify(auditSetRepository, times(1)).create(auditSet1, tx);
//
//
//    }

//    @Test
//    @DisplayName("Test Delete Audit set insufficient role")
//    void testDeleteAuditSet_InsufficientRole() throws JsonProcessingException, CrudException {
//        // Arrange mock input data
//        String userEmail = "mayuris@perceptcs.com";
//        String auditSetId = "123";
//
//        List<String> role = Arrays.asList("GENERAL_USER");
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        String roleString = objectMapper1.writeValueAsString(role);
//
//        User user = User.builder()
//                .userEmail(userEmail)
//                .id(1L)
//                .password("mayu")
//                .name("mayuri sutar")
//                .roleJson(roleString)
//                .organizationName("kanzen")
//                .imageUrl("sdfddssd")
//                .build();
//
//
//        CollaboratorUser collaboratorUser = new CollaboratorUser();
//        collaboratorUser.setUserId(user.getId());
//        collaboratorUser.setUserName(user.getName());
//        collaboratorUser.setEmailId(userEmail);
//        collaboratorUser.setRole("GENERAL_MEMBER");
//
//        Collaborator aclJson = new Collaborator();
//        aclJson.setOwnedBy(collaboratorUser);
//        aclJson.setCoOwners(Collections.emptyList());
//        aclJson.setMembers(Collections.emptyList());
//        aclJson.setReviewers(Collections.emptyList());
//
//        //convert data into json
//        ObjectMapper objectMappernew = new ObjectMapper();
//        String aclJsonInString = objectMappernew.writeValueAsString(aclJson);
//        // Set JSON AuditGroupListForAuditSet
//        List<AuditGroupListForAuditSet> groupListForAuditSets = new ArrayList<>();
//
//        AuditGroupListForAuditSet auditGroupListForAuditSet = AuditGroupListForAuditSet.builder()
//                .auditGroupId("8797898789")
//                .auditGroupName("my audit group")
//                .description("my audit group")
//                .build();
//
//
//        groupListForAuditSets.add(auditGroupListForAuditSet);
//
//
//        // Convert JSON into string AuditGroupListForAuditSet
//        String s = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
//
//        AuditSet auditSet1 = new AuditSet();
//        auditSet1.setAuditSetId(auditSetId);
//        auditSet1.setAuditSetName("AuditSet_name");
//        auditSet1.setAclJson(aclJsonInString);
//        auditSet1.setIsDeleted(false);
//        auditSet1.setAuditGroupListJson(s);
//
//
//        AuditGrpAuditSetMapping auditGrpAuditSetMapping1 = AuditGrpAuditSetMapping.builder()
//                .AuditGroupId("4334443534")
//                .AuditGroupName("audit group ")
//                .auditSetId(auditSet1.getAuditSetId())
//                .auditSetName(auditSet1.getAuditSetName())
//                .build();
//
//        List<AuditGroupListForAuditSet> groupListForAuditSet;
//        ObjectMapper objectMapper = new ObjectMapper();
//        groupListForAuditSet = objectMapper.readValue(auditSet1.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
//        });
//
//        groupListForAuditSet.clear();
//        String removedListOfAuditGroup = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
//        auditSet1.setAuditGroupListJson(removedListOfAuditGroup);
//        auditSet1.setIsDeleted(false);
//        // Mock repository
//
//        // Mock repository
//        when(userRepository.getByUserEmail(userEmail, tx)).thenReturn(user);
//        when(auditSetRepository.get(auditSetId, tx)).thenReturn(auditSet1);
//        when(auditSetRepository.create(auditSet1, tx)).thenReturn(auditSet1);
//        when(auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGroupListForAuditSet.getAuditGroupId(), auditSet1.getAuditSetId(), tx)).thenReturn(auditGrpAuditSetMapping1);
//        // Actual response
//        ApiResponse response = auditService.deleteAuditSet(auditSetId, userEmail, tx);
//
//        //Assert actual_response
//        assertFalse(response.getStatus());
//        assertEquals("User does not have the required role to delete the audit set.", response.getMessage());
//        assertEquals(HttpStatus.BAD_REQUEST, response.getHttpStatus());
//        assertNull(response.getData());
//
//        // Verifying repository interactions
//        verify(userRepository, times(1)).getByUserEmail(userEmail, tx);
//        verify(auditSetRepository, times(1)).get(auditSetId, tx);
//
//
//    }


    @Test
    @DisplayName("Test view external auditor event log")
    void viewExtAuditorEventLog() throws CrudException {
        String auditSetId = "8af4333f-d738-4298-a6a2-2d40e7db5b4f";
        Long itemId = 123L;
        String userEmail = "mayuris@perceptcs.com";
        String currentEmail = "sutar@gmail.com";


        AuditorLogs auditorLog1 = new AuditorLogs();
        auditorLog1.setUserEmail(userEmail);
        auditorLog1.setEventType("ITEM_CREATE");
        auditorLog1.setItemType("FILE");
        auditorLog1.setEventDate(System.currentTimeMillis());

        AuditorLogs auditorLog2 = new AuditorLogs();
        auditorLog2.setUserEmail(userEmail);
        auditorLog2.setEventType("ITEM_CREATE");
        auditorLog2.setItemType("FOLDER");
        auditorLog2.setEventDate(System.currentTimeMillis());

        List<AuditorLogs> auditorLogs = Arrays.asList(auditorLog1, auditorLog2);

        // Mock repository
        when(auditorLogsRepository.getExtAuditorAccessLog(eq(auditSetId), eq(itemId), any(DistributedTransaction.class))).thenReturn(auditorLogs);

        // Actual response
        ApiResponse response = auditService.viewExtAuditorEventLog(auditSetId, itemId, userEmail, tx);

        //Assert actual_response
        assertTrue(response.getStatus());
        assertEquals(true, response.getStatus());
        assertNotNull(response.getData());

        // Verify the expected behavior
        verify(auditorLogsRepository, times(1)).getExtAuditorAccessLog(auditSetId, itemId, tx);
    }

    @Test
    @DisplayName("Test get my audit set list")
    void getMyAuditSetList() throws CrudException {
        // Arrange
        String currentEmail = "mayuris@perceptcs.com";

        User user = new User();
        user.setUserEmail(currentEmail);

        // Mocking SecurityContextHolder
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("EXTERNAL_AUDITOR");
        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);

        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        //set audit set 1
        AuditSet auditSet = AuditSet.builder()
                .auditSetId("1")
                .auditSetName("AuditSet1")
                .createdAt(1234567890123884l)
                .ownerEmail(currentEmail)
                .build();

        //set audit set 2
        AuditSet auditSet1 = AuditSet.builder()
                .auditSetId("2")
                .auditSetName("AuditSet2")
                .createdAt(12345678901234l)
                .ownerEmail(currentEmail)
                .build();

        //set audit set collaborator 1
        AuditSetCollaborators auditSetCollaborators1 = new AuditSetCollaborators();
        auditSetCollaborators1.setUserEmail(currentEmail);
        auditSetCollaborators1.setAuditSetId(auditSet.getAuditSetId());
        auditSetCollaborators1.setAuditSetName(auditSet.getAuditSetName());
        auditSetCollaborators1.setAccessStatus(AccessStatus.NEWLY_ADDED.toString());
        auditSetCollaborators1.setIsFavourite(false);

        //set audit set collaborator 2
        AuditSetCollaborators auditSetCollaborators2 = new AuditSetCollaborators();
        auditSetCollaborators2.setUserEmail(currentEmail);
        auditSetCollaborators2.setAuditSetId(auditSet1.getAuditSetId());
        auditSetCollaborators2.setAuditSetName(auditSet1.getAuditSetName());
        auditSetCollaborators2.setAccessStatus(AccessStatus.NEWLY_ADDED.toString());
        auditSetCollaborators2.setIsFavourite(false);


        List<AuditSetCollaborators> auditSetCollaboratorList = Arrays.asList(auditSetCollaborators1, auditSetCollaborators2);

        //set user audit group
        UserAuditGroup userAuditGroup = UserAuditGroup.
                builder()
                .userEmail(currentEmail)
                .auditGroupId(auditSet.getAuditSetId())
                .auditGroupName(auditSet.getAuditSetName())
                .build();

        //set user audit group 1
        UserAuditGroup userAuditGroup1 = UserAuditGroup.
                builder()
                .userEmail(currentEmail)
                .auditGroupId(auditSet1.getAuditSetId())
                .auditGroupName(auditSet1.getAuditSetName())
                .privilege("MEMBER")
                .build();

        List<UserAuditGroup> userAuditList = new ArrayList<>();
        userAuditList.add(userAuditGroup);
        userAuditList.add(userAuditGroup1);

        //set audit group audit set mapping
        AuditGrpAuditSetMapping auditGrpAuditSetMapping = AuditGrpAuditSetMapping
                .builder()
                .AuditGroupId(userAuditGroup.getAuditGroupId())
                .AuditGroupName(userAuditGroup.getAuditGroupName())
                .auditSetId(auditSetCollaborators1.getAuditSetId())
                .auditSetName(auditSetCollaborators1.getAuditSetName())
                .build();

        //set audit group audit set mapping 1
        AuditGrpAuditSetMapping auditGrpAuditSetMapping1 = AuditGrpAuditSetMapping
                .builder()
                .AuditGroupId(userAuditGroup1.getAuditGroupId())
                .AuditGroupName(userAuditGroup1.getAuditGroupName())
                .auditSetId(auditSetCollaborators2.getAuditSetId())
                .auditSetName(auditSetCollaborators2.getAuditSetName())
                .build();


        List<AuditGrpAuditSetMapping> auditGrpAuditSetMappingList = new ArrayList<>();
        auditGrpAuditSetMappingList.add(auditGrpAuditSetMapping);
        auditGrpAuditSetMappingList.add(auditGrpAuditSetMapping1);


        //set audit set list from collaborator
        List<AuditSetLists> auditSetListss = new ArrayList<>();
        AuditSetLists auditSetList11 = new AuditSetLists();
        auditSetList11.setAuditSetId(auditSetCollaborators2.getAuditSetId());
        auditSetList11.setAuditSetName(auditSetCollaborators2.getAuditSetName());
        auditSetList11.setDescription(auditSet.getDescription());
        auditSetList11.setOwnedBy(auditSet.getOwnerEmail());
        auditSetList11.setCreatedAt(auditSet.getCreatedAt());
        auditSetList11.setAccessStatus(auditSetCollaborators2.getAccessStatus());
        auditSetList11.setIsFavourite(auditSetCollaborators2.getIsFavourite());


        AuditSetLists auditSetList22 = new AuditSetLists();
        auditSetList22.setAuditSetId(auditSetCollaborators2.getAuditSetId());
        auditSetList22.setAuditSetName(auditSetCollaborators2.getAuditSetName());
        auditSetList22.setDescription(auditSet.getDescription());
        auditSetList22.setOwnedBy(auditSet.getOwnerEmail());
        auditSetList22.setCreatedAt(auditSet.getCreatedAt());
        auditSetList22.setAccessStatus(auditSetCollaborators2.getAccessStatus());
        auditSetList22.setIsFavourite(auditSetCollaborators2.getIsFavourite());

        auditSetListss.add(auditSetList11);
        auditSetListss.add(auditSetList22);


        //set audit set list from mapping
        List<AuditSetLists> auditSetLists = new ArrayList<>();
        AuditSetLists auditSetList1 = new AuditSetLists();
        auditSetList1.setAuditSetId(auditGrpAuditSetMapping.getAuditSetId());
        auditSetList1.setAuditSetName(auditGrpAuditSetMapping.getAuditSetName());
        auditSetList1.setDescription(auditSet.getDescription());
        auditSetList1.setOwnedBy(auditSet.getOwnerEmail());
        auditSetList1.setCreatedAt(auditSet.getCreatedAt());
        auditSetList1.setAccessStatus(auditSetCollaborators1.getAccessStatus());
        auditSetList1.setIsFavourite(auditSetCollaborators1.getIsFavourite());


        AuditSetLists auditSetList2 = new AuditSetLists();
        auditSetList2.setAuditSetId(auditGrpAuditSetMapping1.getAuditSetId());
        auditSetList2.setAuditSetName(auditGrpAuditSetMapping1.getAuditSetName());
        auditSetList2.setDescription(auditSet.getDescription());
        auditSetList2.setOwnedBy(auditSet.getOwnerEmail());
        auditSetList2.setCreatedAt(auditSet.getCreatedAt());
        auditSetList2.setAccessStatus(auditSetCollaborators1.getAccessStatus());
        auditSetList2.setIsFavourite(auditSetCollaborators1.getIsFavourite());

        auditSetLists.add(auditSetList1);
        auditSetLists.add(auditSetList2);


        // Mock repository
        when(userRepository.getByUserEmail(user.getUserEmail(), tx)).thenReturn(user);
        when(auditSetRepository.get(auditGrpAuditSetMapping1.getAuditSetId(), tx)).thenReturn(auditSet);
        when(auditSetRepository.get(auditSetCollaborators1.getAuditSetId(), tx)).thenReturn(auditSet);
        when(auditSetRepository.get(auditGrpAuditSetMapping1.getAuditSetId(), tx)).thenReturn(auditSet);

        when(userAuditGroupRepository.getUserGroupList(userAuditGroup.getUserEmail(), tx)).thenReturn(userAuditList);
        when(auditSetCollaboratorsRepository.getAuditSetCollaboratorList(currentEmail, tx)).thenReturn(auditSetCollaboratorList);
        when(auditGrpAuditSetMappingRepository.getUserGrpAuditSetList(auditGrpAuditSetMapping.getAuditGroupId(), tx)).thenReturn(auditGrpAuditSetMappingList);
        when(auditGrpAuditSetMappingRepository.getUserGrpAuditSetList(auditGrpAuditSetMapping1.getAuditGroupId(), tx)).thenReturn(auditGrpAuditSetMappingList);
        when(auditSetCollaboratorsRepository.getAuditSetCollaborator(currentEmail, auditGrpAuditSetMapping.getAuditSetId(), tx)).thenReturn(auditSetCollaborators1);
        when(auditSetCollaboratorsRepository.getAuditSetCollaborator(currentEmail, auditGrpAuditSetMapping1.getAuditSetId(), tx)).thenReturn(auditSetCollaborators2);

        // Actual response
        ApiResponse response = auditService.getMyAuditSetList(currentEmail, tx);

        // Assert
        assertTrue(response.getStatus());
        assertEquals(true, response.getStatus());
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getData());
        // Verify the expected behavior
        verify(userRepository, times(1)).getByUserEmail(user.getUserEmail(), tx);
        verify(userAuditGroupRepository, times(1)).getUserGroupList(userAuditGroup.getUserEmail(), tx);
        verify(auditSetCollaboratorsRepository, times(1)).getAuditSetCollaboratorList(currentEmail, tx);
        verify(auditGrpAuditSetMappingRepository, times(1)).getUserGrpAuditSetList(auditGrpAuditSetMapping.getAuditGroupId(), tx);


    }


    @Test
    @DisplayName("Test update audit set information")
    void updateAuditSetInfo() throws CrudException, JsonProcessingException {
//        // Arrange
//        String currentEmail = "mayuris@perceptcs.com";
//
//        User user = new User();
//        user.setUserEmail(currentEmail);
//
//        // Mocking SecurityContextHolder
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
//        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
//
//        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        // Inputs mock
//        String auditSetId = "548940954540-900-095443";
//        UpdateAuditSetInfo updateAuditSetInfo = UpdateAuditSetInfo.builder()
//                .auditSetName("audit set new")
//                .description("audit set new")
//                .build();
//        // Set JSON AuditGroupListForAuditSet
//        List<AuditGroupListForAuditSet> groupListForAuditSets = new ArrayList<>();
//
//        AuditGroupListForAuditSet auditGroupListForAuditSet = AuditGroupListForAuditSet.builder()
//                .auditGroupId("8797898789")
//                .auditGroupName("my audit group")
//                .description("my audit group")
//                .build();
//
//
//        groupListForAuditSets.add(auditGroupListForAuditSet);
//
//
//        // Convert JSON into string AuditGroupListForAuditSet
//        String s = GenericUtility.convertObjectToJson(groupListForAuditSets);
//
//
//        // Mock audit set list
//        List<AuditSet> auditSetList = new ArrayList<>();
//        AuditSet auditSet = AuditSet.builder()
//                .auditSetId(auditSetId)
//                .auditSetName(" my audit set 1")
//                .description("my audit set 1")
//                .auditGroupListJson(s)
//                .build();
//
//        AuditSet auditSet1 = AuditSet.builder()
//                .auditSetId(auditSetId)
//                .auditSetName(" my audit set 12")
//                .description("my audit set 12")
//                .auditGroupListJson(s)
//                .build();
//
//        auditSetList.add(auditSet);
//        auditSetList.add(auditSet1);
//
//        // Mock audit set
//        AuditSet auditSetss = AuditSet.builder()
//                .auditSetId(auditSet.getAuditSetId())
//                .auditSetName(updateAuditSetInfo.getAuditSetName())
//                .description(updateAuditSetInfo.getDescription())
//                .auditGroupListJson(s)
//                .build();
//
//        List<AuditGroupListForAuditSet> groupList;
//
//        ObjectMapper objectMappernew = new ObjectMapper();
//        groupList = objectMappernew.readValue(auditSetss.getAuditGroupListJson(), new TypeReference<List<AuditGroupListForAuditSet>>() {
//        });
//
//        AuditGrpAuditSetMapping auditGrpAuditSetMapping1 = AuditGrpAuditSetMapping.builder()
//                .AuditGroupId(auditGroupListForAuditSet.getAuditGroupId())
//                .AuditGroupName(auditGroupListForAuditSet.getAuditGroupName())
//                .auditSetId(auditSetss.getAuditSetId())
//                .auditSetName("audit set new")
//                .build();
//
//
//        when(userRepository.getByUserEmail(currentEmail, tx)).thenReturn(user);
//        when(auditSetRepository.getAuditSetList(tx)).thenReturn(auditSetList);
//        when(auditSetRepository.get(auditSetId, tx)).thenReturn(auditSetss);
//        when(auditSetRepository.create(auditSetss, tx)).thenReturn(auditSetss);
//        when(auditGrpAuditSetMappingRepository.getAuditGroupAndAuditSetMapping(auditGrpAuditSetMapping1.getAuditGroupId(), auditSetss.getAuditSetId(), tx)).thenReturn(auditGrpAuditSetMapping1);
//        when(auditGrpAuditSetMappingRepository.create(auditGrpAuditSetMapping1, tx)).thenReturn(auditGrpAuditSetMapping1);
//
//        // Act
//        ApiResponse response = auditService.updateAuditSetInfo(auditSetId, updateAuditSetInfo, currentEmail, tx);
//
//        // Assert
//        // assertTrue(response.getStatus());
//        assertTrue(response.getStatus());
//        assertEquals("Audit set updated successfully", response.getMessage());
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//        assertNull(response.getData());
//
//        // Verify
//        verify(userRepository, times(1)).getByUserEmail(user.getUserEmail(), tx);
//        verify(auditSetRepository, times(1)).getAuditSetList(tx);
//        verify(auditSetRepository, times(1)).get(auditSetId, tx);
//        verify(auditSetRepository, times(1)).create(auditSetss, tx);
//        verify(auditGrpAuditSetMappingRepository, times(1)).getAuditGroupAndAuditSetMapping(
//                auditGrpAuditSetMapping1.getAuditGroupId(), auditSetss.getAuditSetId(), tx);
//        verify(auditGrpAuditSetMappingRepository, times(1)).create(auditGrpAuditSetMapping1, tx);

    }
}
