package com.cod.asyncmicroservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
public class LoadTestingController {

    private static final Logger log = LoggerFactory.getLogger(LoadTestingController.class);
    private final RestTemplate restTemplate;

    public LoadTestingController(RestTemplateBuilder builder) {
        // Set a strict 4.5 second timeout. The slow-task takes 3.0 seconds.
        // If Tomcat queues the request due to thread starvation, it will hit this timeout.
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofMillis(4500))
                .setReadTimeout(Duration.ofMillis(4500))
                .build();
    }

    @GetMapping("/trigger-load-test")
    public List<String> triggerLoadTest(@RequestParam String type, @RequestParam(defaultValue = "200") int count) {
        log.info("Starting internal load test. Firing {} simultaneous requests to {}", count, type);
        
        ExecutorService executor = Executors.newFixedThreadPool(count);
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            final int requestId = i;
            futures.add(executor.submit(() -> {
                try {
                    String url = "http://localhost:8080/" + type + "/slow-task";
                    restTemplate.getForObject(url, String.class);
                    log.info("Request {} SUCCESS", requestId);
                    return "SUCCESS";
                } catch (Exception e) {
                    log.error("Request {} FAILED: {}", requestId, e.getMessage());
                    return "FAIL";
                }
            }));
        }

        executor.shutdown();
        
        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                results.add("FAIL");
            }
        }

        long successCount = results.stream().filter("SUCCESS"::equals).count();
        log.info("Load test finished. Success: {}, Fail: {}", successCount, count - successCount);
        return results;
    }
}
