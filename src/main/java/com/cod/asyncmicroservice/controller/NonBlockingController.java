package com.cod.asyncmicroservice.controller;

import com.cod.asyncmicroservice.business.AsyncService;
import com.cod.asyncmicroservice.business.CustomerService;
import com.cod.asyncmicroservice.business.FileService;
import com.cod.asyncmicroservice.domain.Customer;
import com.cod.asyncmicroservice.domain.FileData;
import com.cod.asyncmicroservice.domain.DashboardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController

@RequestMapping("/nonblocking")
public class NonBlockingController {
    private static final Logger log = LoggerFactory.getLogger(NonBlockingController.class);

    @Autowired
    AsyncService asyncService;

    @Autowired
    FileService fileService;

    @GetMapping("/customers/{name}")
    public CompletableFuture<List<Customer>> getCustomerByName(@PathVariable String name) {
        log.info("Getting customer by name {} ", name);
        CompletableFuture<List<Customer>> listCompletableFuture = asyncService.getCustomerByName(name);
        return listCompletableFuture;
    }

    @PostMapping("/customers/save")
    public CompletableFuture<Customer> addCustomer(@RequestBody Customer customer) {
        log.info("Adding user {} to the Database", customer.getName());
        return asyncService.saveCustomer(customer);
    }

    @GetMapping("/fileread")
    public CompletableFuture<String> readFile() {
        log.info("reading file request");
        return asyncService.readFile();
    }

    @PostMapping("/filewrite")
    public CompletableFuture<Boolean> writeFile(@RequestBody FileData fileData) {
        log.info("Write data {} to File", fileData);
        return asyncService.writeFile(fileData);
    }

    @GetMapping("/dashboard")
    public CompletableFuture<DashboardResponse> getDashboard() {
        log.info("Non-Blocking request to fetch dashboard started");
        
        CompletableFuture<String> creditScoreFuture = asyncService.getCreditScoreAsync();
        CompletableFuture<String> orderHistoryFuture = asyncService.getOrderHistoryAsync();
        CompletableFuture<String> recommendationsFuture = asyncService.getRecommendationsAsync();

        return CompletableFuture.allOf(creditScoreFuture, orderHistoryFuture, recommendationsFuture)
                .thenApply(ignoredVoid -> {
                    try {
                        return new DashboardResponse(
                                creditScoreFuture.get(),
                                orderHistoryFuture.get(),
                                recommendationsFuture.get()
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Error fetching dashboard data", e);
                    }
                });
    }

    @GetMapping("/slow-task")
    public CompletableFuture<String> processSlowTask() {
        log.info("Non-Blocking request for slow task started");
        return asyncService.processSlowTaskAsync();
    }

    @GetMapping("/cpu-heavy")
    public CompletableFuture<List<String>> processCpuHeavy(@RequestParam(defaultValue = "50") int count) {
        log.info("Non-Blocking request for CPU heavy task started");
        return asyncService.processCpuHeavyAsync(count);
    }
}
