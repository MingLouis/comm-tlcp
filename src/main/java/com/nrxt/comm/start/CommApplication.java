package com.nrxt.comm.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.nrxt.comm")
public class CommApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommApplication.class, args);
    }
}
