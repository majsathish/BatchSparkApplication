package com.example.batchspark.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "COLUMN_CONFIG")
public class ColumnConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "column_config_seq")
    @SequenceGenerator(name = "column_config_seq", sequenceName = "COLUMN_CONFIG_SEQ", allocationSize = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_CONFIG_ID", nullable = false)
    @NotNull(message = "File config is required")
    private FileConfig fileConfig;
    
    @Column(name = "SOURCE_COLUMN_NAME", nullable = false)
    @NotBlank(message = "Source column name is required")
    private String sourceColumnName;
    
    @Column(name = "TARGET_COLUMN_NAME", nullable = false)
    @NotBlank(message = "Target column name is required")
    private String targetColumnName;
    
    @Column(name = "DATA_TYPE", nullable = false)
    @NotBlank(message = "Data type is required")
    private String dataType; // VARCHAR2, NUMBER, DATE, TIMESTAMP, etc.
    
    @Column(name = "MAX_LENGTH")
    private Integer maxLength;
    
    @Column(name = "IS_NULLABLE")
    private Boolean isNullable = true;
    
    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;
    
    @Column(name = "COLUMN_ORDER", nullable = false)
    @NotNull(message = "Column order is required")
    private Integer columnOrder;
    
    @Column(name = "TRANSFORMATION_RULE")
    private String transformationRule; // UPPER, LOWER, TRIM, etc.
    
    @Column(name = "VALIDATION_RULE")
    private String validationRule; // NOT_NULL, REGEX, RANGE, etc.
    
    @Column(name = "IS_PRIMARY_KEY")
    private Boolean isPrimaryKey = false;
    
    // Constructors
    public ColumnConfig() {}
    
    public ColumnConfig(String sourceColumnName, String targetColumnName, String dataType, Integer columnOrder) {
        this.sourceColumnName = sourceColumnName;
        this.targetColumnName = targetColumnName;
        this.dataType = dataType;
        this.columnOrder = columnOrder;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public FileConfig getFileConfig() { return fileConfig; }
    public void setFileConfig(FileConfig fileConfig) { this.fileConfig = fileConfig; }
    
    public String getSourceColumnName() { return sourceColumnName; }
    public void setSourceColumnName(String sourceColumnName) { this.sourceColumnName = sourceColumnName; }
    
    public String getTargetColumnName() { return targetColumnName; }
    public void setTargetColumnName(String targetColumnName) { this.targetColumnName = targetColumnName; }
    
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    
    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
    
    public Boolean getIsNullable() { return isNullable; }
    public void setIsNullable(Boolean isNullable) { this.isNullable = isNullable; }
    
    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    
    public Integer getColumnOrder() { return columnOrder; }
    public void setColumnOrder(Integer columnOrder) { this.columnOrder = columnOrder; }
    
    public String getTransformationRule() { return transformationRule; }
    public void setTransformationRule(String transformationRule) { this.transformationRule = transformationRule; }
    
    public String getValidationRule() { return validationRule; }
    public void setValidationRule(String validationRule) { this.validationRule = validationRule; }
    
    public Boolean getIsPrimaryKey() { return isPrimaryKey; }
    public void setIsPrimaryKey(Boolean isPrimaryKey) { this.isPrimaryKey = isPrimaryKey; }
}