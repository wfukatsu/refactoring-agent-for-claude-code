package com.scalar.events_log_tool.application.controller;

import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.business.AuditSetCollaboratorBusiness;
import com.scalar.events_log_tool.application.dto.AuditSetCollab;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/box/auditSetCollab")
@CrossOrigin(origins = "*")
public class AuditSetCollaboratorController {


    private final AuditSetCollaboratorBusiness auditSetCollaboratorBusiness;

    public AuditSetCollaboratorController(AuditSetCollaboratorBusiness auditSetCollaboratorBusiness) {
        this.auditSetCollaboratorBusiness = auditSetCollaboratorBusiness;
    }


    /**
     * Author: Jayesh
     * Description: This API facilitates the change of ownership for an audit set. (Only co-owners of the audit set are eligible to become the new owner.)
     */
    @Operation(summary = "Change AuditSet Owner API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/changeAuditSetOwner")
    public ResponseEntity<ApiResponse> changeAuditSetOwner(@RequestParam String auditSetId,
                                                           @RequestParam String newOwnerId) {

        log.info("Change audit set owner api called");
        ApiResponse apiResponse = auditSetCollaboratorBusiness.changeAuditSetOwner(auditSetId, newOwnerId, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Jayesh
     * Description: This API allows you to retrieve a list of all collaborators (Owner,Co_owners,Members,Reviewers) associated with a specific audit set.
     */
    @Operation(summary = "getCollaboratorsForAuditSet API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getCollaboratorsForAuditSet")
    public ResponseEntity<ApiResponse> getCollaboratorForAuditSet(@RequestParam String auditSetId) {
        log.info("Get audit set collaborators api called");
        ApiResponse apiResponse = auditSetCollaboratorBusiness.getCollaboratorForAuditSet(auditSetId);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API allows you to mark is favourite audit set.
     */
    @Operation(summary = "mark is favourite audit set API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/markIsFavouriteAuditSet")
    public ResponseEntity<ApiResponse> markIsFavouriteAuditSet(@RequestParam String auditSetId, @RequestParam("status") Boolean status) {
        log.info("Mark is favourite audit set api called");
        ApiResponse apiResponse = auditSetCollaboratorBusiness.markIsFavouriteAuditSet(auditSetId, status, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Operation(summary = "getGeneralUserList API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getGeneralUserList")
    public ResponseEntity<ApiResponse> getGeneralUserList()  {
        log.info("Get general user list api called");
        ApiResponse apiResponse = auditSetCollaboratorBusiness.getGeneralUserList();
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }
    @Operation(summary = "getAuditAdminList API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getAuditAdminList")
    public ResponseEntity<ApiResponse> getAuditAdminList()  {
        log.info("Get audit admin list api called");
        ApiResponse apiResponse = auditSetCollaboratorBusiness.getAuditAdminList();
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }
}
