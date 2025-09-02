package com.example.batchspark.controller;

import com.example.batchspark.batch.GenericItemProcessor;
import com.example.batchspark.batch.GenericItemReader;
import com.example.batchspark.batch.GenericItemWriter;
import com.example.batchspark.model.FileConfig;
import com.example.batchspark.repository.FileConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/batch")
public class BatchController {
    
    private static final Logger log = LoggerFactory.getLogger(BatchController.class);
    
    private final JobLauncher jobLauncher;
    private final Job genericDataProcessingJob;
    private final FileConfigRepository fileConfigRepository;
    private final GenericItemReader genericItemReader;
    private final GenericItemProcessor genericItemProcessor;
    private final GenericItemWriter genericItemWriter;
    
    public BatchController(JobLauncher jobLauncher, 
                          Job genericDataProcessingJob,
                          FileConfigRepository fileConfigRepository,
                          GenericItemReader genericItemReader,
                          GenericItemProcessor genericItemProcessor,
                          GenericItemWriter genericItemWriter) {
        this.jobLauncher = jobLauncher;
        this.genericDataProcessingJob = genericDataProcessingJob;
        this.fileConfigRepository = fileConfigRepository;
        this.genericItemReader = genericItemReader;
        this.genericItemProcessor = genericItemProcessor;
        this.genericItemWriter = genericItemWriter;
    }
    
    @PostMapping("/start/{configName}")
    public ResponseEntity<String> startBatch(@PathVariable String configName) {
        try {
            // Load configuration
            Optional<FileConfig> configOpt = fileConfigRepository.findByConfigNameWithColumns(configName);
            if (!configOpt.isPresent()) {
                return ResponseEntity.badRequest()
                        .body("Configuration not found: " + configName);
            }
            
            FileConfig fileConfig = configOpt.get();
            
            // Configure batch components
            genericItemReader.configure(fileConfig);
            genericItemProcessor.configure(fileConfig);
            genericItemWriter.configure(fileConfig);
            
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .addString("configName", configName)
                    .toJobParameters();
            
            jobLauncher.run(genericDataProcessingJob, jobParameters);
            
            return ResponseEntity.ok("Batch job started successfully for config: " + configName);
        } catch (Exception e) {
            log.error("Error starting batch job", e);
            return ResponseEntity.internalServerError()
                    .body("Error starting batch job: " + e.getMessage());
        }
    }
    
    @GetMapping("/configs")
    public ResponseEntity<List<FileConfig>> getConfigs() {
        List<FileConfig> configs = fileConfigRepository.findByIsActiveTrue();
        return ResponseEntity.ok(configs);
    }
    
    @GetMapping("/configs/{configName}")
    public ResponseEntity<FileConfig> getConfig(@PathVariable String configName) {
        Optional<FileConfig> config = fileConfigRepository.findByConfigNameWithColumns(configName);
        return config.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}