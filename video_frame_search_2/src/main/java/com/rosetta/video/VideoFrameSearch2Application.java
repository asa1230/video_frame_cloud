package com.rosetta.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableEurekaClient
@EnableScheduling
@SpringBootApplication
public class VideoFrameSearch2Application {

    public static void main(String[] args) {
        SpringApplication.run(VideoFrameSearch2Application.class, args);
    }

}

