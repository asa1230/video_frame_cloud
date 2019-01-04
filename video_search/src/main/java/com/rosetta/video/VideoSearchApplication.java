package com.rosetta.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class VideoSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoSearchApplication.class, args);
    }

}

