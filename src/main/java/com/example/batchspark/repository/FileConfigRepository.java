package com.example.batchspark.repository;

import com.example.batchspark.model.FileConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileConfigRepository extends JpaRepository<FileConfig, Long> {
    
    Optional<FileConfig> findByConfigNameAndIsActiveTrue(String configName);
    
    List<FileConfig> findByIsActiveTrue();
    
    @Query("SELECT fc FROM FileConfig fc LEFT JOIN FETCH fc.columnConfigs cc " +
           "WHERE fc.configName = :configName AND fc.isActive = true " +
           "ORDER BY cc.columnOrder")
    Optional<FileConfig> findByConfigNameWithColumns(@Param("configName") String configName);
}