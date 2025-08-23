package kh.edu.istad.codecompass.dto.jugde0.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateSubmissionRequest(
        @JsonProperty("source_code")
        @NotBlank(message = "Source code cannot be blank")
        String sourceCode,

        @JsonProperty("language_id")
        @NotNull(message = "Language ID cannot be null")
        String languageId,  // Must be Integer

        @JsonProperty("stdin")
        String stdin,

        @JsonProperty("expected_output")
        String expectedOutput,

        // Optional fields
        @JsonProperty("number_of_runs")
        Integer numberOfRuns,
        @JsonProperty("cpu_time_limit")
        Double cpuTimeLimit,
        @JsonProperty("cpu_extra_time")
        Double cpuExtraTime,
        @JsonProperty("wall_time_limit")
        Double wallTimeLimit,
        @JsonProperty("memory_limit")
        Integer memoryLimit,
        @JsonProperty("stack_limit")
        Integer stackLimit,
        @JsonProperty("max_processes_and_or_threads")
        Integer maxProcessesAndOrThreads,
        @JsonProperty("enable_per_process_and_thread_time_limit")
        Boolean enablePerProcessAndThreadTimeLimit,
        @JsonProperty("enable_per_process_and_thread_memory_limit")
        Boolean enablePerProcessAndThreadMemoryLimit,
        @JsonProperty("max_file_size")
        Integer maxFileSize,
        @JsonProperty("enable_network")
        Boolean enableNetwork
) {}
