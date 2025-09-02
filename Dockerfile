FROM openjdk:24-jre-slim

WORKDIR /app

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the jar file
COPY target/batch-spark-demo-1.0.0.jar app.jar

# Create directories
RUN mkdir -p /app/output /app/logs

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application with Java 24 preview features
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]