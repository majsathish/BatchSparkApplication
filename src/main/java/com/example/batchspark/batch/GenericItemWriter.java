package com.example.batchspark.batch;

import com.example.batchspark.model.FileConfig;
import com.example.batchspark.model.GenericDataRecord;
import com.example.batchspark.service.GenericDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class GenericItemWriter implements ItemWriter<GenericDataRecord> {
    
    private static final Logger log = LoggerFactory.getLogger(GenericItemWriter.class);
    
    private final GenericDataService genericDataService;
    private FileConfig fileConfig;
    
    public GenericItemWriter(GenericDataService genericDataService) {
        this.genericDataService = genericDataService;
    }
    
    public void configure(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        // Ensure table exists
        genericDataService.createTableIfNotExists(fileConfig);
    }
    
    @Override
    public void write(Chunk<? extends GenericDataRecord> chunk) throws Exception {
        if (fileConfig == null) {
            log.error("FileConfig not set for writer");
            return;
        }
        
        log.info("Writing {} records to table {}", chunk.size(), fileConfig.getTargetTableName());
        genericDataService.insertBatch(chunk.getItems(), fileConfig);
        log.debug("Successfully wrote {} records", chunk.size());
    }
}