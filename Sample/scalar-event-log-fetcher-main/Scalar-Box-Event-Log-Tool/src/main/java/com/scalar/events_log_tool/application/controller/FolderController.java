package com.scalar.events_log_tool.application.controller;

import com.scalar.events_log_tool.application.business.FolderBusiness;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/box/folder")
@CrossOrigin(origins = "*")
public class FolderController {

    private final FolderBusiness folderBusiness;

    public FolderController(FolderBusiness folderBusiness) {
        this.folderBusiness = folderBusiness;
    }

    /**
     * Author: Mayuri
     * Description: This API to get item list using folderId.
     */
    @Operation(summary = "getItemList API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getItemList")
    public ApiResponse getItemList(@RequestParam("folderId") String folderId) {

        log.info("getItemList called");
        return folderBusiness.getItemList(folderId, SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
