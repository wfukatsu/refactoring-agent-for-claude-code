package com.scalar.events_log_tool.application.controller;

import com.scalar.events_log_tool.application.business.AuditGroupBusiness;
import com.scalar.events_log_tool.application.dto.CreateAuditGroup;
import com.scalar.events_log_tool.application.dto.UpdateAuditGroup;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/box/auditGroup")
public class AuditGroupController {

    private final AuditGroupBusiness auditGroupBusiness;

    public AuditGroupController(AuditGroupBusiness auditGroupBusiness) {
        this.auditGroupBusiness = auditGroupBusiness;
    }

    /**
     * Author: Mayuri
     * Description: This API enables to create audit group
     */
    @Operation(summary = "createAuditGroup API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/createAuditGroup")
    public ResponseEntity<ApiResponse> createAuditGroup(@Valid @RequestBody CreateAuditGroup createAuditGroup) {
        log.info("Create audit group api called");
        ApiResponse apiResponse = auditGroupBusiness.createAuditGroup(createAuditGroup, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API enables to get list of audit group
     */
    @Operation(summary = "getListOfAuditGroup API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getListOfAuditGroup")
    public ResponseEntity<ApiResponse> getListOfAuditGroup() {
        log.info("Get list of AuditGroup api called  ");
        ApiResponse apiResponse = auditGroupBusiness.getListOfAuditGroup(SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    /**
     * Author: Mayuri
     * Description: This API enables to update audit group
     */
    @Operation(summary = "updateAuditGroup API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/updateAuditGroup/{auditGroupId}")
    public ResponseEntity<ApiResponse> updateAuditGroup(@PathVariable("auditGroupId") String auditGroupId, @RequestBody UpdateAuditGroup updateAuditGroup) {
        log.info("Update audit group api called");
        ApiResponse apiResponse = auditGroupBusiness.updateAuditGroup(auditGroupId, updateAuditGroup);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    /**
     * Author: Jayesh
     * Description: This API enables to delete audit group
     */
    @Operation(summary = "deleteAuditGroup API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/deleteAuditGroup/{auditGroupId}")
    public ResponseEntity<ApiResponse> deleteAuditGroup(@PathVariable("auditGroupId") String auditGroupId) {
        log.info("Delete audit group api called");
        ApiResponse apiResponse = auditGroupBusiness.deleteAuditGroup(auditGroupId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API enables to get list of audit group
     */
    @Operation(summary = "getListOfAuditGroupMembers API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getListOfAuditGroupMembers/{auditGroupId}")
    public ResponseEntity<ApiResponse> getListOfAuditGroupMembers(@PathVariable("auditGroupId") String auditGroupId) {
        log.info("Get list of audit group api called");
        ApiResponse apiResponse = auditGroupBusiness.getListOfAuditGroupMembers(auditGroupId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }
}
