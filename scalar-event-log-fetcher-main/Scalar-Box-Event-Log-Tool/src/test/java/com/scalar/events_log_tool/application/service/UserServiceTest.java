package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.LanguageSupported;
import com.scalar.events_log_tool.application.dto.UserDto;
import com.scalar.events_log_tool.application.model.RoleUser;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @MockBean
    DistributedTransactionManager manager;

    @MockBean
    DistributedTransaction tx;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleUserRepository roleUserRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;
    @MockBean
    private ObjectMapper objectMapper;
    @MockBean
    private AuditorLogsRepository auditorLogsRepository;
    @MockBean
    private UserAuditGroupRepository userAuditGroupRepository;
    @MockBean
    private AuditGrpAuditSetMappingRepository auditGrpAuditSetMappingRepository;
    @MockBean
    private AuditSetRepository auditSetRepository;
    @MockBean
    private AuditGroupRepository auditGroupRepository;
    @MockBean
    private AuditSetCollaboratorsRepository auditSetCollaboratorsRepository;

    @BeforeEach
    private void setUp() throws TransactionException {
        Mockito.when(manager.start()).thenReturn(tx);
    }

    @Test
    @DisplayName("Test Create User")
    void createUser() throws IOException {

        String currentUser = "currentUser@gmail.com";
        User u = new User();
        u.setUserEmail(currentUser);
        // Arrange mock input data
        List<String> role = Arrays.asList("AUDIT_ADMIN");
        ObjectMapper objectMapper1 = new ObjectMapper();
        String roleString = objectMapper1.writeValueAsString(role);

        // Mocking SecurityContextHolder
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);

        Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        UserDto userDto = new UserDto();
        userDto.setUserEmail("mayuris@perceptcs.com");
        userDto.setName("mayuri sutar");
        userDto.setPassword("mayu");
        userDto.setOrganizationName("kanzen");
        userDto.setRole("AUDIT_ADMIN");


        User user1 = User.builder()
                .userEmail(userDto.getUserEmail())
                .id(null)
                .name(userDto.getName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .organizationName(userDto.getOrganizationName())
                .imageUrl("https://sample-test2-images.s3.ap-south-1.amazonaws.com/user.png")
                .roleJson(roleString)
                .isDeleted(false)
                .isBoxAdmin(false)
                .languageCode(LanguageSupported.English.getCode())
                .build();


        RoleUser roleUser = RoleUser.builder()
                .userId(user1.getId())
                .roleName("AUDIT_ADMIN")
                .userEmail(user1.getUserEmail())
                .userName(user1.getName())
                .build();

        // Mock repository
        Mockito.when(userRepository.getByUserEmail((currentUser), (tx))).thenReturn(u);
        Mockito.when(roleUserRepository.create(roleUser, tx)).thenReturn(roleUser);
        Mockito.when(userRepository.create(user1, tx)).thenReturn(user1);
        // Actual response
        ApiResponse response = userService.createUser(userDto, tx);

        //Assert actual_response
        assertTrue(response.getStatus());
        assertEquals("User is created successfully.", response.getMessage());
        assertEquals(HttpStatus.OK, response.getHttpStatus());


        // Verifying repository interactions
        verify(userRepository, times(1)).getByUserEmail(user1.getUserEmail(), tx);

        verify(roleUserRepository, times(1)).create(roleUser, tx);

        verify(userRepository, times(1)).create(user1, tx);

    }
