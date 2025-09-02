package com.example.batchspark.repository;

import com.example.batchspark.model.EmployeeAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAnalyticsRepository extends JpaRepository<EmployeeAnalytics, Long> {
}