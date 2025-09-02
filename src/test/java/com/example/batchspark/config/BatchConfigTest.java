package com.example.batchspark.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class BatchConfigTest {

    @Autowired
    private Job genericDataProcessingJob;

    @Test
    void testJobConfiguration() {
        assertThat(genericDataProcessingJob).isNotNull();
        assertThat(genericDataProcessingJob.getName()).isEqualTo("genericDataProcessingJob");
    }
}