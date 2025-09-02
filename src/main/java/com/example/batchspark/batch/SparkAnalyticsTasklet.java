package com.example.batchspark.batch;

import com.example.batchspark.model.FileConfig;
import com.example.batchspark.repository.FileConfigRepository;
import com.example.batchspark.service.SparkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SparkAnalyticsTasklet implements Tasklet {
    
    private static final Logger log = LoggerFactory.getLogger(SparkAnalyticsTasklet.class);
    
    private final SparkService sparkService;
    private final FileConfigRepository fileConfigRepository;
    
    public SparkAnalyticsTasklet(SparkService sparkService, FileConfigRepository fileConfigRepository) {
        this.sparkService = sparkService;
        this.fileConfigRepository = fileConfigRepository;
    }
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info("Starting Spark analytics processing...");
        
        try {
            // Get configuration name from job parameters
            String configName = chunkContext.getStepContext()
                    .getJobParameters()
                    .get("configName")
                    .toString();
            
            Optional<FileConfig> configOpt = fileConfigRepository.findByConfigNameWithColumns(configName);
            
            if (!configOpt.isPresent()) {
                log.error("Configuration not found: {}", configName);
                throw new RuntimeException("Configuration not found: " + configName);
            }
            
            FileConfig fileConfig = configOpt.get();
            String outputPath = "output/analytics/" + fileConfig.getTargetTableName();
            
            // Perform analytics using Spark
            sparkService.performGenericAnalytics(fileConfig, outputPath);
            
            // Perform cross-table analytics if multiple active configs exist
            List<FileConfig> allConfigs = fileConfigRepository.findByIsActiveTrue();
            if (allConfigs.size() > 1) {
                List<String> tableNames = allConfigs.stream()
                        .map(FileConfig::getTargetTableName)
                        .collect(Collectors.toList());
                
                sparkService.performCrossTableAnalytics(tableNames, outputPath + "/cross_analysis");
            }
            
            contribution.incrementWriteCount(1);
            log.info("Completed Spark analytics processing for config: {}", configName);
            
        } catch (Exception e) {
            log.error("Error in Spark analytics processing", e);
            throw e;
        }
        
        return RepeatStatus.FINISHED;
    }
}