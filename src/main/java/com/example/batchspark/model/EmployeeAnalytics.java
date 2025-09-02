package com.example.batchspark.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "employee_analytics")
public class EmployeeAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "employee_count")
    private Long employeeCount;
    
    @Column(name = "avg_salary")
    private BigDecimal avgSalary;
    
    @Column(name = "min_salary")
    private BigDecimal minSalary;
    
    @Column(name = "max_salary")
    private BigDecimal maxSalary;
    
    @Column(name = "total_salary")
    private BigDecimal totalSalary;
    
    // Default constructor
    public EmployeeAnalytics() {}
    
    // All args constructor
    public EmployeeAnalytics(Long id, String department, Long employeeCount, 
                           BigDecimal avgSalary, BigDecimal minSalary, 
                           BigDecimal maxSalary, BigDecimal totalSalary) {
        this.id = id;
        this.department = department;
        this.employeeCount = employeeCount;
        this.avgSalary = avgSalary;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.totalSalary = totalSalary;
    }
    
    // Constructor without ID
    public EmployeeAnalytics(String department, Long employeeCount, 
                           BigDecimal avgSalary, BigDecimal minSalary, 
                           BigDecimal maxSalary, BigDecimal totalSalary) {
        this.department = department;
        this.employeeCount = employeeCount;
        this.avgSalary = avgSalary;
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
        this.totalSalary = totalSalary;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Long getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Long employeeCount) { this.employeeCount = employeeCount; }
    
    public BigDecimal getAvgSalary() { return avgSalary; }
    public void setAvgSalary(BigDecimal avgSalary) { this.avgSalary = avgSalary; }
    
    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }
    
    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }
    
    public BigDecimal getTotalSalary() { return totalSalary; }
    public void setTotalSalary(BigDecimal totalSalary) { this.totalSalary = totalSalary; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeAnalytics that = (EmployeeAnalytics) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(department, that.department);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, department);
    }
    
    @Override
    public String toString() {
        return "EmployeeAnalytics{" +
                "id=" + id +
                ", department='" + department + '\'' +
                ", employeeCount=" + employeeCount +
                ", avgSalary=" + avgSalary +
                ", minSalary=" + minSalary +
                ", maxSalary=" + maxSalary +
                ", totalSalary=" + totalSalary +
                '}';
    }
}