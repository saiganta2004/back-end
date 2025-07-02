#!/bin/bash
# Build script for Render.com

echo "Building Smart Attendance Backend..."

# Clean and package the application
mvn clean package -DskipTests

echo "Build completed successfully!"
