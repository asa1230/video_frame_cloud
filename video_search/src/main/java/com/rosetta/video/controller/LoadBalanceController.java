package com.rosetta.video.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 负载均衡调度
 */
@RestController
public class LoadBalanceController {

    private static final Logger log = LoggerFactory.getLogger(LoadBalanceController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public String test(){
        String now = restTemplate.getForObject("http://frame-search/search/test", String.class);
        return now;
    }

    @PostMapping("/search")
    public String search(String url) {
        LinkedMultiValueMap<String,String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("url",url);
        HttpEntity<LinkedMultiValueMap<String,String>> httpEntity = new HttpEntity<>(paramMap,null);
        String body;
        try {
            body = restTemplate.postForEntity("http://frame-search/search/query", httpEntity, String.class).getBody();
        } catch (Exception e) {
            log.error(e.toString());
            body = "3";
        }
        log.info(url + " --- flag : " + body);
        return body;
    }

    @PostMapping("/query")
    public String verify(String url) {
        LinkedMultiValueMap<String,String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("url",url);
        HttpEntity<LinkedMultiValueMap<String,String>> httpEntity = new HttpEntity<>(paramMap,null);
        String body = restTemplate.postForEntity("http://frame-search/search/hbase", httpEntity, String.class).getBody();
        log.info(url + " --- flag : " + body);
        return body;
    }



}
