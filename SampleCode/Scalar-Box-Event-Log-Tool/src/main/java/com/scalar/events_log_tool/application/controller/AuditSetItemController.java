package com.scalar.events_log_tool.application.controller;


import com.scalar.events_log_tool.application.business.AuditSetItemBusiness;
import com.scalar.events_log_tool.application.dto.AddItem;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/box/auditSetItem")
@CrossOrigin(origins = "*")
public class AuditSetItemController {

    private final AuditSetItemBusiness auditSetItemBusiness;

    public AuditSetItemController(AuditSetItemBusiness auditSetItemBusiness) {
        this.auditSetItemBusiness = auditSetItemBusiness;
    }

    /**
     * Author: Jayesh
     * Description: This API facilitates the addition of an item to an audit set.
     */
    @Operation(summary = "Add Item to Audit Set API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/addItemToAuditSet/{auditSetId}")
    public ResponseEntity<ApiResponse> addItemToAuditSet(@PathVariable("auditSetId") String auditSetId, @RequestBody AddItem addItem) {

        log.info("Add Item to Audit Set api called");
        ApiResponse apiResponse = auditSetItemBusiness.addItemToAuditSet(auditSetId, addItem, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    @Operation(summary = "viewItemsFromSelectedAuditSet API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/viewItemsFromSelectedAuditSet/{auditSetId}")
    public ResponseEntity<ApiResponse> viewItemsFromAuditSet(@PathVariable("auditSetId") String auditSetId) {

        log.info("viewItemsFromSelectedAuditSet api called");
        ApiResponse apiResponse = auditSetItemBusiness.getAuditSetItems(auditSetId);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }



    @Operation(summary = "getAllowListFromAuditSet API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getAllowListFromAuditSet/{auditSetId}/{itemId}")
    public ResponseEntity<ApiResponse> getAllowDenyListFromAuditSet(@PathVariable("auditSetId") String auditSetId, @PathVariable("itemId") Long itemId) {

        log.info("getItemFromAuditSet api called");
        ApiResponse apiResponse = auditSetItemBusiness.getAllowListFromAuditSet(auditSetId, itemId);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    @Operation(summary = "getItemFromAuditSet API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getItemFromAuditSet/{auditSetId}/{itemId}")
    public ResponseEntity<ApiResponse> getItemFromAuditSet(@PathVariable("auditSetId") String auditSetId, @PathVariable("itemId") Long itemId, @RequestParam(value = "subfolderId", required = false) Long subfolderId) {

        log.info("getItemFromAuditSet api called");
        ApiResponse apiResponse = auditSetItemBusiness.getItemFromAuditSet(auditSetId, itemId, subfolderId);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


}
