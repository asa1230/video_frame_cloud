package com.rosetta.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableEurekaClient
@EnableScheduling
@SpringBootApplication
public class VideoFrameSearch4Application {

    public static void main(String[] args) {
        SpringApplication.run(VideoFrameSearch4Application.class, args);
    }

}

