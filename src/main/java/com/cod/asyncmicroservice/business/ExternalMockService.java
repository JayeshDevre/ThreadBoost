package com.cod.asyncmicroservice.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExternalMockService {
    private static final Logger log = LoggerFactory.getLogger(ExternalMockService.class);

    public String getCreditScore() {
        log.info("Fetching credit score. This will take 2 seconds...");
        simulateDelay(2000);
        return "Score: 780";
    }

    public String getOrderHistory() {
        log.info("Fetching order history. This will take 2 seconds...");
        simulateDelay(2000);
        return "Orders: 42 - Recent: Laptop";
    }

    public String getRecommendations() {
        log.info("Fetching recommendations. This will take 2 seconds...");
        simulateDelay(2000);
        return "Recommend: Mechanical Keyboard";
    }

    public String processSlowTask() {
        log.info("Processing slow task. This will take 3 seconds...");
        simulateDelay(3000);
        return "Slow task completed successfully.";
    }

    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread was interrupted", e);
        }
    }
}
