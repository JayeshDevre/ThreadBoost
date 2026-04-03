package com.cod.asyncmicroservice.controller;

import com.cod.asyncmicroservice.business.CustomerService;
import com.cod.asyncmicroservice.business.FileService;
import com.cod.asyncmicroservice.domain.Customer;
import com.cod.asyncmicroservice.domain.FileData;
import com.cod.asyncmicroservice.domain.DashboardResponse;
import com.cod.asyncmicroservice.business.ExternalMockService;
import com.cod.asyncmicroservice.business.CpuIntensiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController

@RequestMapping("/blocking")
public class BlockingController {
    private static final Logger log = LoggerFactory.getLogger(BlockingController.class);

    @Autowired
    CustomerService customerService;

    @Autowired
    FileService fileService;

    @Autowired
    ExternalMockService externalMockService;

    @Autowired
    CpuIntensiveService cpuIntensiveService;

    @GetMapping("/customers/{name}")
    public List<Customer> getCustomerByName(@PathVariable String name) {
        log.info("Getting customer by name {} ", name);
        List customerList = customerService.getCustomerByName(name);
        log.info("Received {} customers by name {}", customerList.size(), name);
        return customerList;
    }

    @PostMapping("/customers/save")
    public Customer addCustomer(@RequestBody Customer customer) {
        log.info("Adding user {} to the Database", customer.getName());
        return customerService.addCustomer(customer);
    }

    @GetMapping("/fileread")
    public String readFile() {
        log.info("reading file request");
        return fileService.readFile();
    }

    @PostMapping("/filewrite")
    public boolean writeFile(@RequestBody FileData fileData) {
        log.info("Write data {} to File", fileData);
        return fileService.writeFile(fileData);
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {
        log.info("Blocking request to fetch dashboard started");
        String creditScore = externalMockService.getCreditScore();
        String orderHistory = externalMockService.getOrderHistory();
        String recommendation = externalMockService.getRecommendations();

        return new DashboardResponse(creditScore, orderHistory, recommendation);
    }

    @GetMapping("/slow-task")
    public String processSlowTask() {
        log.info("Blocking request for slow task started");
        return externalMockService.processSlowTask();
    }

    @GetMapping("/cpu-heavy")
    public List<String> processCpuHeavy(@RequestParam(defaultValue = "50") int count) {
        log.info("Blocking request for CPU heavy task started");
        return cpuIntensiveService.processDataSequentially(count);
    }

   /* public CompletableFuture<String> readFile() {
        log.info("reading file request");
        return asyncService.readFile();

    }*/

   /* @PostMapping("/filewrite")
    public CompletableFuture<Boolean> writeFile(@RequestBody com.levi.microservicedemo.domain.FileData fileData) {
        log.info("Write data {} to File", fileData);
        return asyncService.writeFile(fileData);
    }*/
}
