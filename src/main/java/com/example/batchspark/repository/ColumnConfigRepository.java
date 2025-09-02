package com.example.batchspark.repository;

import com.example.batchspark.model.ColumnConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnConfigRepository extends JpaRepository<ColumnConfig, Long> {
    
    List<ColumnConfig> findByFileConfigIdOrderByColumnOrder(Long fileConfigId);
}