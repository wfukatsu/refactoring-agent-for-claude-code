package com.scalar.events_log_tool.application.controller;


import com.scalar.events_log_tool.application.business.AuditSetBusiness;
import com.scalar.events_log_tool.application.dto.AuditSetInputDto;
import com.scalar.events_log_tool.application.dto.UpdateAuditSet;
import com.scalar.events_log_tool.application.dto.UpdateAuditSetItem;
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
@RequestMapping("/box/auditSet")
@CrossOrigin(origins = "*")
public class AuditSetController {

    private final AuditSetBusiness auditSetBusiness;

    public AuditSetController(AuditSetBusiness auditSetBusiness) {
        this.auditSetBusiness = auditSetBusiness;
    }

    /**
     * Author: Mayuri
     * Description: This API creates audit set. Only audit admin create audit set
     */
    @Operation(summary = "createAuditSet API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/createAuditSet")
    public ResponseEntity<ApiResponse> createAuditSet(@Valid @RequestBody AuditSetInputDto auditSetInputDto) {
        log.info("Create audit set api called");
        ApiResponse apiResponse = auditSetBusiness.createAuditSet(auditSetInputDto, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API delete audit set.only audit admin delete audit set
     */
    @Operation(summary = "deleteAuditSet API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/deleteAuditSet/{auditSetId}")
    public ResponseEntity<ApiResponse> deleteAuditSet(@PathVariable("auditSetId") String auditSetId) {
        log.info("Delete audit set api called");
        ApiResponse apiResponse = auditSetBusiness.deleteAuditSet(auditSetId, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }

    /**
     * Author: Mayuri
     * Description: This API get my audit set list.
     */
    @Operation(summary = "getMyAuditSetList API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getMyAuditSetList")
    public ResponseEntity<ApiResponse> getMyAuditSetList() {
        log.info("Get my audit set list api called");
        ApiResponse apiResponse = auditSetBusiness.getMyAuditSetList(SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API view external auditor event log,using auditSetId,itemId,userEmail
     */
    @Operation(summary = "viewExtAuditorAccessLog API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/viewExtAuditorAccessLog")
    public ResponseEntity<ApiResponse> viewExtAuditorEventLog(@RequestParam("auditSetId") String auditSetId, @RequestParam("itemId") Long itemId, @RequestParam(value = "userEmail", required = false) String userEmail) {
        log.info("View external auditor event log api called");
        ApiResponse apiResponse = auditSetBusiness.viewExtAuditorEventLog(auditSetId, itemId, userEmail);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API update audit set. Only audit admin create audit set
     */
    @Operation(summary = "updateAuditSet API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/updateAuditSet/{auditSetId}")
    public ResponseEntity<ApiResponse> updateAuditSetInfo(@PathVariable("auditSetId") String auditSetId, @RequestBody UpdateAuditSet updateAuditSet) {
        log.info("Update audit set api called");
        ApiResponse apiResponse = auditSetBusiness.updateAuditSetInfo(auditSetId, updateAuditSet, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    @Operation(summary = "validate Audit Set API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/validateAuditSet/{auditSetId}")
    public ResponseEntity<ApiResponse> verifyAuditSet(@PathVariable("auditSetId") String auditSetId) {
        log.info("Verify audit set api called");
        ApiResponse apiResponse = auditSetBusiness.verifyAuditSet(auditSetId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());

    }


    @Operation(summary = "getMyAuditSetListForItemId API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getMyAuditSetListForItemId/{itemId}")
    public ResponseEntity<ApiResponse> getMyAuditSetListForItemId(@PathVariable("itemId") Long itemId) {
        log.info("Get my audit set list for item Id api Called");
        ApiResponse apiResponse = auditSetBusiness.getMyAuditSetListForItemId(itemId);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Operation(summary = "updateAuditSetsForItemId API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/updateAuditSetsForItemId/{itemId}")
    public ResponseEntity<ApiResponse> updateAuditSetsForItemId(@PathVariable("itemId") Long itemId, @RequestBody UpdateAuditSetItem auditSetItem) {
        log.info("Update audit sets for itemId api Called");
        ApiResponse apiResponse = auditSetBusiness.updateAuditSetsForItemId(itemId, auditSetItem);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

}
