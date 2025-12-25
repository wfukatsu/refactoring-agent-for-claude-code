package com.scalar.events_log_tool.application.controller;

import com.scalar.events_log_tool.application.business.UserBusiness;
import com.scalar.events_log_tool.application.dto.IntegratedItemDetailsInput;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/box/item")
@CrossOrigin(origins = "*")
public class ItemController {


    private final UserBusiness userBusiness;

    public ItemController(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }


    @Operation(summary = "getIntegratedItemDetails API endpoint")
    @PostMapping("/getIntegratedItemDetail")
    public ResponseEntity<ApiResponse> getIntegratedItemDetails(@RequestBody IntegratedItemDetailsInput integratedItemDetailsInput) {
        log.info("getIntegratedItemDetails API called:");
        ApiResponse apiResponse = userBusiness.getIntegratedItemDetails(integratedItemDetailsInput.getItemId(), integratedItemDetailsInput.getUserId(),
                integratedItemDetailsInput.getItemType());

        return new ResponseEntity<ApiResponse>(apiResponse, apiResponse.getHttpStatus());
    }

}
