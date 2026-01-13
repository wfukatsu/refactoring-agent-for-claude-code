package com.scalar.events_log_tool.application.business;

import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.exception.transaction.RollbackException;
import com.scalar.db.exception.transaction.TransactionException;
import com.scalar.events_log_tool.application.constant.EventType;
import com.scalar.events_log_tool.application.dto.EventDetails;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.User;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.service.EventLogService;
import com.scalar.events_log_tool.application.service.UserService;
import com.scalar.events_log_tool.application.utility.GenericUtility;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventLogBusiness {

    private final DistributedTransactionManager transactionManager;

    private final EventLogService eventLogService;

    private final UserService userService;

    public EventLogBusiness(DistributedTransactionManager transactionManager, EventLogService eventLogService, UserService userService) {
        this.transactionManager = transactionManager;
        this.eventLogService = eventLogService;
        this.userService = userService;
    }

    public ApiResponse getEventsByDateRange(Date startDate, Date endDate) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();
            List<EventDetails> dataSets = eventLogService.getEventsByDateRange(dateFormat.format(startDate), dateFormat.format(endDate), transaction);
            transaction.commit();
            if (!dataSets.isEmpty())
                return new ApiResponse(true, "", HttpStatus.OK, dataSets);
            else
                return new ApiResponse(true, Translator.toLocale("com.event.notFound.date"), HttpStatus.OK, new ArrayList<>());

        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse getEventsByDateRangeAndUser(Date startDate, Date endDate, String userId) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();
            List<EventDetails> dataSets = eventLogService.getEventsByDateRange(dateFormat.format(startDate), dateFormat.format(endDate), transaction);
            ApiResponse apiResponse = eventLogService.getEventsByDateRangeAndUser(dataSets, Long.parseLong(userId));
            transaction.commit();
            return apiResponse;

        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse getEventsByDateRangeAndEventType(Date startDate, Date endDate, String eventType) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();

            List<EventDetails> dataSets = eventLogService.getEventsByDateRange(dateFormat.format(startDate), dateFormat.format(endDate), transaction);
            ApiResponse apiResponse = eventLogService.getEventsByDateRangeAndEventType(dataSets, eventType);
            transaction.commit();
            return apiResponse;

        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse getEventsByDateRangeAndFileId(Date startDate, Date endDate, Long fileId) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();

            ApiResponse apiResponse = eventLogService.getEventsByDateRangeAndFileId(dateFormat.format(startDate), dateFormat.format(endDate), fileId, transaction);
            transaction.commit();
            return apiResponse;

        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }

    public ApiResponse getEventsByDateRangeAndUserAndItemId(Date startDate, Date endDate, Long userId, Long itemId) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();

            ApiResponse eventsByDateRangeAndFileIdResponse = eventLogService.getEventsByDateRangeAndFileId(dateFormat.format(startDate), dateFormat.format(endDate), itemId, transaction);
            if (eventsByDateRangeAndFileIdResponse.getStatus()) {
                List<EventDetails> eventDetailList = (List<EventDetails>) eventsByDateRangeAndFileIdResponse.getData();

                User user = userService.getByUserId(userId, transaction);
                if (user == null) {
                    throw new GenericException(Translator.toLocale("com.user.notFound"));
                }
                transaction.commit();
                List<EventDetails> filteredData = eventDetailList.stream()
                        .filter(e -> e.getEventCreatedUserId().equals(userId))
                        .collect(Collectors.toList());
                return ApiResponse.builder()
                        .data(filteredData)
                        .httpStatus(HttpStatus.OK)
                        .message("")
                        .status(true)
                        .build();
            }
            return eventsByDateRangeAndFileIdResponse;
        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }


    }

    public ApiResponse getEventsByDateRangeAndEventTypeAndItemIdAndUserId(Date startDate, Date endDate, String
            eventType, Long itemId, Long userId) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();

            ApiResponse eventsByDateRangeAndFileId = eventLogService.getEventsByDateRangeAndFileId(dateFormat.format(startDate), dateFormat.format(endDate), itemId, transaction);
            if (eventsByDateRangeAndFileId.getStatus()) {
                List<EventDetails> eventDetailList = (List<EventDetails>) eventsByDateRangeAndFileId.getData();

                User user = userService.getByUserId(userId, transaction);
                if (user == null) {
                    throw new GenericException(Translator.toLocale("com.unable.transaction"));
                }
                transaction.commit();
                List<EventDetails> filteredData = eventDetailList.stream()
                        .filter(e -> e.getEventCreatedUserId().equals(userId) && e.getEventType().equalsIgnoreCase(eventType))
                        .collect(Collectors.toList());
                return ApiResponse.builder()
                        .data(filteredData)
                        .httpStatus(HttpStatus.OK)
                        .message("")
                        .status(true)
                        .build();
            }
            return eventsByDateRangeAndFileId;
        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }

    }

    public ApiResponse getEventType() {

        List<String> eventTypeList = new ArrayList<>();
        for (EventType eventType : EventType.values()) {
            eventTypeList.add(eventType.name());
        }
        return new ApiResponse(true, "", HttpStatus.OK, eventTypeList);
    }

    public ApiResponse getEventsByDateRangeAndEventTypeAndItemId(Date startDate, Date endDate, String eventType, Long itemId) {

        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();
            String startDateObj = dateFormat.format(startDate);
            String endDateObj = dateFormat.format(endDate);

            ApiResponse eventsByDateRangeAndFileId = eventLogService.getEventsByDateRangeAndFileId(startDateObj, endDateObj, itemId, transaction);
            if (eventsByDateRangeAndFileId.getStatus()) {
                List<EventDetails> eventDetailList = (List<EventDetails>) eventsByDateRangeAndFileId.getData();

                transaction.commit();
                List<EventDetails> filteredData = eventDetailList.stream()
                        .filter(e -> e.getEventType().equalsIgnoreCase(eventType))
                        .collect(Collectors.toList());
                return ApiResponse.builder()
                        .data(filteredData)
                        .httpStatus(HttpStatus.OK)
                        .message("")
                        .status(true)
                        .build();
            }
            return eventsByDateRangeAndFileId;
        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }


    }

    public ApiResponse getEventsByDateRangeAndEventTypeAndUser(Date startDate, Date endDate, String eventType, Long userId) {
        DistributedTransaction transaction;
        try {
            transaction = transactionManager.start();
        } catch (TransactionException te) {
            throw new GenericException(Translator.toLocale("com.unable.transaction"));
        }

        try {
            DateFormat dateFormat = GenericUtility.getDateFormat();
            List<EventDetails> dataSets = eventLogService.getEventsByDateRange(dateFormat.format(startDate), dateFormat.format(endDate), transaction);
            ApiResponse apiResponse = eventLogService.getEventsByDateRangeAndEventTypeAndUser(dataSets, eventType, userId);
            transaction.commit();
            return apiResponse;

        } catch (TransactionException e) {

            log.info("Error while Committing transaction ..");
            try {
                transaction.rollback();
            } catch (RollbackException ex) {
                ex.printStackTrace();
            }
            throw new GenericException(e.getMessage());
        }
    }
}

