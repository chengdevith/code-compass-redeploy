package kh.edu.istad.codecompass.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Data
public class Judge0TimeoutConfig {
    // HTTP request timeouts (communication with Judge0 API)
    @Value("${judge0.http-connection-timeout}")
    private Duration httpConnectionTimeout;
    @Value("${judge0.http-read-timeout}")
    private Duration httpReadTimeout;
    @Value("${judge0.http-write-timeout}")
    private Duration httpWriteTimeout;

    // Initial submission request timeout
    @Value("${judge0.submission-request-timeout}")
    private Duration submissionRequestTimeout;

    // Polling configuration
    @Value("${judge0.polling-timeout}")
    private Duration pollingTimeout;                                // Per poll request
    @Value("${judge0.total-polling-timeout}")
    private Duration totalPollingTimeout;                           // Total time to poll
    @Value("${judge0.initial-polling-delay}")
    private Duration initialPollingDelay;                           // First poll delay
    @Value("${judge0.max-polling-interval}")
    private Duration maxPollingInterval;                            // Max time between polls
    @Value("${judge0.max-polling-attempts}")
    private int maxPollingAttempts = 36;                            // 3 minutes / 5 seconds

    // Code execution limits (what Judge0 will enforce)
    @Value("${judge0.cpu-time-limit}")
    private double cpuTimeLimit;            // 2 seconds for most problems
    @Value("${judge0.wall-time-limit}")
    private double wallTimeLimit;           // 10 seconds total wall time
    @Value("${judge0.memory-limit}")
    private int memoryLimit;               // 256MB memory limit
}