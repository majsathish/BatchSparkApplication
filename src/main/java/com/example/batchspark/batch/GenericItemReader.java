package com.example.batchspark.batch;

import com.example.batchspark.model.ColumnConfig;
import com.example.batchspark.model.FileConfig;
import com.example.batchspark.model.GenericDataRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class GenericItemReader extends FlatFileItemReader<GenericDataRecord> {
    
    private static final Logger log = LoggerFactory.getLogger(GenericItemReader.class);
    
    public void configure(FileConfig fileConfig) {
        setName("genericItemReader");
        setResource(new FileSystemResource(fileConfig.getSourceFilePath()));
        
        // Configure line mapper
        DefaultLineMapper<GenericDataRecord> lineMapper = new DefaultLineMapper<>();
        
        // Configure tokenizer
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(fileConfig.getDelimiter());
        
        String[] columnNames = fileConfig.getColumnConfigs().stream()
                .sorted((c1, c2) -> c1.getColumnOrder().compareTo(c2.getColumnOrder()))
                .map(ColumnConfig::getSourceColumnName)
                .toArray(String[]::new);
        
        tokenizer.setNames(columnNames);
        lineMapper.setLineTokenizer(tokenizer);
        
        // Configure field set mapper
        lineMapper.setFieldSetMapper(new GenericFieldSetMapper(fileConfig));
        
        setLineMapper(lineMapper);
        
        if (fileConfig.getHasHeader()) {
            setLinesToSkip(1);
        }
        
        log.info("Configured reader for file: {} with {} columns", 
                fileConfig.getSourceFilePath(), columnNames.length);
    }
    
    private static class GenericFieldSetMapper implements FieldSetMapper<GenericDataRecord> {
        
        private final FileConfig fileConfig;
        
        public GenericFieldSetMapper(FileConfig fileConfig) {
            this.fileConfig = fileConfig;
        }
        
        @Override
        public GenericDataRecord mapFieldSet(FieldSet fieldSet) {
            GenericDataRecord record = new GenericDataRecord(fileConfig.getTargetTableName());
            
            for (ColumnConfig column : fileConfig.getColumnConfigs()) {
                String sourceColumn = column.getSourceColumnName();
                Object value = fieldSet.readString(sourceColumn);
                record.addColumnValue(sourceColumn, value);
            }
            
            return record;
        }
    }
}