package com.example.batchspark.service;

import com.example.batchspark.model.ColumnConfig;
import com.example.batchspark.model.FileConfig;
import com.example.batchspark.repository.FileConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigurationService {
    
    private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);
    
    private final FileConfigRepository fileConfigRepository;
    
    public ConfigurationService(FileConfigRepository fileConfigRepository) {
        this.fileConfigRepository = fileConfigRepository;
    }
    
    @Transactional
    public FileConfig createFileConfig(String configName, String sourceFilePath, 
                                     String targetTableName, List<ColumnConfig> columns) {
        FileConfig fileConfig = new FileConfig();
        fileConfig.setConfigName(configName);
        fileConfig.setSourceFilePath(sourceFilePath);
        fileConfig.setTargetTableName(targetTableName);
        fileConfig.setDelimiter(",");
        fileConfig.setHasHeader(true);
        fileConfig.setChunkSize(100);
        fileConfig.setIsActive(true);
        fileConfig.setCreatedDate(LocalDateTime.now());
        fileConfig.setUpdatedDate(LocalDateTime.now());
        
        // Set file config reference for columns
        for (ColumnConfig column : columns) {
            column.setFileConfig(fileConfig);
        }
        fileConfig.setColumnConfigs(columns);
        
        FileConfig saved = fileConfigRepository.save(fileConfig);
        log.info("Created file configuration: {} with {} columns", configName, columns.size());
        
        return saved;
    }
    
    public Optional<FileConfig> getFileConfig(String configName) {
        return fileConfigRepository.findByConfigNameWithColumns(configName);
    }
    
    public List<FileConfig> getAllActiveConfigs() {
        return fileConfigRepository.findByIsActiveTrue();
    }
    
    @Transactional
    public void deactivateConfig(String configName) {
        Optional<FileConfig> configOpt = fileConfigRepository.findByConfigNameAndIsActiveTrue(configName);
        if (configOpt.isPresent()) {
            FileConfig config = configOpt.get();
            config.setIsActive(false);
            config.setUpdatedDate(LocalDateTime.now());
            fileConfigRepository.save(config);
            log.info("Deactivated configuration: {}", configName);
        }
    }
}