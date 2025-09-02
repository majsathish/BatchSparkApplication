package com.example.batchspark.model;

import java.util.HashMap;
import java.util.Map;

public class GenericDataRecord {
    
    private String tableName;
    private Map<String, Object> columnValues = new HashMap<>();
    
    public GenericDataRecord() {}
    
    public GenericDataRecord(String tableName) {
        this.tableName = tableName;
    }
    
    public void addColumnValue(String columnName, Object value) {
        columnValues.put(columnName, value);
    }
    
    public Object getColumnValue(String columnName) {
        return columnValues.get(columnName);
    }
    
    public boolean hasColumn(String columnName) {
        return columnValues.containsKey(columnName);
    }
    
    // Getters and Setters
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    
    public Map<String, Object> getColumnValues() { return columnValues; }
    public void setColumnValues(Map<String, Object> columnValues) { this.columnValues = columnValues; }
}