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

import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.apache.spark.sql.functions.*;

@Service
public class ModernSparkService {
    
    private static final Logger log = LoggerFactory.getLogger(ModernSparkService.class);
    
    private SparkSession sparkSession;
    private final JdbcTemplate jdbcTemplate;
    
    @Value("${spring.datasource.url}")
    private String dbUrl;
    
    @Value("${spring.datasource.username}")
    private String dbUsername;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    public ModernSparkService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        initializeSparkSession();
    }
    
    private void initializeSparkSession() {
        this.sparkSession = SparkSession.builder()
                .appName("ModernGenericDataProcessor")
                .master("local[*]")
                .config("spark.sql.adaptive.enabled", "true")
                .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
                .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .config("spark.sql.execution.arrow.pyspark.enabled", "true")
                .config("spark.sql.adaptive.skewJoin.enabled", "true")
                .getOrCreate();
        
        sparkSession.sparkContext().setLogLevel("WARN");
        log.info("Spark session initialized with version: {}", sparkSession.version());
    }
    
    public void performAdvancedAnalytics(FileConfig fileConfig, String outputPath) {
        log.info("Starting advanced Spark analytics for table: {}", fileConfig.getTargetTableName());
        
        try {
            Dataset<Row> dataDF = readFromOracle(fileConfig.getTargetTableName());
            
            if (dataDF.count() == 0) {
                log.warn("No data found in table: {}", fileConfig.getTargetTableName());
                return;
            }
            
            // Create temporary view
            String viewName = fileConfig.getTargetTableName().toLowerCase() + "_view";
            dataDF.createOrReplaceTempView(viewName);
            
            // Perform comprehensive analytics
            performDataQualityAnalysis(dataDF, fileConfig, outputPath);
            performStatisticalAnalysis(dataDF, fileConfig, outputPath);
            performPatternAnalysis(dataDF, fileConfig, outputPath);
            
            log.info("Completed advanced analytics for table: {}", fileConfig.getTargetTableName());
            
        } catch (Exception e) {
            log.error("Error in advanced analytics processing", e);
            throw new RuntimeException("Advanced analytics failed", e);
        }
    }
    
    private Dataset<Row> readFromOracle(String tableName) {
        Properties connectionProperties = new Properties();
        connectionProperties.put("user", dbUsername);
        connectionProperties.put("password", dbPassword);
        connectionProperties.put("driver", "oracle.jdbc.OracleDriver");
        connectionProperties.put("fetchsize", "10000");
        
        return sparkSession.read()
                .jdbc(dbUrl, tableName, connectionProperties);
    }
    
    private void performDataQualityAnalysis(Dataset<Row> dataDF, FileConfig fileConfig, String outputPath) {
        log.info("Performing data quality analysis for table: {}", fileConfig.getTargetTableName());
        
        // Null analysis with modern Spark functions
        Dataset<Row> nullAnalysis = dataDF.select(
            dataDF.columns().length > 0 ? 
                expr("stack(" + dataDF.columns().length + ", " +
                    String.join(", ", 
                        java.util.Arrays.stream(dataDF.columns())
                            .map(col -> "'" + col + "', sum(case when " + col + " is null then 1 else 0 end)")
                            .toArray(String[]::new)
                    ) + ") as (column_name, null_count)")
                : lit(null).as("dummy")
        );
        
        nullAnalysis.show();
        
        // Save null analysis
        nullAnalysis.coalesce(1)
                .write()
                .mode("overwrite")
                .option("header", "true")
                .csv(outputPath + "/" + fileConfig.getTargetTableName() + "_null_analysis");
        
        // Duplicate analysis
        long totalRows = dataDF.count();
        long distinctRows = dataDF.distinct().count();
        long duplicateRows = totalRows - distinctRows;
        
        Dataset<Row> duplicateAnalysis = sparkSession.createDataFrame(
            List.of(
                sparkSession.sparkContext().parallelize(List.of(
                    org.apache.spark.sql.RowFactory.create("total_rows", totalRows),
                    org.apache.spark.sql.RowFactory.create("distinct_rows", distinctRows),
                    org.apache.spark.sql.RowFactory.create("duplicate_rows", duplicateRows)
                ), 1)
            ).get(0),
            org.apache.spark.sql.types.DataTypes.createStructType(
                new org.apache.spark.sql.types.StructField[]{
                    org.apache.spark.sql.types.DataTypes.createStructField("metric", org.apache.spark.sql.types.DataTypes.StringType, false),
                    org.apache.spark.sql.types.DataTypes.createStructField("value", org.apache.spark.sql.types.DataTypes.LongType, false)
                }
            )
        );
        
        duplicateAnalysis.coalesce(1)
                .write()
                .mode("overwrite")
                .option("header", "true")
                .csv(outputPath + "/" + fileConfig.getTargetTableName() + "_duplicate_analysis");
    }
    
    private void performStatisticalAnalysis(Dataset<Row> dataDF, FileConfig fileConfig, String outputPath) {
        log.info("Performing statistical analysis for table: {}", fileConfig.getTargetTableName());
        
        // Enhanced statistical summary
        Dataset<Row> enhancedSummary = dataDF.summary("count", "mean", "stddev", "min", "25%", "50%", "75%", "max");
        enhancedSummary.show();
        
        enhancedSummary.coalesce(1)
                .write()
                .mode("overwrite")
                .option("header", "true")
                .csv(outputPath + "/" + fileConfig.getTargetTableName() + "_enhanced_summary");
        
        // Correlation analysis for numeric columns
        String[] numericColumns = java.util.Arrays.stream(dataDF.dtypes())
                .filter(tuple -> tuple._2().contains("Double") || tuple._2().contains("Integer") || tuple._2().contains("Long"))
                .map(tuple -> tuple._1())
                .toArray(String[]::new);
        
        if (numericColumns.length > 1) {
            for (int i = 0; i < numericColumns.length; i++) {
                for (int j = i + 1; j < numericColumns.length; j++) {
                    double correlation = dataDF.stat().corr(numericColumns[i], numericColumns[j]);
                    log.info("Correlation between {} and {}: {}", numericColumns[i], numericColumns[j], correlation);
                }
            }
        }
    }
    
    private void performPatternAnalysis(Dataset<Row> dataDF, FileConfig fileConfig, String outputPath) {
        log.info("Performing pattern analysis for table: {}", fileConfig.getTargetTableName());
        
        // String pattern analysis
        String[] stringColumns = java.util.Arrays.stream(dataDF.dtypes())
                .filter(tuple -> tuple._2().contains("String"))
                .map(tuple -> tuple._1())
                .toArray(String[]::new);
        
        for (String column : stringColumns) {
            Dataset<Row> patternAnalysis = dataDF
                    .select(
                        col(column),
                        length(col(column)).alias("length"),
                        regexp_extract(col(column), "^[A-Za-z]+", 0).alias("alpha_prefix"),
                        regexp_extract(col(column), "[0-9]+", 0).alias("numeric_part")
                    )
                    .groupBy("length", "alpha_prefix")
                    .count()
                    .orderBy(desc("count"));
            
            patternAnalysis.coalesce(1)
                    .write()
                    .mode("overwrite")
                    .option("header", "true")
                    .csv(outputPath + "/" + fileConfig.getTargetTableName() + "_" + column + "_patterns");
        }
    }
    
    public void performMLReadyDataPreparation(List<String> tableNames, String outputPath) {
        log.info("Preparing ML-ready datasets for tables: {}", tableNames);
        
        try {
            Map<String, Dataset<Row>> tables = new HashMap<>();
            
            // Load all tables
            for (String tableName : tableNames) {
                Dataset<Row> df = readFromOracle(tableName);
                tables.put(tableName, df);
                df.createOrReplaceTempView(tableName.toLowerCase());
            }
            
            // Feature engineering example
            if (tableNames.size() >= 2) {
                String table1 = tableNames.get(0).toLowerCase();
                String table2 = tableNames.get(1).toLowerCase();
                
                // Create feature-rich dataset
                Dataset<Row> featureDataset = sparkSession.sql(
                    String.format("""
                        SELECT 
                            t1.*,
                            t2.*,
                            CASE WHEN t1.id IS NOT NULL AND t2.id IS NOT NULL THEN 1 ELSE 0 END as has_relationship
                        FROM %s t1 
                        FULL OUTER JOIN %s t2 ON t1.id = t2.id
                        """, table1, table2)
                );
                
                featureDataset.coalesce(1)
                        .write()
                        .mode("overwrite")
                        .option("header", "true")
                        .parquet(outputPath + "/ml_ready_dataset.parquet");
            }
            
        } catch (Exception e) {
            log.error("Error in ML data preparation", e);
        }
    }
    
    @PreDestroy
    public void shutdown() {
        if (sparkSession != null) {
            sparkSession.stop();
            log.info("Spark session stopped");
        }
    }
}