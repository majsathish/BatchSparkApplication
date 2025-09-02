package com.example.batchspark.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "FILE_CONFIG")
public class FileConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_config_seq")
    @SequenceGenerator(name = "file_config_seq", sequenceName = "FILE_CONFIG_SEQ", allocationSize = 1)
    private Long id;
    
    @Column(name = "CONFIG_NAME", unique = true, nullable = false)
    @NotBlank(message = "Configuration name is required")
    private String configName;
    
    @Column(name = "SOURCE_FILE_PATH", nullable = false)
    @NotBlank(message = "Source file path is required")
    private String sourceFilePath;
    
    @Column(name = "TARGET_TABLE_NAME", nullable = false)
    @NotBlank(message = "Target table name is required")
    private String targetTableName;
    
    @Column(name = "DELIMITER")
    private String delimiter = ",";
    
    @Column(name = "HAS_HEADER")
    private Boolean hasHeader = true;
    
    @Column(name = "CHUNK_SIZE")
    private Integer chunkSize = 100;
    
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;
    
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;
    
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;
    
    @OneToMany(mappedBy = "fileConfig", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ColumnConfig> columnConfigs;
    
    // Constructors
    public FileConfig() {}
    
    public FileConfig(String configName, String sourceFilePath, String targetTableName) {
        this.configName = configName;
        this.sourceFilePath = sourceFilePath;
        this.targetTableName = targetTableName;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }
    
    public String getSourceFilePath() { return sourceFilePath; }
    public void setSourceFilePath(String sourceFilePath) { this.sourceFilePath = sourceFilePath; }
    
    public String getTargetTableName() { return targetTableName; }
    public void setTargetTableName(String targetTableName) { this.targetTableName = targetTableName; }
    
    public String getDelimiter() { return delimiter; }
    public void setDelimiter(String delimiter) { this.delimiter = delimiter; }
    
    public Boolean getHasHeader() { return hasHeader; }
    public void setHasHeader(Boolean hasHeader) { this.hasHeader = hasHeader; }
    
    public Integer getChunkSize() { return chunkSize; }
    public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    
    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
    
    public List<ColumnConfig> getColumnConfigs() { return columnConfigs; }
    public void setColumnConfigs(List<ColumnConfig> columnConfigs) { this.columnConfigs = columnConfigs; }
    
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}