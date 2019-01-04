package com.rosetta.video;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class VideoEurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoEurekaServerApplication.class, args);
    }

}

