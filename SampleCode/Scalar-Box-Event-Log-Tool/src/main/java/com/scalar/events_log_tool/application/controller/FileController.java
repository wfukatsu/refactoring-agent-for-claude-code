package com.scalar.events_log_tool.application.controller;


import com.scalar.events_log_tool.application.business.FileBusiness;
import com.scalar.events_log_tool.application.dto.ExtAuditorEventLog;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/box/file")
@CrossOrigin(origins = "*")
public class FileController {


    private final FileBusiness fileBusiness;

    public FileController(FileBusiness fileBusiness) {
        this.fileBusiness = fileBusiness;
    }

    /**
     * Author: Jayesh
     * Description: Retrieves the copies of a file identified by its SHA-1 hash and itemId.
     */
    @Operation(summary = "Get file copies API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getFileCopies")
    public ResponseEntity<ApiResponse> getFileCopies(@RequestParam String sha1Hash,
                                                     @RequestParam Long itemId) {
        log.info("Get File copies called");
        ApiResponse apiResponse = fileBusiness.getFileCopies(sha1Hash, itemId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Jayesh
     * Description: This API enables the retrieval of all versions associated with a specific file.
     */
    @Operation(summary = "Get file versions API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getFileVersions")
    public ResponseEntity<ApiResponse> getFileVersions(@RequestParam String fileId,
                                                       @RequestParam(value = "auditSetId", required = false) String auditSetId) {

        log.info("Get File versions called");
        ApiResponse apiResponse = fileBusiness.getFileVersions(fileId, SecurityContextHolder.getContext().getAuthentication().getName(), auditSetId);
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


    /**
     * Author: Mayuri
     * Description: This API get file details using fileId.
     */
    @Operation(summary = "getFileDetails API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getFileDetails")
    public ResponseEntity<ApiResponse> getFileDetails(@RequestParam("itemId") Long itemId,
                                                      @RequestParam(value = "auditSetId", required = false) String auditSetId) {
        log.info("Get file details called");
        ApiResponse apiResponse = fileBusiness.getFileDetails(itemId, auditSetId, SecurityContextHolder.getContext().getAuthentication().getName());
        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API get folder details using folderId.
     */
    @Operation(summary = "getFolderDetails API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getFolderDetails")
    public ResponseEntity<ApiResponse> getFolderDetails(@RequestParam("folderId") Long folderId,
                                                        @RequestParam(value = "auditSetId", required = false) String auditSetId) {
        log.info("get folder details called");
        ApiResponse apiResponse = fileBusiness.getFolderDetails(folderId, auditSetId, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API adds external auditor event log in auditor table when external auditor will do any action on audit set.
     */
    @Operation(summary = "addExtAuditorEventLog API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/addExtAuditorEventLog")
    public ResponseEntity<ApiResponse> addExtAuditorEventLog(@RequestBody ExtAuditorEventLog extAuditorEventLog) {
        log.info("add external auditor event log called");
        ApiResponse apiResponse = fileBusiness.addExtAuditorEventLog(extAuditorEventLog, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    /**
     * Author: Mayuri
     * Description: This API to get item collaborator using itemId,itemType.
     */
    @Operation(summary = "getItemCollaborator API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getItemCollaborator")
    public ResponseEntity<ApiResponse> getItemCollaborator(@RequestParam("itemId") String itemId,
                                                           @RequestParam("itemType") String itemType,
                                                           @RequestParam(value = "auditSetId", required = false) String auditSetId) {
        log.info("getItemCollaborator called");
        ApiResponse apiResponse = fileBusiness.getItemCollaborator(itemId, itemType, SecurityContextHolder.getContext().getAuthentication().getName(),auditSetId);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @Operation(summary = "check Tampering Status API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/checkTamperingStatus/{fileId}")
    public ResponseEntity<ApiResponse> checkTamperingStatus(@PathVariable("fileId") String fileId) {
        log.info("checkTamperingStatus API called");
        ApiResponse apiResponse = fileBusiness.checkTamperingStatus(fileId);

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }


}
