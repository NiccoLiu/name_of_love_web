package com.love;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NameOfLoveWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(NameOfLoveWebApplication.class, args);
    }
}
