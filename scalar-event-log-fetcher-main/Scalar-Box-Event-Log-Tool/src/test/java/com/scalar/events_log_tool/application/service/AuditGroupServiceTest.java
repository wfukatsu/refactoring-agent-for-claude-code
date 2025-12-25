package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.AuditGroupPrivileges;
import com.scalar.events_log_tool.application.dto.GroupUserPrivileges;
import com.scalar.events_log_tool.application.model.AuditGroup;
import com.scalar.events_log_tool.application.model.AuditGrpAuditSetMapping;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.model.UserAuditGroup;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.responsedto.AuditGroupInfo;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuditGroupServiceTest {
    @MockBean
    DistributedTransactionManager manager;
    @MockBean
    DistributedTransaction tx;
    @MockBean
    private AuditGroupRepository auditGroupRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private AuditGroupService auditGroupService;
    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;
    @MockBean
    private UserAuditGroupRepository userAuditGroupRepository;
    @MockBean
    private AuditSetRepository auditSetRepository;
    @MockBean
    private AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository;

    @BeforeEach
    private void setUp() throws TransactionException {
        when(manager.start()).thenReturn(tx);
    }


    @Test
    @DisplayName("Test create audit group ")
    void createAuditGroup() throws JsonProcessingException, TransactionException {
//        // Mock input data
//        CreateAuditGroup createAuditGroup = new CreateAuditGroup();
//        createAuditGroup.setAuditGroupName("Audit Group set");
//        createAuditGroup.setDescription("Test Descript");
//
//        CreateAuditGroup createAuditGroup1 = new CreateAuditGroup();
//        createAuditGroup1.setAuditGroupName("Audit Group setsss");
//        createAuditGroup1.setDescription("Test Descriptionss");
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
//                .id(2L)
//                .name("mayuri sutar")
//                .password("mayu")
//                .imageUrl("image_url")
//                .organizationName("kanzen")
//                .roleJson(roleString)
//                .build();
//
//        //set user privileges
//        GroupUserPrivileges groupUserPrivileges = GroupUserPrivileges.builder()
//                .userEmail(user1.getUserEmail())
//                .userName(user1.getName())
//                .privileges(AuditGroupPrivileges.OWNER.toString())
//                .build();
//
//
//        List<GroupUserPrivileges> userPrivilegesList = Arrays.asList(groupUserPrivileges);
//        //convert object to json using generic method
//        String members = GenericUtility.convertObjectToStringJson(userPrivilegesList);
//
//        // Create a SimpleDateFormat object for UTC with the format
//        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
//        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        // Format the date in UTC
//        String formattedUtcDate = utcFormat.format(new Date());
//        String[] split = formattedUtcDate.split("-");
//        String withoutMillis = split[1].substring(0, 6);
//
//
//        //set data in audit group table
//        AuditGroup auditGroup = AuditGroup.builder()
//                .userEmail(user1.getUserEmail())
//                .auditGroupId("8af4333f-d738-4298-a6a2-2d40e7d4555")
//                .auditGroupName("audit")
//                .description("new")
//                .ownerId(user1.getId())
//                .ownerName(user1.getName())
//                .memberListJson(members)
//                .createdAt(Long.parseLong((split[0] + withoutMillis)))
//                .isDeleted(false)
//                .build();
//
//
//        AuditGroup auditGroup1 = AuditGroup.builder()
//                .userEmail(user1.getUserEmail())
//                .auditGroupId("8af4333f-d738-4298-a6a2-2d40e7d4778")
//                .auditGroupName(createAuditGroup.getAuditGroupName())
//                .description(createAuditGroup.getDescription())
//                .ownerId(user1.getId())
//                .ownerName(user1.getName())
//                .memberListJson(members)
//                .createdAt(Long.parseLong((split[0] + withoutMillis)))
//                .isDeleted(false)
//                .build();
//
//        List<AuditGroup> auditGroupList = new ArrayList<>();
//        auditGroupList.add(auditGroup);
//        auditGroupList.add(auditGroup1);
//
//
//        AuditGroupResponse auditGroupResponse = AuditGroupResponse.
//                builder()
//                .auditGroupId(auditGroup1.getAuditGroupId())
//                .auditSetName(createAuditGroup1.getAuditGroupName())
//                .description(createAuditGroup1.getDescription())
//                .build();
//        // Mock repository
//        when(auditGroupRepository.getgroupList((tx))).thenReturn(auditGroupList);
//        when(userRepository.getByUserEmail(user1.getUserEmail(), tx)).thenReturn(user1);
//        when(auditGroupRepository.create(eq(auditGroup), eq(tx))).thenReturn(auditGroup);
//
//        // Actual response
//        ApiResponse result = auditGroupService.createAuditGroup(createAuditGroup1, user1.getUserEmail(), tx);
//
//        //Assert actual_response
//        assertTrue(result.getStatus());
//        assertEquals(HttpStatus.OK, result.getHttpStatus());
//
//
//        // Verifying repository interactions
//        verify(auditGroupRepository, times(1)).getgroupList(tx);
//        verify(userRepository).getByUserEmail(user1.getUserEmail(), tx);
//        verify(auditGroupRepository, times(1)).create(any(AuditGroup.class), eq(tx));
    }


    @Test
    @DisplayName("Test get list of audit group")
    void getListOfAuditGroup() throws JsonProcessingException, CrudException {

        // Mock data
        List<String> role = Arrays.asList("AUDIT_ADMIN");
        ObjectMapper objectMapper1 = new ObjectMapper();
        String roleString = objectMapper1.writeValueAsString(role);

        //user 1
        User user1 = User.builder()
                .userEmail("mayuris@perceptcs.com")
                .id(2L)
                .name("mayuri sutar")
                .password("mayu")
                .imageUrl("image_url")
                .organizationName("kanzen")
                .roleJson(roleString)
                .build();


        // Mocking SecurityContextHolder
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);

        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        //set user privileges
        GroupUserPrivileges groupUserPrivileges = GroupUserPrivileges.builder()
                .userEmail(user1.getUserEmail())
                .userName(user1.getName())
                .privileges(AuditGroupPrivileges.OWNER.toString())
                .build();


        List<GroupUserPrivileges> userPrivilegesList = Arrays.asList(groupUserPrivileges);

        //convert object to json using generic method
        String members = GenericUtility.convertObjectToStringJson(userPrivilegesList);

        //set data in audit group 1
        AuditGroup auditGroup = AuditGroup.builder()
                .userEmail(user1.getUserEmail())
                .auditGroupId("8af4333f-d738-4298-a6a2-2d40e7d4555")
                .auditGroupName("audit")
                .description("new")
                .ownerId(user1.getId())
                .ownerName(user1.getName())
                .memberListJson(members)
                .isDeleted(false)
                .build();

        //set user privileges
        GroupUserPrivileges groupUserPrivileges1 = GroupUserPrivileges.builder()
                .userEmail(user1.getUserEmail())
                .userName(user1.getName())
                .privileges(AuditGroupPrivileges.MEMBER.toString())
                .build();


        List<GroupUserPrivileges> userPrivilegesList1 = Arrays.asList(groupUserPrivileges1);

        //convert object to json using generic method
        String members1 = GenericUtility.convertObjectToStringJson(userPrivilegesList1);

        //set data in audit group 2
        AuditGroup auditGroup1 = AuditGroup.builder()
                .userEmail(user1.getUserEmail())
                .auditGroupId("8af4333f-d738-4298-a6a2-2d40e7d4555")
                .auditGroupName("audit")
                .description("new")
                .ownerId(2l)
                .ownerName("new user for audit group")
                .memberListJson(members1)
                .isDeleted(false)
                .build();

        List<AuditGroup> auditGroupList = new ArrayList<>();
        auditGroupList.add(auditGroup);
        auditGroupList.add(auditGroup1);

        // Mock repository
        when(userRepository.getByUserEmail(user1.getUserEmail(), tx)).thenReturn(user1);
        when(auditGroupRepository.getgroupList(tx)).thenReturn(auditGroupList);


        // Mock ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        List<GroupUserPrivileges> memberList;
        try {
            // Convert JSON string to a list of AuditGroupUser
            memberList = objectMapper.readValue(auditGroup.getMemberListJson(), new TypeReference<List<GroupUserPrivileges>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Json to AuditGroupUser list", e);
        }

        memberList.size();

        List<AuditGroupInfo> auditGroupLists = new ArrayList<>();
        //audit group 1
        AuditGroupInfo auditGroupList1 = AuditGroupInfo.builder()
                .auditGroupId(auditGroup.getAuditGroupId())
                .auditGroupName(auditGroup.getAuditGroupName())
                .description(auditGroup.getDescription())
                .memberCount(memberList.size())
                .createdAt(auditGroup.getCreatedAt())
                .ownedBy(auditGroup.getUserEmail())
                .build();


        List<GroupUserPrivileges> memberList1;
        try {
            // Convert JSON string to a list of AuditGroupUser
            memberList = objectMapper.readValue(auditGroup1.getMemberListJson(), new TypeReference<List<GroupUserPrivileges>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting Json to AuditGroupUser list", e);
        }


        //audit group 1
        AuditGroupInfo auditGroupList2 = AuditGroupInfo.builder()
                .auditGroupId(auditGroup1.getAuditGroupId())
                .auditGroupName(auditGroup1.getAuditGroupName())
                .description(auditGroup1.getDescription())
                .memberCount(memberList.size())
                .createdAt(auditGroup1.getCreatedAt())
                .ownedBy(auditGroup1.getUserEmail())
                .build();


        auditGroupLists.add(auditGroupList1);
        auditGroupLists.add(auditGroupList2);

        // Call the method to be tested
        ApiResponse result = auditGroupService.getListOfAuditGroup(user1.getUserEmail(), tx);


        // Assertions
        assertTrue(result.getStatus());
        assertEquals(HttpStatus.OK, result.getHttpStatus());

        // Verify the expected behavior
        verify(userRepository, times(1)).getByUserEmail(user1.getUserEmail(), tx);
        verify(auditGroupRepository, times(1)).getgroupList(tx);


    }

    @Test
    @DisplayName("Test get audit group information")
    void testGetAuditGroupInfo() throws CrudException, JsonProcessingException {
//        // Mock data
//        String auditGroupId = "8af4333f-d738-4298-a6a2-2d40e7d4555";
//        String currentEmail = "mayus@perceptcs.com";
//
//        List<String> role = Arrays.asList("AUDIT_ADMIN");
//        ObjectMapper objectMapper1 = new ObjectMapper();
//        String roleString = objectMapper1.writeValueAsString(role);
//
//        //user 1
//        User user1 = User.builder()
//                .userEmail(currentEmail)
//                .id(2L)
//                .name("mayuri sutar")
//                .password("mayu")
//                .imageUrl("image_url")
//                .organizationName("kanzen")
//                .roleJson(roleString)
//                .build();
//
//        // Mocking SecurityContextHolder
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
//        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
//
//        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        //set user privileges
//        GroupUserPrivileges groupUserPrivileges = GroupUserPrivileges.builder()
//                .userEmail(user1.getUserEmail())
//                .userName(user1.getName())
//                .privileges(AuditGroupPrivileges.OWNER.toString())
//                .build();
//
//
//        List<GroupUserPrivileges> userPrivilegesList = Arrays.asList(groupUserPrivileges);
//
//        //convert object to json using generic method
//        String members = GenericUtility.convertObjectToStringJson(userPrivilegesList);
//
//        //set data in audit group 1
//        AuditGroup auditGroup = AuditGroup.builder()
//                .userEmail(user1.getUserEmail())
//                .auditGroupId("8af4333f-d738-4298-a6a2-2d40e7d4555")
//                .auditGroupName("audit")
//                .description("new")
//                .ownerId(user1.getId())
//                .ownerName(user1.getName())
//                .memberListJson(members)
//                .isDeleted(false)
//                .build();
//
//
//        List<GroupUserPrivileges> memberList = new ArrayList<>();
//        GroupUserPrivileges ownerMember = new GroupUserPrivileges();
//        ownerMember.setUserEmail("sample@example.com");
//        ownerMember.setPrivileges(CollaboratorUserRoles.OWNER.toString());
//        memberList.add(ownerMember);
//
//        // Mock repository
//        //  when(userRepository.getByUserEmail(user1.getUserEmail(), tx)).thenReturn(user1);
//        when(auditGroupRepository.getAuditGroup(auditGroup.getAuditGroupId(), tx)).thenReturn(auditGroup);
//
//        // Actual response
//        ApiResponse response = auditGroupService.getAuditGroupInfo(auditGroupId, currentEmail, tx);
//
//        //Assert actual_response
//        assertTrue(response.getStatus());
//        assertEquals(true, response.getStatus());
//
//        // Verifying repository interactions
//        verify(auditGroupRepository, times(1)).getAuditGroup(auditGroup.getAuditGroupId(), tx);


    }


    @Test
    @DisplayName("Test updateAuditGroup")
    void updateAuditGroup() throws CrudException {
//        // Arrange
//        String currentEmail = "mayuris@perceptcs.com";
//        // Set up test data
//        String auditGroupId = "123";
//        UpdateAuditGroup updateAuditGroup = new UpdateAuditGroup();
//        updateAuditGroup.setAuditGroupName("my audit group");
//        updateAuditGroup.setDescription("my audit group with audit set");
//
//
//        User user = new User();
//        user.setUserEmail(currentEmail);
//        // Mocking SecurityContextHolder
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
//        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
//
//        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        // Mock auditGroupRepository
//        AuditGroup auditGroup = new AuditGroup();
//        auditGroup.setAuditGroupId("123");
//        auditGroup.setAuditGroupName("audit group for testing");
//        auditGroup.setDescription("audit group for testing");
//        auditGroup.setIsDeleted(false);
//
//// Mock AuditGrpAuditSetMapping
//        List<AuditGrpAuditSetMapping> auditGrpAuditSetMappingList = new ArrayList<>();
//
//        AuditGrpAuditSetMapping auditGrpAuditSetMapping1 = AuditGrpAuditSetMapping.builder()
//                .AuditGroupId(auditGroup.getAuditGroupId())
//                .AuditGroupName(auditGroup.getAuditGroupName())
//                .auditSetId("324343355")
//                .auditSetName("audit set new for group")
//                .build();
//        auditGrpAuditSetMappingList.add(auditGrpAuditSetMapping1);
//
//
//        AuditGrpAuditSetMapping auditGrpAuditSetMapping = AuditGrpAuditSetMapping.builder()
//                .AuditGroupId(auditGroup.getAuditGroupId())
//                .AuditGroupName(updateAuditGroup.getAuditGroupName())
//                .auditSetId("324343355")
//                .auditSetName("audit set new for group")
//                .build();
//
//
//        //set json AuditGroupListForAuditSet
//        List<AuditGroupListForAuditSet> groupListForAuditSets = new ArrayList<>();
//
//        AuditGroupListForAuditSet auditGroupListForAuditSet = AuditGroupListForAuditSet.builder()
//                .auditGroupId(auditGroup.getAuditGroupId())
//                .auditGroupName(auditGroup.getAuditGroupName())
//                .description(auditGroup.getDescription())
//                .build();
//        groupListForAuditSets.add(auditGroupListForAuditSet);
//
//
//        //convert  json into string AuditGroupListForAuditSet
//        String s = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
//
//        //mock audit set
//        AuditSet auditSet = AuditSet.builder()
//                .auditSetId(auditGrpAuditSetMapping.getAuditSetId())
//                .auditSetName(auditGrpAuditSetMapping.getAuditSetName())
//                .auditGroupListJson(s)
//                .build();
//
//        //set json AuditGroupListForAuditSet
//        List<AuditGroupListForAuditSet> groupListForAuditSetss = new ArrayList<>();
//        AuditGroupListForAuditSet auditGroupListForAuditSets1 = AuditGroupListForAuditSet.builder()
//                .auditGroupId(auditGroup.getAuditGroupId())
//                .auditGroupName(updateAuditGroup.getAuditGroupName())
//                .description(updateAuditGroup.getDescription())
//                .build();
//
//        groupListForAuditSetss.add(auditGroupListForAuditSets1);
//
//        //convert  json into string AuditGroupListForAuditSet
//        String s1 = GenericUtility.convertObjectToStringJson(groupListForAuditSets);
//
//        //set json AuditGroupListForAuditSet
//        AuditSet auditSet1 = AuditSet.builder()
//                .auditSetId(auditGrpAuditSetMapping.getAuditSetId())
//                .auditSetName(auditGrpAuditSetMapping.getAuditSetName())
//                .auditGroupListJson(s1)
//                .build();
//
//        when(userRepository.getByUserEmail(user.getUserEmail(), tx)).thenReturn(user);
//        when(auditGroupRepository.getAuditGroup(eq(auditGroupId), eq(tx))).thenReturn(auditGroup);
//        when(auditGroupRepository.create(eq(auditGroup), eq(tx))).thenReturn(auditGroup);
//        when(auditGrpAuditSetMappingRepository.getUserGrpAuditSetList(auditGroup.getAuditGroupId(), tx)).thenReturn(auditGrpAuditSetMappingList);
//        when(auditGrpAuditSetMappingRepository.create(eq(auditGrpAuditSetMapping), eq(tx))).thenReturn(auditGrpAuditSetMapping);
//
//        when(auditSetRepository.get(eq(auditGrpAuditSetMapping.getAuditSetId()), eq(tx))).thenReturn(auditSet1);
//        when(auditSetRepository.create(eq(auditSet1), eq(tx))).thenReturn(auditSet1);
//        // Mock other repository methods as needed
//
//        // Call the method to test
//        ApiResponse response = auditGroupService.updateAuditGroup(auditGroupId, updateAuditGroup, user.getUserEmail(), tx);
//
//        // Assertions
//        assertTrue(response.getStatus());
//        assertEquals(true, response.getStatus());
//        assertEquals("Audit group updated successfully", response.getMessage());
//
//
//        // Verify the expected behavior
//        verify(userRepository, times(1)).getByUserEmail(eq(currentEmail), eq(tx));
//        verify(auditGroupRepository, times(1)).getAuditGroup(eq(auditGroupId), eq(tx));
//        verify(auditGroupRepository, times(1)).create(eq(auditGroup), eq(tx));
//        verify(auditGrpAuditSetMappingRepository, times(1)).getUserGrpAuditSetList(eq(auditGroup.getAuditGroupId()), eq(tx));
//        verify(auditGrpAuditSetMappingRepository, times(1)).create(eq(auditGrpAuditSetMapping), eq(tx));
//        verify(auditSetRepository, times(1)).get(eq(auditGrpAuditSetMapping.getAuditSetId()), eq(tx));
//        verify(auditSetRepository, times(1)).create(eq(auditSet1), eq(tx));

    }

    @Test
    void deleteAuditGroup() throws JsonProcessingException, CrudException {
        // Mocking auditGroupRepository.getAuditGroup()
        String currentEmail = "mayuris@perceptcs.com";
        String auditGroupId = "1234456";
        //set role for current user
        List<String> role = Arrays.asList("AUDIT_ADMIN");
        ObjectMapper objectMapper1 = new ObjectMapper();
        String roleString = objectMapper1.writeValueAsString(role);

        // Mocking SecurityContextHolder
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        //set user
        User user = new User();
        user.setUserEmail(currentEmail);
        user.setName("mayuri sutar");
        user.setRoleJson(roleString);

        ObjectMapper objectMapper = new ObjectMapper();
        List<GroupUserPrivileges> memberlist = new ArrayList<>();
        GroupUserPrivileges groupUserPrivileges = new GroupUserPrivileges();
        groupUserPrivileges.setUserEmail("olduser@gmail.com");
        groupUserPrivileges.setUserName("old user");
        groupUserPrivileges.setPrivileges("MEMBER");

        GroupUserPrivileges groupUserPrivileges1 = new GroupUserPrivileges();
        groupUserPrivileges1.setUserEmail("olduser1@gmail.com");
        groupUserPrivileges1.setUserName("old user1");
        groupUserPrivileges1.setPrivileges("MEMBER");

        memberlist.add(groupUserPrivileges);
        memberlist.add(groupUserPrivileges1);

        String members = GenericUtility.convertObjectToStringJson(memberlist);


        AuditGroup auditGroup = new AuditGroup();
        auditGroup.setAuditGroupId(auditGroupId);
        auditGroup.setAuditGroupName("audit group old");
        auditGroup.setDescription("audit group old");
        auditGroup.setMemberListJson(members);
        auditGroup.setIsDeleted(true);

        List<GroupUserPrivileges> memberEmails = null;
        // Convert JSON string to List<String>

        memberEmails = objectMapper.readValue(auditGroup.getMemberListJson(), new TypeReference<List<GroupUserPrivileges>>() {
        });


        UserAuditGroup userAuditGroup = new UserAuditGroup();
        userAuditGroup.setAuditGroupId(auditGroup.getAuditGroupId());
        userAuditGroup.setAuditGroupName(auditGroup.getAuditGroupName());
        userAuditGroup.setUserEmail(groupUserPrivileges.getUserEmail());
        userAuditGroup.setPrivilege("MEMBER");

        List<AuditGrpAuditSetMapping> allAuditSetByGroupId = new ArrayList<>();

        AuditGrpAuditSetMapping auditGrpAuditSetMapping = new AuditGrpAuditSetMapping();
        auditGrpAuditSetMapping.setAuditSetId("12345677");
        auditGrpAuditSetMapping.setAuditSetName("audit set");
        auditGrpAuditSetMapping.setAuditGroupId(userAuditGroup.getAuditGroupId());
        auditGrpAuditSetMapping.setAuditGroupName(userAuditGroup.getAuditGroupName());

        allAuditSetByGroupId.add(auditGrpAuditSetMapping);

        //mock repository
        when(auditGroupRepository.getAuditGroup(auditGroup.getAuditGroupId(), tx)).thenReturn(auditGroup);
        when(userAuditGroupRepository.get(userAuditGroup.getAuditGroupId(), userAuditGroup.getUserEmail(), tx)).thenReturn(new UserAuditGroup());
        when(auditGrpAuditSetMappingRepository.getAllAuditSetByGroupId(auditGrpAuditSetMapping.getAuditGroupId(), tx)).thenReturn(allAuditSetByGroupId);
        when(auditGroupRepository.create(auditGroup, tx)).thenReturn(new AuditGroup());


        ApiResponse apiResponse = auditGroupService.deleteAuditGroup(auditGroupId, tx);

        //assert equals
        assertEquals(true, apiResponse.getStatus());
        assertEquals("Audit group deleted successfully.", apiResponse.getMessage());
        assertEquals(HttpStatus.OK, apiResponse.getHttpStatus());

        // Verify repository
        verify(auditGroupRepository, times(1)).create(auditGroup, tx);
        verify(userAuditGroupRepository, times(1)).get(userAuditGroup.getAuditGroupId(), userAuditGroup.getUserEmail(), tx);
        verify(auditGrpAuditSetMappingRepository, times(1)).getAllAuditSetByGroupId(auditGrpAuditSetMapping.getAuditGroupId(), tx);
        verify(auditGroupRepository, times(1)).create(auditGroup, tx);
    }
}