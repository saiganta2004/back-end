# Render.com Deployment Guide

## Step 1: Prepare Your Repository
1. Make sure your code is pushed to GitHub
2. Ensure the following files are in your backend folder:
   - `pom.xml`
   - `src/` directory
   - `application-render.yml` (created)

## Step 2: Create Render Account
1. Go to https://render.com
2. Sign up with GitHub
3. Click "New +" → "Web Service"
4. Connect your GitHub repository
5. Select your repository and branch

## Step 3: Configure Render Settings
**Build Settings:**
- Name: `smart-attendance-backend`
- Environment: `Java`
- Region: Choose closest to you
- Branch: `main` (or your default branch)
- Root Directory: `backend` (if your backend is in a subfolder)

**Build Command:**
```
mvn clean package -DskipTests
```

**Start Command:**
```
java -jar target/smart-attendance-backend-1.0.0.jar
```

## Step 4: Set Environment Variables
In Render dashboard, add these environment variables:

```
SPRING_PROFILES_ACTIVE=render
PORT=8080
DATABASE_URL=jdbc:mysql://aws.connect.psdb.cloud/smart-attendance?sslMode=REQUIRE&useSSL=true&serverTimezone=UTC
DB_USERNAME=<your-planetscale-username>
DB_PASSWORD=<your-planetscale-password>
JWT_SECRET=YourVeryLongAndSecureJWTSecretKeyForProduction123456789AbCdEf
FACE_RECOGNITION_URL=http://localhost:5000
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://6864bc010b4af4783a60daa4--smart-attendance-ai.netlify.app,https://smart-attendance-ai.netlify.app
```

## Step 5: Deploy
1. Click "Create Web Service"
2. Render will automatically build and deploy
3. You'll get a URL like: `https://smart-attendance-backend-xxxx.onrender.com`

## Step 6: Test Deployment
Visit: `https://your-app-url.onrender.com/api/health`
Should return: `{"status":"UP","timestamp":...}`
