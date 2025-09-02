package com.example.batchspark.config;

import com.example.batchspark.batch.*;
import com.example.batchspark.model.GenericDataRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
    
    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);
    
    private final SparkAnalyticsTasklet sparkAnalyticsTasklet;
    
    public BatchConfig(SparkAnalyticsTasklet sparkAnalyticsTasklet) {
        this.sparkAnalyticsTasklet = sparkAnalyticsTasklet;
    }
    
    @Bean
    public Job genericDataProcessingJob(JobRepository jobRepository, 
                                      PlatformTransactionManager transactionManager) {
        return new JobBuilder("genericDataProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(loadDataStep(jobRepository, transactionManager))
                .next(sparkAnalyticsStep(jobRepository, transactionManager))
                .build();
    }
    
    @Bean
    public Step loadDataStep(JobRepository jobRepository, 
                           PlatformTransactionManager transactionManager) {
        return new StepBuilder("loadDataStep", jobRepository)
                .<GenericDataRecord, GenericDataRecord>chunk(100, transactionManager)
                .reader(genericItemReader())
                .processor(genericItemProcessor())
                .writer(genericItemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }
    
    @Bean
    public Step sparkAnalyticsStep(JobRepository jobRepository, 
                                 PlatformTransactionManager transactionManager) {
        return new StepBuilder("sparkAnalyticsStep", jobRepository)
                .tasklet(sparkAnalyticsTasklet, transactionManager)
                .build();
    }
    
    @Bean
    public GenericItemReader genericItemReader() {
        return new GenericItemReader();
    }
    
    @Bean
    public GenericItemProcessor genericItemProcessor() {
        return new GenericItemProcessor();
    }
    
    @Bean
    public GenericItemWriter genericItemWriter() {
        return new GenericItemWriter();
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("batch-");
        executor.initialize();
        return executor;
    }
}