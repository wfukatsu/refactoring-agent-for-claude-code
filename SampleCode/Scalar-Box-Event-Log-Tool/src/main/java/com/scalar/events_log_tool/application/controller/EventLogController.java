package com.scalar.events_log_tool.application.controller;

import com.scalar.events_log_tool.application.business.EventLogBusiness;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@SecurityRequirement(name = "Authorization")
@RequestMapping("/box/event")
@CrossOrigin(origins = "*")
public class EventLogController {

    private final EventLogBusiness eventLogBusiness;

    public EventLogController(EventLogBusiness eventLogBusiness) {
        this.eventLogBusiness = eventLogBusiness;
    }

    @GetMapping("/getEventsByDateRange")
    public ApiResponse getEventsByDateRange(
            @Parameter(description = "Start date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2024/01/01 00:00:00:000") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
            @Parameter(description = "End date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2024/01/02 00:00:00:000") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate) {
        log.info("getEventsByDateRange Api called");
        return eventLogBusiness.getEventsByDateRange(startDate, endDate);
    }

    @GetMapping("/getEventsByDateRangeAndUser")
    public ApiResponse getEventsByDateRangeAndUser(@Parameter(description = "Start date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
                                                   @Parameter(description = "End date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate,
                                                   @RequestParam("userId") String userId) {

        log.info("getEventsByDateRange called");
        return eventLogBusiness.getEventsByDateRangeAndUser(startDate, endDate, userId);
    }

    @GetMapping("/getEventsByDateRangeAndEventType")
    public ApiResponse getEventsByDateRangeAndEventType(@Parameter(description = "Start date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
                                                        @Parameter(description = "End date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate,
                                                        @RequestParam("eventType") String eventType) {

        log.info("getEventsByDateRangeAndEventType called");
        return eventLogBusiness.getEventsByDateRangeAndEventType(startDate, endDate, eventType);
    }


    @GetMapping("/getEventsByDateRangeAndFileId")
    public ApiResponse getEventsByDateRangeAndFileId(@Parameter(description = "Start date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
                                                     @Parameter(description = "End date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate,
                                                     @RequestParam("FileId") Long fileId) {

        log.info("getEventsByDateRangeAndFileId called");
        return eventLogBusiness.getEventsByDateRangeAndFileId(startDate, endDate, fileId);
    }


    @GetMapping("/getEventsByDateRangeAndUserAndItemId")
    public ApiResponse getEventsByDateRangeAndUserAndItemId(@Parameter(description = "Start date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
                                                            @Parameter(description = "End date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate,
                                                            @RequestParam("userId") Long userId,
                                                            @RequestParam("itemId") Long itemId) {

        log.info("getEventsByDateRangeAndUserAndItemId called");
        return eventLogBusiness.getEventsByDateRangeAndUserAndItemId(startDate, endDate, userId, itemId);
    }


    @GetMapping("/getEventsByDateRangeAndEventTypeAndItemIdAndUserId")
    public ApiResponse getEventsByDateRangeAndEventTypeAndItemIdAndUserId(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
                                                                          @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate,
                                                                          @RequestParam("eventType") String eventType,
                                                                          @RequestParam("itemId") Long itemId,
                                                                          @RequestParam("userId") Long userId) {

        log.info("getEventsByDateRangeAndEventTypeAndItemIdAndUserId called");

        return eventLogBusiness.getEventsByDateRangeAndEventTypeAndItemIdAndUserId(startDate, endDate, eventType, itemId, userId);
    }

    @GetMapping("/getEventsByDateRangeAndEventTypeAndItemId")
    public ApiResponse getEventsByDateRangeAndUserAndItemId(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
                                                            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate,
                                                            @RequestParam("eventType") String eventType,
                                                            @RequestParam("itemId") Long itemId) {

        log.info("getEventsByDateRangeAndEventTypeAndItemId called");

        return eventLogBusiness.getEventsByDateRangeAndEventTypeAndItemId(startDate, endDate, eventType, itemId);
    }

    /**
     * Author: Mayuri
     * Description: This API to get event type.
     */
    @Operation(summary = "getEventType API endpoint", security = @SecurityRequirement(name = "Authorization"))
    @GetMapping("/getEventType")
    public ResponseEntity<ApiResponse> getEventType() {
        log.info("Get event types called ");
        ApiResponse apiResponse = eventLogBusiness.getEventType();

        return new ResponseEntity<>(apiResponse, apiResponse.getHttpStatus());
    }

    @GetMapping("/getEventsByDateRangeAndEventTypeAndUser")
    public ApiResponse getEventsByDateRangeAndEventTypeAndUser(@Parameter(description = "Start date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date startDate,
                                                               @Parameter(description = "End date in the format yyyy/MM/dd HH:mm:ss:SSS", example = "2023/01/01 00:00:00:000") @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss:SSS") Date endDate,
                                                               @RequestParam("eventType") String eventType,
                                                               @RequestParam("userId") Long userId) {

        log.info("getEventsByDateRangeAndEventTypeAndUser called");
        return eventLogBusiness.getEventsByDateRangeAndEventTypeAndUser(startDate, endDate, eventType, userId);
    }
}
