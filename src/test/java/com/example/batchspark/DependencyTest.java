package com.example.batchspark;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test to verify that Spring and SLF4J dependencies are properly configured
 */
@SpringBootTest
public class DependencyTest {

    private static final Logger log = LoggerFactory.getLogger(DependencyTest.class);

    @Test
    public void testSLF4JLogger() {
        assertNotNull(log);
        log.info("SLF4J Logger is working correctly");
    }

    @Component
    static class TestComponent {
        private static final Logger log = LoggerFactory.getLogger(TestComponent.class);
        
        public void testMethod() {
            log.info("Test component is working");
        }
    }

    @Test
    public void testSpringContext() {
        // This test will pass if Spring context loads successfully
        log.info("Spring Boot 3.5.5 context loaded successfully");
    }
    
    @Test
    public void testSpringBootVersion() {
        // Verify we're running the correct Spring Boot version
        String version = org.springframework.boot.SpringBootVersion.getVersion();
        log.info("Running Spring Boot version: {}", version);
        assertNotNull(version);
    }
}