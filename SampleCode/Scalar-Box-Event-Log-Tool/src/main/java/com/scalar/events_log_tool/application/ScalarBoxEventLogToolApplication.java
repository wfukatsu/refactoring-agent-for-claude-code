package com.scalar.events_log_tool.application;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Abhishek
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
public class ScalarBoxEventLogToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScalarBoxEventLogToolApplication.class, args);
    }

}
