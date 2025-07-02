# Quick Test Script

## Before Deploying - Test Locally

### 1. Build the Application
```bash
mvn clean package -DskipTests
```

### 2. Check if JAR was created
Look for: `target/smart-attendance-backend-1.0.0.jar`

### 3. Test Run (Optional)
```bash
java -jar target/smart-attendance-backend-1.0.0.jar
```

Then visit: http://localhost:8080/api/health

## Ready for Deployment!
If the build succeeds and creates the JAR file, you're ready to deploy to Render!
