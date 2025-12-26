package com.scalar.events_log_tool.application.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scalar.events_log_tool.application.dto.PathCollection;
import com.scalar.events_log_tool.application.dto.PathEntry;
import com.scalar.events_log_tool.application.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Abhishek
 */
@Slf4j
public class GenericUtility {


    private static String tokenBoxEndpoint = "https://api.box.com/oauth2/token";


    public static String getExactPath(PathCollection pathCollection) {
        return
                pathCollection.getEntries().stream().map(PathEntry::getName)
                        .collect(Collectors.joining("/"));

    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String convertObjectToStringJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(object);
            log.info("{}: {}", object.getClass().getSimpleName(), json);
            return json;
        } catch (JsonProcessingException e) {
            throw new GenericException("Error converting object to JSON");

        }
    }


    public static ResponseEntity<String> refreshToken(String refreshToken, String clientId, String clientSecret) {

        // Create RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Set request headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Set request parameters
        String requestBody = "grant_type=refresh_token" +
                "&refresh_token=" + refreshToken +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret;

        // Create HTTP entity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        // Make the POST request
        return restTemplate.exchange(tokenBoxEndpoint, HttpMethod.POST, requestEntity, String.class);
    }

    public static String generateOtp() {
        String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
        if (Integer.parseInt(otp) < 100000) {
            otp = Integer.toString(Integer.parseInt(otp) + 100000);
        }
        log.info("Generating Otp: " + otp);
        return otp;
    }


    public static String getStringSizeLengthFile(long size) {

        if (size == 0) {
            return "0 KB";
        }
        DecimalFormat df = new DecimalFormat("0");

        Integer sizeKb = 1024;
        Integer sizeMb = sizeKb * sizeKb;
        Integer sizeGb = sizeMb * sizeKb;
        Integer sizeTerra = sizeGb * sizeKb;

        if ((size < sizeKb))
            return df.format(size) + " B";
        else if (size < sizeMb)
            return df.format(size / sizeKb) + " KB";
        else if (size < sizeGb)
            return df.format(size / sizeMb) + " MB";
        else if (size < sizeTerra)
            return df.format(size / sizeGb) + " GB";

        return "--";
    }


    public static DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    }

    public static SimpleDateFormat getUTCDateFormatWithoutMilliseconds() {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMddHHmmSS");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormat;

    }

    public static SimpleDateFormat getUTCDateFormatWithMilliseconds() {
        SimpleDateFormat utcFormat = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormat;

    }
}
