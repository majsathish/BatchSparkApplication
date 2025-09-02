package com.example.batchspark.service;

import com.example.batchspark.model.FileConfig;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.spark.sql.functions.*;

@Service
public class SparkService {
    
    private static final Logger log = LoggerFactory.getLogger(SparkService.class);
    
    private SparkSession sparkSession;
    private final JdbcTemplate jdbcTemplate;
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    public SparkService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.sparkSession = SparkSession.builder()
                .appName("GenericDataProcessor")
                .master("local[*]") // Use all available cores
                .config("spark.sql.adaptive.enabled", "true")
                .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .getOrCreate();
        
        sparkSession.sparkContext().setLogLevel("WARN");
    }
    
    public void performGenericAnalytics(FileConfig fileConfig, String outputPath) {
        log.info("Starting Spark analytics for table: {}", fileConfig.getTargetTableName());
        
        try {
            // Read data from Oracle database
            Dataset<Row> dataDF = readFromOracle(fileConfig.getTargetTableName());
            
            if (dataDF.count() == 0) {
                log.warn("No data found in table: {}", fileConfig.getTargetTableName());
                return;
            }
            
            // Create temporary view for SQL queries
            String viewName = fileConfig.getTargetTableName().toLowerCase() + "_view";
            dataDF.createOrReplaceTempView(viewName);
            
            // Perform generic analytics based on data types
            performDataProfiling(dataDF, fileConfig, outputPath);
            performAggregationAnalytics(dataDF, fileConfig, outputPath);
            
            log.info("Completed Spark analytics for table: {}", fileConfig.getTargetTableName());
            
        } catch (Exception e) {
            log.error("Error in Spark analytics processing", e);
            throw new RuntimeException("Spark analytics failed", e);
        }
    }
    
    private Dataset<Row> readFromOracle(String tableName) {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", dbUsername);
        connectionProperties.put("password", dbPassword);
        connectionProperties.put("driver", "oracle.jdbc.OracleDriver");
        
        return sparkSession.read()
                .jdbc(dbUrl, tableName, connectionProperties);
    }
    
    private void performDataProfiling(Dataset<Row> dataDF, FileConfig fileConfig, String outputPath) {
        log.info("Performing data profiling for table: {}", fileConfig.getTargetTableName());
        
        // Basic statistics
        Dataset<Row> summary = dataDF.summary();
        summary.show();
        
        // Save summary statistics
        summary.coalesce(1)
                .write()
                .mode("overwrite")
                .option("header", "true")
                .csv(outputPath + "/" + fileConfig.getTargetTableName() + "_summary");
        
        // Null value analysis
        Map<String, Long> nullCounts = new HashMap<>();
        for (String column : dataDF.columns()) {
            long nullCount = dataDF.filter(col(column).isNull().or(col(column).equalTo(""))).count();
            nullCounts.put(column, nullCount);
        }
        
        log.info("Null counts for table {}: {}", fileConfig.getTargetTableName(), nullCounts);
    }
    
    private void performAggregationAnalytics(Dataset<Row> dataDF, FileConfig fileConfig, String outputPath) {
        log.info("Performing aggregation analytics for table: {}", fileConfig.getTargetTableName());
        
        // Find categorical columns (string types)
        Tuple2<String, String>[] categoricalColumns = dataDF.dtypes();
        
        for (int i = 0; i < categoricalColumns.length; i++) {
            String columnName = categoricalColumns[i]._1();
            String dataType = categoricalColumns[i]._2();
            
            if (dataType.contains("String") && !columnName.equalsIgnoreCase("ID")) {
                // Perform group by analysis for categorical columns
                Dataset<Row> groupAnalysis = dataDF
                        .groupBy(columnName)
                        .agg(count("*").alias("count"))
                        .orderBy(desc("count"));
                
                groupAnalysis.show(20);
                
                // Save group analysis
                groupAnalysis.coalesce(1)
                        .write()
                        .mode("overwrite")
                        .option("header", "true")
                        .csv(outputPath + "/" + fileConfig.getTargetTableName() + "_" + columnName + "_analysis");
            }
        }
        
        // Perform numeric column analysis
        performNumericAnalysis(dataDF, fileConfig, outputPath);
    }
    
    private void performNumericAnalysis(Dataset<Row> dataDF, FileConfig fileConfig, String outputPath) {
        Tuple2<String, String>[] numericColumns = dataDF.dtypes();
        
        for (int i = 0; i < numericColumns.length; i++) {
            String columnName = numericColumns[i]._1();
            String dataType = numericColumns[i]._2();
            
            if ((dataType.contains("Double") || dataType.contains("Integer") || dataType.contains("Long")) 
                && !columnName.equalsIgnoreCase("ID")) {
                
                Dataset<Row> numericStats = dataDF
                        .select(
                                min(columnName).alias("min_" + columnName),
                                max(columnName).alias("max_" + columnName),
                                avg(columnName).alias("avg_" + columnName),
                                stddev(columnName).alias("stddev_" + columnName)
                        );
                
                numericStats.show();
                
                // Save numeric statistics
                numericStats.coalesce(1)
                        .write()
                        .mode("overwrite")
                        .option("header", "true")
                        .csv(outputPath + "/" + fileConfig.getTargetTableName() + "_" + columnName + "_stats");
            }
        }
    }
    
    public void performCrossTableAnalytics(List<String> tableNames, String outputPath) {
        log.info("Performing cross-table analytics for tables: {}", tableNames);
        
        try {
            Map<String, Dataset<Row>> tables = new HashMap<>();
            
            // Load all tables
            for (String tableName : tableNames) {
                Dataset<Row> df = readFromOracle(tableName);
                tables.put(tableName, df);
                df.createOrReplaceTempView(tableName.toLowerCase());
            }
            
            // Perform cross-table analysis (example: join analysis)
            if (tableNames.size() >= 2) {
                String table1 = tableNames.get(0).toLowerCase();
                String table2 = tableNames.get(1).toLowerCase();
                
                // Example join analysis
                Dataset<Row> joinAnalysis = sparkSession.sql(
                    String.format("SELECT COUNT(*) as total_records FROM %s t1 " +
                                 "LEFT JOIN %s t2 ON t1.id = t2.id", table1, table2)
                );
                
                joinAnalysis.show();
                
                joinAnalysis.coalesce(1)
                        .write()
                        .mode("overwrite")
                        .option("header", "true")
                        .csv(outputPath + "/cross_table_analysis");
            }
            
        } catch (Exception e) {
            log.error("Error in cross-table analytics", e);
        }
    }
    
    @PreDestroy
    public void shutdown() {
        if (sparkSession != null) {
            sparkSession.stop();
        }
    }
}