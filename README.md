# Modern Generic Spring Batch + Spark Hybrid Data Processing

This application demonstrates a modern, configurable hybrid approach using **Java 21** and **Spring Boot 3.3.5** combining Spring Batch for ETL operations with Apache Spark for big data analytics. The system can process any CSV file based on configuration stored in Oracle database tables.

## Modern Features

- **Java 21**: LTS Java version with modern features and excellent performance
- **Spring Boot 3.3.5**: Stable Spring Boot version with enhanced performance and security
- **Modern Spring Batch**: New JobBuilder and StepBuilder APIs (no more deprecated factories)
- **Jakarta EE**: Full migration from javax to jakarta namespace
- **Generic Processing**: Configurable table and column mappings via database configuration
- **Advanced Spark Analytics**: Enhanced with ML-ready data preparation and statistical analysis
- **Oracle Database**: Production-ready database with latest JDBC drivers
- **Parallel Processing**: Multi-threaded Spring Batch steps with modern TaskExecutor
- **REST API**: Modern REST endpoints with validation and actuator integration

## Architecture

1. **Configuration Management**: File and column configurations stored in Oracle tables
2. **Data Ingestion**: Spring Batch reads CSV files based on configuration
3. **Data Processing**: Validates and transforms data according to column rules
4. **Data Loading**: Saves processed data to dynamically created Oracle tables
5. **Analytics**: Spark processes the data for comprehensive analytics
6. **Results Storage**: Analytics results saved to output files

## Configuration Tables

### FILE_CONFIG
| Column | Type | Description |
|--------|------|-------------|
| CONFIG_NAME | VARCHAR2(100) | Unique configuration identifier |
| SOURCE_FILE_PATH | VARCHAR2(500) | Path to source CSV file |
| TARGET_TABLE_NAME | VARCHAR2(100) | Target Oracle table name |
| DELIMITER | VARCHAR2(10) | CSV delimiter (default: ',') |
| HAS_HEADER | NUMBER(1) | Whether CSV has header row |
| CHUNK_SIZE | NUMBER(10) | Batch processing chunk size |
| IS_ACTIVE | NUMBER(1) | Configuration active status |

### COLUMN_CONFIG
| Column | Type | Description |
|--------|------|-------------|
| SOURCE_COLUMN_NAME | VARCHAR2(100) | CSV column name |
| TARGET_COLUMN_NAME | VARCHAR2(100) | Database column name |
| DATA_TYPE | VARCHAR2(50) | Oracle data type |
| MAX_LENGTH | NUMBER(10) | Maximum column length |
| TRANSFORMATION_RULE | VARCHAR2(100) | Data transformation rule |
| VALIDATION_RULE | VARCHAR2(255) | Data validation rule |
| COLUMN_ORDER | NUMBER(10) | Column processing order |

## Transformation Rules

- **UPPER**: Convert to uppercase
- **LOWER**: Convert to lowercase  
- **TRIM**: Remove leading/trailing spaces
- **CAPITALIZE**: Capitalize first letter
- **TRIM_UPPER**: Trim and convert to uppercase

## Validation Rules

- **NOT_NULL**: Field cannot be empty
- **NUMERIC**: Must be a valid number
- **POSITIVE_NUMBER**: Must be positive number
- **EMAIL**: Must be valid email format
- **Custom Regex**: Any regex pattern

## Database Setup (Local Oracle Installation)

1. **Install Oracle Database**:
   - Download Oracle Database XE (Express Edition) from Oracle website
   - Install Oracle XE on your local machine
   - Default installation creates database on port 1521

2. **Create Database User**:
   Connect to Oracle as SYSTEM user and run:
   ```sql
   CREATE USER batch_user IDENTIFIED BY batch_password;
   GRANT CONNECT, RESOURCE, CREATE TABLE, CREATE SEQUENCE TO batch_user;
   GRANT UNLIMITED TABLESPACE TO batch_user;
   ```

3. **Update application.yml** (if different from defaults):
   ```yaml
   spring:
     datasource:
       url: jdbc:oracle:thin:@localhost:1521:XE
       username: batch_user
       password: batch_password
   ```

4. **Alternative: Use Existing Oracle Database**:
   If you have an existing Oracle database, just update the connection details:
   ```yaml
   spring:
     datasource:
       url: jdbc:oracle:thin:@your-oracle-host:1521:your-sid
       username: your_username
       password: your_password
   ```

## Running the Application

### Prerequisites
- **Java 21** (LTS version recommended for production)
- **Maven 3.8+** (for Java 21 support)
- **Oracle Database 19c+** (local installation or remote access)

### Steps

1. **Setup Oracle Database** (if not already done):
   - Install Oracle Database XE locally, OR
   - Get access to existing Oracle database
   - Create user and grant permissions as shown above

