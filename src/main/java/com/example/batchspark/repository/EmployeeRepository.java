package com.example.batchspark.repository;

import com.example.batchspark.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    @Query("SELECT DISTINCT e.department FROM Employee e")
    List<String> findDistinctDepartments();
    
    List<Employee> findByDepartment(String department);
}