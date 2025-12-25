package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.dto.AddItem;
import com.scalar.events_log_tool.application.dto.Collaborator;
import com.scalar.events_log_tool.application.dto.CollaboratorUser;
import com.scalar.events_log_tool.application.model.AuditSet;
import com.scalar.events_log_tool.application.model.AuditSetItem;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.AuditSetItemRepository;
import com.scalar.events_log_tool.application.repository.AuditSetRepository;
import com.scalar.events_log_tool.application.repository.UserRepository;
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
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuditSetItemServiceTest {

    @Autowired
    private AuditSetItemService auditSetItemService;
    @MockBean
    private ObjectMapper objectMapper;
    @MockBean
    DistributedTransactionManager manager;
    @MockBean
    private DistributedTransaction tx;
    @MockBean
    private AuditSetRepository auditSetRepository;
    @MockBean
    private AuditSetItemRepository auditSetItemRepository;
    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;
    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    private void setUp() throws TransactionException {
        when(manager.start()).thenReturn(tx);
    }

//    @Test
//    @DisplayName("Test add item to auditSet Success Scenario")
//    void testAddItemToAuditSet_Success() throws Exception {
//        // Arrange
//        String auditSetId = "auditSetId";
//        AddItem addItem = new AddItem(12345L, "ABC", "FOLDER", null, null);
//        String currentUser = "adduser@gmail.com";
//
//        User user = new User();
//        user.setUserEmail(currentUser);
//        user.setId(123L);
//
//        CollaboratorUser collaboratorUser = new CollaboratorUser();
//        collaboratorUser.setUserId(123L);
//        collaboratorUser.setUserName("jayesh");
//        collaboratorUser.setEmailId("adduser@gmail.com");
//        collaboratorUser.setRole("CO_OWNER");
//
//        CollaboratorUser collaboratorOwner = new CollaboratorUser();
//        collaboratorOwner.setUserId(6789L);
//        collaboratorOwner.setUserName("jayesh owner");
//        collaboratorOwner.setEmailId("adduserowner@gmail.com");
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
//        ObjectMapper objectMappernew = new ObjectMapper();
//        String aclJsonInString = objectMappernew.writeValueAsString(aclJson);
//
//        AuditSet auditSet = new AuditSet();
//        auditSet.setAuditSetId(auditSetId);
//        auditSet.setAuditSetName("AuditSet_name");
//        auditSet.setAclJson(aclJsonInString);
//
//        AuditSetItem auditSetItem = new AuditSetItem();
//        auditSetItem.setAuditSetId(auditSet.getAuditSetId());
//        auditSetItem.setItemId(addItem.getItemId());
//        auditSetItem.setItemName(addItem.getItemName());
//        auditSetItem.setItemType(addItem.getItemType());
//        auditSetItem.setAccessList(addItem.getAccessListType());
//        auditSetItem.setListJson(addItem.getDenyItems());
//        auditSetItem.setAssignedByUserId(user.getId());
//
//        when(auditSetRepository.get(auditSetId, tx)).thenReturn(auditSet);
//        when(userRepository.getByUserEmail(currentUser, tx)).thenReturn(user);
//        when(objectMapper.readValue(any(String.class), eq(Collaborator.class))).thenReturn(aclJson);
//        when(auditSetItemRepository.get(auditSet.getAuditSetId(), addItem.getItemId(), tx)).thenReturn(null);
//        when(auditSetItemRepository.create(auditSetItem, tx)).thenReturn(auditSetItem);
//
//        // Actual response
//        ApiResponse actualResponse = auditSetItemService.addItemToAuditSet(auditSetId, addItem, currentUser, tx);
//
//        //Assert actual_response
//        assertEquals(HttpStatus.OK, actualResponse.getHttpStatus());
//        assertEquals("Item added to AuditSet Successfully", actualResponse.getMessage());
//        assertEquals(true, actualResponse.getStatus());
//
//        // Verify
//        verify(auditSetRepository, times(1)).get(auditSetId, tx);
//        verify(userRepository, times(1)).getByUserEmail(currentUser, tx);
//        verify(objectMapper, times(1)).readValue(any(String.class), eq(Collaborator.class));
//        verify(auditSetItemRepository, times(1)).get(auditSet.getAuditSetId(), addItem.getItemId(), tx);
//
//    }

}