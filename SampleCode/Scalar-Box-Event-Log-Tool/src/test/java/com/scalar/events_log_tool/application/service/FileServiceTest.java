package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.dto.ExtAuditorEventLog;
import com.scalar.events_log_tool.application.model.AuditSet;
import com.scalar.events_log_tool.application.model.AuditorLogs;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.repository.*;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class FileServiceTest {
    @MockBean
    DistributedTransactionManager manager;

    @MockBean
    DistributedTransaction tx;
    @Autowired
    private FileService fileService;

    @MockBean
    private AuditorLogsRepository auditorLogsRepository;
    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ItemStatusRepository itemStatusRepository;
    @MockBean
    private ObjectMapper objectMapper;
    @MockBean
    private AuditSetRepository auditSetRepository;

    @MockBean
    private ItemsBySha1Repository itemsBySha1Repository;

    @BeforeEach
    private void setUp() throws TransactionException {
        when(manager.start()).thenReturn(tx);
    }

    /*
    Commented Test Case
    @Test
    @DisplayName("Test get file copies Success Scenario")
    void testGetFileCopies() throws TransactionException, JsonProcessingException {
        // Arrange input data
        String sha1Hash = "yourSha1Hash";
        Long itemId = 1L;

        com.scalar.events_log_tool.application.dto.User
                user = new com.scalar.events_log_tool.application.dto.User();
        user.setId("123");
        user.setLogin("jay@gmail.com");
        user.setName("jayesh");
        user.setType("user");

        String aclJsonInString = objectMapper.writeValueAsString(user);

        List<ItemsBySha1> mockItemsBySha1List = new ArrayList<>();

        ItemsBySha1 itemsBySha1 = ItemsBySha1.builder()
                .sha1Hash("yourSha1Hash")
                .itemId(1L)
                .itemVersionId(11L)
                .itemName("ItemName")
                .itemVersionNumber(0)
                .createdAt(20240116111648L)
                .path("a->b")
                .ownerByJson(aclJsonInString)
                .build();

        mockItemsBySha1List.add(itemsBySha1);

        //mock calls
        when(itemsBySha1Repository.getItemsBySha1(sha1Hash, tx)).thenReturn(mockItemsBySha1List);

        // Actual Response
        ApiResponse result = fileService.getFileCopies(sha1Hash, itemId,null, tx);

        // Assert
        assertTrue(result.getStatus());
        assertEquals(HttpStatus.OK, result.getHttpStatus());
        assertNotNull(result.getData());

        // Verify
        verify(itemsBySha1Repository, times(1)).getItemsBySha1(sha1Hash, tx);
    }
    */

    @Test
    @DisplayName("Test add external auditor event log Success Scenario")
    void addExtAuditorEventLog() throws JsonProcessingException {


        // Mocking data
        List<String> role = Arrays.asList("EXTERNAL_AUDITOR");
        String roleString = objectMapper.writeValueAsString(role);

        User user1 = User.builder()
                .userEmail("mayu@perceptcs.com")
                .id(1L)
                .name("mayu sutar")
                .password("mayu")
                .imageUrl("image_url")
                .organizationName("kanzen")
                .roleJson(roleString)
                .build();

        // Mocking SecurityContextHolder
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("EXTERNAL_AUDITOR");
        Collection<SimpleGrantedAuthority> authCollection = Collections.singleton(simpleGrantedAuthority);
        when(authentication.getAuthorities()).thenReturn((Collection) authCollection);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        //convert date into UTC format
        SimpleDateFormat utcFormats = new SimpleDateFormat("yyyyMMdd-HHmmss");
        utcFormats.setTimeZone(TimeZone.getTimeZone("UTC"));
        String formattedUtcDates = utcFormats.format(new Date());
        String[] split = formattedUtcDates.split("-");
        String withoutMillis = split[1].substring(0, 6);


        AuditSet auditSet = AuditSet.builder()
                .auditSetId("8af4333f-d738-4298-a6a2-2d40e7db5b4f")
                .auditSetName("new audit set")
                .description("new audit set 1")
                .ownerId(user1.getId())
                .ownerName(user1.getName())
                .ownerEmail(user1.getUserEmail())
                .isDeleted(false)
                .build();


        ExtAuditorEventLog extAuditorEventLog = ExtAuditorEventLog.builder()
                .auditSetId(auditSet.getAuditSetId())
                .itemId(123L)
                .actionType("ITEM_DOWNLOAD")
                .itemType("FILE")
                .build();


        AuditorLogs auditorLogs = AuditorLogs.builder()
                .auditSetId(extAuditorEventLog.getAuditSetId())
                .itemId(extAuditorEventLog.getItemId())
                .userEmail(user1.getUserEmail())
                .eventDate(Long.parseLong(split[0] + withoutMillis))
                .eventType(extAuditorEventLog.getActionType())
                .customJsonEventDetails(null)
                .itemType("FILE")
                .build();


        // Mocking repository behavior
        when(userRepository.getByUserEmail(user1.getUserEmail(), tx)).thenReturn(user1);
        when(auditorLogsRepository.create(auditorLogs, tx)).thenReturn(auditorLogs);
        when(auditSetRepository.get(eq(auditSet.getAuditSetId()), eq(tx))).thenReturn(auditSet);
        when(auditorLogsRepository.getAuditorLogsByPrimaryKey(auditorLogs.getAuditSetId(),
                auditorLogs.getItemId(), auditorLogs.getUserEmail(), auditorLogs.getEventDate(), tx)).thenReturn(null);

        // Test the method
        ApiResponse response = fileService.addExtAuditorEventLog(extAuditorEventLog, user1.getUserEmail(), tx);

        // Assertions
        assertTrue(response.getStatus());
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertNotNull(response.getData());


        // Verifying repository interactions
        verify(userRepository, times(1)).getByUserEmail(user1.getUserEmail(), tx);
        verify(auditorLogsRepository, times(1)).create(auditorLogs, tx);
        verify(auditSetRepository, times(1)).get(any(String.class), eq(tx));
    }
}