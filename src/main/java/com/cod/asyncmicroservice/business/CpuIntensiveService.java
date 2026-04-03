package com.cod.asyncmicroservice.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CpuIntensiveService {

    private static final Logger log = LoggerFactory.getLogger(CpuIntensiveService.class);

    // Default iterations for heavy CPU usage
    private static final int HASH_ITERATIONS = 50000;

    /**
     * Sequential execution - Blocking all on a single CPU core.
     */
    public List<String> processDataSequentially(int count) {
        log.info("Starting sequential CPU-bound processing for {} items", count);
        return IntStream.range(0, count)
                .mapToObj(this::performHeavyCompute)
                .collect(Collectors.toList());
    }

    /**
     * Parallel execution - Spreading load across all CPU cores instantly.
     */
    public List<String> processDataParallel(int count) {
        log.info("Starting parallel CPU-bound processing for {} items", count);
        return IntStream.range(0, count)
                .parallel()
                .mapToObj(this::performHeavyCompute)
                .collect(Collectors.toList());
    }

    private String performHeavyCompute(int id) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = "SecurePassword" + id;
            for (int i = 0; i < HASH_ITERATIONS; i++) {
                data = bytesToHex(digest.digest(data.getBytes()));
            }
            return data.substring(0, 10); // just return a short portion to save bandwidth
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not found", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
