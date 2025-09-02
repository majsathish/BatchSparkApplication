# Local Oracle Database Setup Guide

This guide helps you set up Oracle Database locally without Docker.

## Option 1: Oracle Database XE (Recommended for Development)

### Download and Install
1. Go to [Oracle Database XE Downloads](https://www.oracle.com/database/technologies/xe-downloads.html)
2. Download Oracle Database XE for your operating system
3. Run the installer with default settings

### Default Configuration
- **Port**: 1521
- **SID**: XE
- **System Password**: Set during installation
- **Connection URL**: `jdbc:oracle:thin:@localhost:1521:XE`

### Create Application User
1. Connect to Oracle as SYSTEM:
   ```bash
   sqlplus system/your_system_password@localhost:1521/XE
   ```

2. Create user and grant permissions:
   ```sql
   CREATE USER batch_user IDENTIFIED BY batch_password;
   GRANT CONNECT, RESOURCE TO batch_user;
   GRANT CREATE TABLE, CREATE SEQUENCE TO batch_user;
   GRANT UNLIMITED TABLESPACE TO batch_user;
   EXIT;
   ```

## Option 2: Oracle Database Standard Edition

### Download and Install
1. Go to [Oracle Database Downloads](https://www.oracle.com/database/technologies/oracle-database-software-downloads.html)
2. Download Oracle Database 19c or 21c
3. Follow installation wizard
4. Choose "Create and configure a database" option

### Post-Installation Setup
1. Note down the connection details from installation
2. Create application user as shown above
3. Update `application.yml` with your specific connection details

## Option 3: Use Existing Oracle Database

If you have access to an existing Oracle database:

1. Get connection details from your DBA:
   - Host/IP address
   - Port (usually 1521)
   - Service name or SID
   - Username/Password

2. Update `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:oracle:thin:@your-host:1521:your-service
       username: your_username
       password: your_password
   ```

## Verification

Test your Oracle connection:

```bash
# Using SQL*Plus
sqlplus batch_user/batch_password@localhost:1521/XE

# Or using any Oracle client tool like SQL Developer
```

## Troubleshooting

### Common Issues

1. **TNS Listener not running**:
   ```bash
   # Windows
   lsnrctl start
   
   # Linux/Mac
   sudo lsnrctl start
   ```

2. **Database not started**:
   ```bash
   # Connect as SYSDBA and start
   sqlplus / as sysdba
   STARTUP;
   ```

3. **Port 1521 already in use**:
   - Check if another Oracle instance is running
   - Use different port in Oracle configuration

4. **Connection timeout**:
   - Check firewall settings
   - Verify Oracle listener is running
   - Check network connectivity

### Oracle Service Management

**Windows**:
- Services: `OracleServiceXE`, `OracleXETNSListener`
- Start/Stop via Services.msc

**Linux/Mac**:
```bash
# Start Oracle
sudo systemctl start oracle-xe
sudo systemctl start oracle-xe-listener

# Stop Oracle  
sudo systemctl stop oracle-xe
sudo systemctl stop oracle-xe-listener
```

## Memory Requirements

- **Oracle XE**: Minimum 2GB RAM
- **Oracle Standard**: Minimum 4GB RAM
- **Recommended**: 8GB+ RAM for development with Spark

## Alternative: Oracle Cloud Free Tier

If local installation is not feasible:

1. Sign up for [Oracle Cloud Free Tier](https://cloud.oracle.com/free)
2. Create Autonomous Database (Always Free)
3. Download wallet and configure connection
4. Update application.yml with cloud connection details

This gives you a fully managed Oracle database without local installation.