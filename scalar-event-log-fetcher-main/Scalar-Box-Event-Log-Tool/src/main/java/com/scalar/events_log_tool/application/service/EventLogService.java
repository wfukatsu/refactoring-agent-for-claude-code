package com.scalar.events_log_tool.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.db.api.DistributedTransaction;
import com.scalar.db.exception.transaction.CrudException;
import com.scalar.events_log_tool.application.dto.EventDetails;
import com.scalar.events_log_tool.application.exception.GenericException;
import com.scalar.events_log_tool.application.model.Events;
import com.scalar.events_log_tool.application.model.ItemEvents;
import com.scalar.events_log_tool.application.repository.EventsRepository;
import com.scalar.events_log_tool.application.repository.ItemEventsRepository;
import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import com.scalar.events_log_tool.application.utility.Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Pooja
 */
@Slf4j
@Service
public class EventLogService {

    private final EventsRepository eventsRepository;

    private final ItemEventsRepository itemEventsRepository;

    public EventLogService(EventsRepository eventsRepository, ItemEventsRepository itemEventsRepository) {
        this.eventsRepository = eventsRepository;
        this.itemEventsRepository = itemEventsRepository;
    }


    public List<EventDetails> getEventsByDateRange(String startTimestamp, String endTimeStamp, DistributedTransaction transaction) throws CrudException {
        List<EventDetails> dataSets = new ArrayList<>();

        try {

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd HHmmssSSS");

            String utcStartDate = utcFormat.format(inputFormat.parse(startTimestamp));
            String utcEndDate = utcFormat.format(inputFormat.parse(endTimeStamp));

            String startDate = utcStartDate.split(" ")[0];
            String endDate = utcEndDate.split(" ")[0];

            //get the dates between start and end Date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
            LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

            long duration = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);

            String changingStartDate = utcStartDate;


            for (int i = 0; i <= duration; i++) {
                String[] splitDate = changingStartDate.split(" ");
                String date = splitDate[0];

                if (duration == 0) {
                    // Only one day in the duration
                    List<Events> eventsList = eventsRepository.getEventsByDate(transaction, date, splitDate[1], utcEndDate.split(" ")[1]);
                    dataSets.addAll(eventslistToEventsDetails(eventsList));
                } else if (i == 0) {
                    // First date in the range
                    List<Events> eventsList = eventsRepository.getEventsByDate(transaction, date, splitDate[1], "235959999");

                    dataSets.addAll(eventslistToEventsDetails(eventsList));

                } else if (i == duration) {
                    // Last date in the range
                    List<Events> eventsList = eventsRepository.getEventsByDate(transaction, date, "000000000", utcEndDate.split(" ")[1]);

                    dataSets.addAll(eventslistToEventsDetails(eventsList));
                } else {
                    List<Events> eventsList = eventsRepository.getEventsByDate(transaction, date, "000000000", "235959999");
                    dataSets.addAll(eventslistToEventsDetails(eventsList));

                }

                Calendar c = Calendar.getInstance();
                c.setTime(utcFormat.parse(changingStartDate));
                c.add(Calendar.DATE, 1);
                changingStartDate = utcFormat.format(c.getTime());
            }

        } catch (ParseException e) {
            throw new GenericException("Error parsing date ");
        }

        return dataSets;
    }


    private List<EventDetails> eventslistToEventsDetails(List<Events> eventsList) {

        List<EventDetails> eventDetailsList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (Events events : eventsList) {
            try {
                //Convert source json to json node
                JsonNode node = mapper.readTree(events.getSourceJson());
                EventDetails eventDetails = EventDetails.builder()
                        .eventId(events.getEventId())
                        .eventType(events.getEventType())
                        .eventCreatedUserId(events.getUserId())
                        .eventCreatedUserName(events.getUserName())
                        .itemId(events.getItemId())
                        .itemName(node.get("name").asText())
                        .eventCreatedAt(String.valueOf(events.getCreatedAt()))
                        .build();
                eventDetailsList.add(eventDetails);

            } catch (JsonProcessingException e) {
                throw new GenericException("Error converting JSON");
            }
        }
        return eventDetailsList;
    }

    public ApiResponse getEventsByDateRangeAndUser(List<EventDetails> dataSets, Long userId) {
        List<EventDetails> eventsList = dataSets.stream()
                .filter(event -> userId.equals(event.getEventCreatedUserId()))
                .collect(Collectors.toList());

        if (!eventsList.isEmpty()) {
            return new ApiResponse(true, "", HttpStatus.OK, eventsList);
        } else {
            return new ApiResponse(true, Translator.toLocale("com.event.notFound.dateUser"), HttpStatus.NOT_FOUND, null);
        }
    }


    public ApiResponse getEventsByDateRangeAndEventType(List<EventDetails> dataSets, String eventType) {

        List<EventDetails> eventsList = dataSets.stream()
                .filter(e -> e.getEventType().equals(eventType))
                .collect(Collectors.toList());

        if (!eventsList.isEmpty())
            return new ApiResponse(true, "", HttpStatus.OK, eventsList);
        else
            return new ApiResponse(true, Translator.toLocale("com.event.notFound.dateEventType"), HttpStatus.NOT_FOUND, null);

    }

    public ApiResponse getEventsByDateRangeAndFileId(String startTimestamp, String endTimeStamp, Long fileId, DistributedTransaction transaction) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/ddHH:mm:ss");
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        ObjectMapper mapper = new ObjectMapper();
        List<EventDetails> eventDetailList = new ArrayList<>();

        try {
            Date utcStartDate = dateFormat.parse(startTimestamp);
            Date utcEndDate = dateFormat.parse(endTimeStamp);

            Long startDate = Long.valueOf(utcFormat.format(utcStartDate));
            Long endDate = Long.valueOf(utcFormat.format(utcEndDate));

            List<ItemEvents> eventsList = itemEventsRepository.getEventsByItem(fileId, startDate, endDate, transaction);


            for (ItemEvents events : eventsList) {

                JsonNode node = mapper.readTree(events.getEventJsonData());

                EventDetails eventDetails = EventDetails.builder()
                        .eventId(events.getEventId())
                        .eventType(events.getEventType())
                        .eventCreatedUserId(node.get("eventCreatedUserId").asLong())
                        .eventCreatedUserName(node.get("eventCreatedUserName").asText())
                        .itemId(events.getItemId())
                        .itemName(node.get("name").asText())
                        .eventCreatedAt(String.valueOf(events.getEventDate()))
                        .build();

                eventDetailList.add(eventDetails);
            }

            if (!eventDetailList.isEmpty())
                return new ApiResponse(true, "", HttpStatus.OK, eventDetailList);
            else
                return new ApiResponse(true, Translator.toLocale("com.event.NotFound.fileDate"), HttpStatus.NOT_FOUND, eventDetailList);

        } catch (ParseException | JsonProcessingException e) {
            throw new GenericException(Translator.toLocale("com.something.wrong"));
        }
    }

    public ApiResponse getEventsByDateRangeAndEventTypeAndUser(List<EventDetails> dataSets, String eventType, Long userId) {
        List<EventDetails> eventsList = dataSets.stream()
                .filter(e -> e.getEventType().equals(eventType) && e.getEventCreatedUserId().equals(userId))
                .collect(Collectors.toList());

        if (!eventsList.isEmpty())
            return new ApiResponse(true, "", HttpStatus.OK, eventsList);
        else
            return new ApiResponse(true, Translator.toLocale("com.event.NotFound.dateEventUser"), HttpStatus.NOT_FOUND, null);

    }

}
