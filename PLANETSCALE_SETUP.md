# PlanetScale Setup Guide

## Step 1: Create PlanetScale Account
1. Go to https://planetscale.com
2. Sign up with GitHub (recommended)
3. Click "Create database"
4. Database name: `smart-attendance`
5. Region: Choose closest to you (e.g., US East, Europe)
6. Click "Create database"

## Step 2: Get Connection Details
1. In your database dashboard, click "Connect"
2. Select "Java" 
3. Copy the connection string (looks like):
   ```
   jdbc:mysql://aws.connect.psdb.cloud/smart-attendance?sslMode=REQUIRE
   ```
4. Note down:
   - Host: aws.connect.psdb.cloud
   - Database: smart-attendance
   - Username: (will be provided)
   - Password: (will be provided)

## Step 3: Create Database Schema
PlanetScale will automatically create tables when your app starts (thanks to hibernate.ddl-auto=update)

## Important Notes:
- Free tier: 5GB storage, 1 billion row reads/month
- SSL is required (we'll configure this)
- No localhost access (cloud only)
