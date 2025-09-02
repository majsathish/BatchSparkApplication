package com.example.batchspark.batch;

import com.example.batchspark.model.ColumnConfig;
import com.example.batchspark.model.FileConfig;
import com.example.batchspark.model.GenericDataRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Component
public class GenericItemProcessor implements ItemProcessor<GenericDataRecord, GenericDataRecord> {
    
    private static final Logger log = LoggerFactory.getLogger(GenericItemProcessor.class);
    
    private FileConfig fileConfig;
    
    public void configure(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }
    
    @Override
    public GenericDataRecord process(GenericDataRecord record) throws Exception {
        if (fileConfig == null) {
            log.error("FileConfig not set for processor");
            return null;
        }
        
        // Process each column according to its configuration
        for (ColumnConfig column : fileConfig.getColumnConfigs()) {
            Object value = record.getColumnValue(column.getSourceColumnName());
            
            // Apply validation rules
            if (!validateValue(value, column)) {
                log.warn("Validation failed for column {} with value: {}", 
                        column.getSourceColumnName(), value);
                return null; // Skip this record
            }
            
            // Apply transformation rules
            Object transformedValue = transformValue(value, column);
            record.addColumnValue(column.getSourceColumnName(), transformedValue);
        }
        
        log.debug("Processed record for table: {}", record.getTableName());
        return record;
    }
    
    private boolean validateValue(Object value, ColumnConfig column) {
        String validationRule = column.getValidationRule();
        
        if (validationRule == null || validationRule.isEmpty()) {
            return true;
        }
        
        String stringValue = value != null ? value.toString().trim() : "";
        
        switch (validationRule.toUpperCase()) {
            case "NOT_NULL":
                return !stringValue.isEmpty();
                
            case "NUMERIC":
                try {
                    new BigDecimal(stringValue);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
                
            case "EMAIL":
                return Pattern.matches("^[A-Za-z0-9+_.-]+@(.+)$", stringValue);
                
            case "POSITIVE_NUMBER":
                try {
                    BigDecimal bd = new BigDecimal(stringValue);
                    return bd.compareTo(BigDecimal.ZERO) > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
                
            default:
                // Treat as regex pattern
                return Pattern.matches(validationRule, stringValue);
        }
    }
    
    private Object transformValue(Object value, ColumnConfig column) {
        if (value == null) {
            return column.getDefaultValue();
        }
        
        String transformationRule = column.getTransformationRule();
        
        if (transformationRule == null || transformationRule.isEmpty()) {
            return value;
        }
        
        String stringValue = value.toString();
        
        switch (transformationRule.toUpperCase()) {
            case "UPPER":
                return stringValue.toUpperCase();
                
            case "LOWER":
                return stringValue.toLowerCase();
                
            case "TRIM":
                return stringValue.trim();
                
            case "CAPITALIZE":
                return capitalizeFirstLetter(stringValue);
                
            case "TRIM_UPPER":
                return stringValue.trim().toUpperCase();
                
            default:
                return value;
        }
    }
    
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}