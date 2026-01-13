package com.scalar.events_log_tool.application.exception;


import com.scalar.events_log_tool.application.responsedto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ExceptionControlAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.info("---------------", ex);
        ex.printStackTrace();
        ApiResponse apiResponse = ApiResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(ex.getMessage())
                .data(null)
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ApiResponse> handleGenricException(GenericException ex, Locale locale) {
        log.info("---------------", ex);
        ex.printStackTrace();
        ApiResponse apiResponse = ApiResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .data(null)
                .message(ex.getMessage())
                .status(false)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {

        List<String> list = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            // error:
            String message = error.getDefaultMessage();
            list.add(message);
        });


        String res = list.get(0);
        ApiResponse apiResponse = ApiResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .data(null)
                .message(res)
                .status(false)
                .build();

        return new ResponseEntity<Object>(apiResponse, HttpStatus.BAD_REQUEST);

    }
}