2. **Clone and configure**:
   ```bash
   git clone <repository>
   cd batch-spark-demo
   ```

3. **Update database connection** (if needed):
   Edit `src/main/resources/application.yml` with your Oracle connection details

4. **Build the project**:
   ```bash
   mvn clean compile
   ```

5. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
   
   The application will:
   - Connect to Oracle database
   - Create configuration tables automatically
   - Insert sample configurations
   - Start on port 8080

6. **Verify setup**:
   ```bash
   curl -X GET http://localhost:8080/api/batch/configs
   ```

7. **Trigger batch processing**:
   ```bash
   curl -X POST http://localhost:8080/api/batch/start/EMPLOYEE_DATA
   ```

## Sample Configurations

The application comes with two pre-configured examples:

### Employee Data Configuration
- **Config Name**: EMPLOYEE_DATA
- **Target Table**: EMPLOYEES
- **Columns**: employeeId, firstName, lastName, department, salary, hireDate

### Product Data Configuration  
- **Config Name**: PRODUCT_DATA
- **Target Table**: PRODUCTS
- **Columns**: productId, productName, category, price, quantity, createdDate

## Analytics Generated

The Spark engine automatically generates:

1. **Data Profiling**:
   - Summary statistics for all columns
   - Null value analysis
   - Data type distribution

2. **Categorical Analysis**:
   - Group by analysis for string columns
   - Count distributions
   - Top values analysis

3. **Numeric Analysis**:
   - Min, max, average, standard deviation
   - Statistical summaries
   - Outlier detection

4. **Cross-Table Analysis**:
   - Join analysis between tables
   - Relationship mapping
   - Data consistency checks

## Performance Features

- **Multi-threading**: Spring Batch uses configurable thread pools
- **Spark Optimization**: Uses all available CPU cores with adaptive query execution
- **Oracle Connection Pooling**: HikariCP for optimal database performance
- **Batch Processing**: Configurable chunk sizes per file type
- **Memory Management**: Spark handles large datasets efficiently

## Adding New Configurations

1. **Insert File Configuration**:
   ```sql
   INSERT INTO FILE_CONFIG (ID, CONFIG_NAME, SOURCE_FILE_PATH, TARGET_TABLE_NAME, DELIMITER, HAS_HEADER, CHUNK_SIZE, IS_ACTIVE)
   VALUES (FILE_CONFIG_SEQ.NEXTVAL, 'SALES_DATA', '/data/sales.csv', 'SALES', ',', 1, 200, 1);
   ```

2. **Insert Column Configurations**:
   ```sql
   INSERT INTO COLUMN_CONFIG (ID, FILE_CONFIG_ID, SOURCE_COLUMN_NAME, TARGET_COLUMN_NAME, DATA_TYPE, COLUMN_ORDER, TRANSFORMATION_RULE, VALIDATION_RULE)
   VALUES (COLUMN_CONFIG_SEQ.NEXTVAL, [file_config_id], 'saleId', 'SALE_ID', 'VARCHAR2', 1, 'TRIM_UPPER', 'NOT_NULL');
   ```

3. **Trigger Processing**:
   ```bash
   curl -X POST http://localhost:8080/api/batch/start/SALES_DATA
   ```

## Production Considerations

- **Oracle RAC**: Configure for high availability
- **Spark Cluster**: Deploy on cluster for large datasets
- **Monitoring**: Add JMX metrics and logging
- **Security**: Implement authentication and authorization
- **Backup**: Regular database and configuration backups

## Modern Technology Stack

- **Java 21** (LTS version for production stability)
- **Spring Boot 3.3.5** (stable production version)
- **Spring Batch 5.x** with modern JobBuilder/StepBuilder APIs
- **Apache Spark 3.5.0** with enhanced performance
- **Oracle JDBC 23c** (ojdbc11) driver
- **Jakarta EE 10** (full migration from javax)
- **HikariCP** latest connection pooling
- **Spring Boot Actuator** for monitoring and health checks
- **No Lombok dependency** - Uses standard Java code for better IDE compatibility and debugging

## Code Quality Features

- **Standard Java Code**: All classes use standard Java getters, setters, and constructors instead of Lombok annotations
- **Better IDE Support**: Full IDE support for debugging, refactoring, and code navigation
- **Explicit Code**: All methods are explicitly written for better maintainability
- **No Magic**: No annotation processing or code generation - what you see is what you get
- **Java 21 LTS**: Uses Long Term Support version for production stability and compatibility

## Version Compatibility

This project has been optimized for:
- **Java 21 LTS**: Provides excellent performance and long-term support
- **Spring Boot 3.3.5**: Stable production version with proven reliability
- **Maven 3.8+**: Compatible with Java 21 and modern build practices
- **Oracle Database 19c+**: Modern database features with excellent JDBC support