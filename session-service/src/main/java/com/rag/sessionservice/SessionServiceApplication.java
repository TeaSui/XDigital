package com.rag.sessionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class SessionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SessionServiceApplication.class, args);
    }

}