//

    /**
     * Commented Test Case, will update later.
     * @Test
     @DisplayName("Test Delete User")
     void deleteUser() throws JsonProcessingException, CrudException {
     // Arrange mock input data
     String adminEmail = "mayuris@perceptcs.com";

     String userEmail1 = "delete@perceptcs.com";

     List<String> role = Arrays.asList("AUDIT_ADMIN");
     ObjectMapper objectMapper1 = new ObjectMapper();
     String roleString = objectMapper1.writeValueAsString(role);

     // Mocking SecurityContextHolder
     SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
     Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);

     Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
     when(securityContext.getAuthentication()).thenReturn(authentication);
     SecurityContextHolder.setContext(securityContext);


     User user = new User();
     user.setUserEmail(adminEmail);
     user.setRoleJson(roleString);
     user.setIsDeleted(false);


     List<String> role1 = Arrays.asList("GENERAL_USER");
     ObjectMapper objectMapper2 = new ObjectMapper();
     String roleString1 = objectMapper2.writeValueAsString(role1);

     User user1 = new User();
     user1.setUserEmail(userEmail1);
     user1.setName("new user for delete");
     user1.setRoleJson(roleString1);
     user1.setIsDeleted(false);

     List<String> roleList;
     roleList = objectMapper.readValue(user1.getRoleJson(), new TypeReference<List<String>>() {
     });

     RoleUser roleUser = RoleUser.builder()
     .roleName("GENERAL_USER")
     .userName(user1.getName())
     .userEmail(user1.getUserEmail())
     .build();


     //set collaborator user in collaborator
     Collaborator collaborator = new Collaborator();
     collaborator.setOwnedBy(new CollaboratorUser());
     collaborator.setCoOwners(new ArrayList<>());
     collaborator.setMembers(new ArrayList<>());
     collaborator.setReviewers(new ArrayList<>());


     // create collaborator user
     CollaboratorUser collaboratorUser = new CollaboratorUser();
     collaboratorUser.setUserId(user1.getId());
     collaboratorUser.setUserName(user1.getName());
     collaboratorUser.setEmailId(user1.getUserEmail());
     collaboratorUser.setRole(CollaboratorUserRoles.REVIEWER.toString());

     // Add collaboratorUser to the list of reviewers
     List<CollaboratorUser> reviewersList = new ArrayList<>();
     reviewersList.add(collaboratorUser);
     collaborator.setReviewers(reviewersList);

     String aclJson = GenericUtility.convertObjectToStringJson(collaborator);


     AuditSetCollaborators auditSetCollaborators = AuditSetCollaborators.builder()
     .auditSetId("12324344545")
     .auditSetName("my audit set")
     .build();


     AuditSetCollaborators auditSetCollaborators1 = AuditSetCollaborators.builder()
     .auditSetId("12324344545")
     .auditSetName("my audit set")
     .build();

     List<AuditSetCollaborators> collaboratorsSet = new ArrayList<>();
     collaboratorsSet.add(auditSetCollaborators);
     collaboratorsSet.add(auditSetCollaborators1);


     AuditSet auditSet1 = AuditSet.builder()
     .auditSetId(auditSetCollaborators.getAuditSetId())
     .auditSetName(auditSetCollaborators.getAuditSetName())
     .isDeleted(false)
     .ownerEmail("owner@gmail.com")
     .aclJson(aclJson)
     .build();


     List<GroupUserPrivileges> groupUserPrivileges = new ArrayList<>();


     GroupUserPrivileges groupUserPrivilegess = GroupUserPrivileges.builder()
     .userName(user1.getName())
     .userName(user1.getUserEmail())
     .privileges("MEMBER")
     .build();
     groupUserPrivileges.add(groupUserPrivilegess);

     String members = GenericUtility.convertObjectToStringJson(groupUserPrivileges);


     AuditGroup auditGroup = AuditGroup.builder()
     .auditGroupId("1223324243")
     .auditGroupName("one audit group")
     .userEmail(user1.getUserEmail())
     .memberListJson(members)
     .build();

     List<GroupUserPrivileges> memberEmails = objectMapper.readValue(auditGroup.getMemberListJson(), new TypeReference<List<GroupUserPrivileges>>() {
     });

     List<GroupUserPrivileges> groupUserPrivilege1 = new ArrayList<>();
     //
     String collaboratorJson1 = GenericUtility.convertObjectToStringJson(groupUserPrivilege1);

     AuditGroup auditGroups = AuditGroup.builder()
     .auditGroupId("1223324243")
     .auditGroupName("one audit group")
     .userEmail(user1.getUserEmail())
     .memberListJson(collaboratorJson1)
     .build();


     UserAuditGroup userAuditGroup = UserAuditGroup.builder()
     .auditGroupId(auditSet1.getAuditSetId())
     .userEmail("oneuser@gmail.com")
     .auditGroupName(auditSet1.getAuditSetName())
     .privilege("REVIEWER")
     .build();

     UserAuditGroup userAuditGroup1 = UserAuditGroup.builder()
     .auditGroupId(auditSet1.getAuditSetId())
     .userEmail("oneuser@gmail.com")
     .auditGroupName(auditSet1.getAuditSetName())
     .privilege("REVIEWER")
     .build();

     List<UserAuditGroup> userAuditGroupList = new ArrayList<>();
     userAuditGroupList.add(userAuditGroup);
     userAuditGroupList.add(userAuditGroup1);


     // Mock repository
     Mockito.when(userRepository.getByUserEmail(eq(adminEmail), eq(tx))).thenReturn(user);
     Mockito.when(userRepository.getByUserEmail(eq(userEmail1), eq(tx))).thenReturn(user1);
     Mockito.when(userRepository.create(user1, tx)).thenReturn(user1);
     Mockito.when(roleUserRepository.get(roleUser.getRoleName(), roleUser.getUserEmail(), tx)).thenReturn(roleUser);
     Mockito.when(auditSetCollaboratorsRepository.getAuditSetCollaboratorList(user1.getUserEmail(), tx)).thenReturn(collaboratorsSet);
     Mockito.when(auditSetRepository.get(auditSet1.getAuditSetId(), tx)).thenReturn(auditSet1);
     Mockito.when(auditSetRepository.create(auditSet1, tx)).thenReturn(auditSet1);
     Mockito.when(userAuditGroupRepository.getUserGroupList(user1.getUserEmail(), tx)).thenReturn(userAuditGroupList);
     Mockito.when(auditGroupRepository.getAuditGroup(userAuditGroup.getAuditGroupId(), tx)).thenReturn(auditGroup);
     Mockito.when(auditGroupRepository.create(auditGroups, tx)).thenReturn(auditGroups);

     // Actual response
     ApiResponse response = userService.deleteUser(user1.getUserEmail(), userEmail1, tx);

     //Assert actual_response
     assertTrue(response.getStatus());
     assertEquals("User is deleted Successfully.", response.getMessage());
     assertEquals(HttpStatus.OK, response.getHttpStatus());
     assertNull(response.getData());

     //        // Verifying repository interactions
     verify(userRepository, times(2)).getByUserEmail(any(), any());
     verify(userRepository, times(1)).create(user1, tx);
     verify(userRepository, times(1)).create(eq(user1), eq(tx));
     verify(roleUserRepository, times(1)).get(eq(roleUser.getRoleName()), eq(roleUser.getUserEmail()), eq(tx));
     verify(auditSetCollaboratorsRepository, times(1)).getAuditSetCollaboratorList(user1.getUserEmail(), tx);
     verify(auditSetRepository, times(2)).get(any(), any()); // Two calls to get() method
     verify(auditSetRepository, times(1)).create(auditSet1, tx);
     verify(userAuditGroupRepository, times(1)).getUserGroupList(eq(user1.getUserEmail()), eq(tx));
     verify(auditGroupRepository, times(2)).getAuditGroup(any(), any());
     verify(auditGroupRepository, times(2)).create(any(), any());


     }*/


    /**
     * Commented Test Case, will update later.
     * @Test
     @DisplayName("Test update user role success scenario ")
     public void testUpdateUserRole() throws JsonProcessingException {
     // Mocking existing user
     User existingUser = new User();
     existingUser.setUserEmail("jay@gmail.com");
     existingUser.setRoleJson("[\"AUDIT_ADMIN\"]");

     when(objectMapper.readValue(anyString(), eq(new TypeReference<List<String>>() {
     })))
     .thenReturn(Collections.singletonList("AUDIT_ADMIN"));

     // mock calls
     when(userRepository.getByUserEmail("jay@gmail.com", tx))
     .thenReturn(existingUser);


     SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("AUDIT_ADMIN");
     Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
     Mockito.when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
     when(securityContext.getAuthentication()).thenReturn(authentication);
     SecurityContextHolder.setContext(securityContext);


     when(roleUserRepository.get("AUDIT_ADMIN", "jay@gmail.com", tx))
     .thenReturn(new RoleUser());


     when(userRepository.create(existingUser, tx)).thenReturn(existingUser);

     doNothing().when(roleUserRepository).delete(eq("GENERAL_USER"), eq("jay@gmail.com"), any(DistributedTransaction.class));

     ApiResponse response = userService.updateUserRole("jay@gmail.com", Arrays.asList("GENERAL_USER", "AUDIT_ADMIN"), tx);

     // Assert
     assertTrue(response.getStatus());
     assertEquals("The user role has been updated successfully.", response.getMessage());
     assertEquals(HttpStatus.OK, response.getHttpStatus());

     }*/

//    @Test
//    void testGetListOfExternalAuditors() throws TransactionException {
//        // Arrange
//        List<RoleUser> userList = new ArrayList<>();
//        userList.add(new RoleUser("EXTERNAL_AUDITOR", 123L, "Jayesh", "jayesh123@gmail.com"));
//        userList.add(new RoleUser("EXTERNAL_AUDITOR", 124L, "Jayesh Mahajan", "jayesh124@gmail.com"));
//
//        when(roleUserRepository.getByRole("EXTERNAL_AUDITOR", tx)).thenReturn(userList);
//
//        // Act
//        ApiResponse response = userService.getListOfExternalAuditors(tx);
//
//        // Assert
//        assertEquals(true, response.getStatus());
//        assertEquals(HttpStatus.OK, response.getHttpStatus());
//        assertEquals(2, ((List<RoleUser>) response.getData()).size());
//    }
}
