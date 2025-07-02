# 🚀 Complete Deployment Guide: Render + PlanetScale

## Phase 1: Setup PlanetScale Database (5 minutes)

### 1.1 Create PlanetScale Account
1. Go to https://planetscale.com
2. Click "Sign up" and use GitHub
3. Click "Create database"
4. Name: `smart-attendance`
5. Region: Choose closest to you
6. Click "Create database"

### 1.2 Get Database Credentials
1. In your database dashboard, click "Connect"
2. Select "General" → "Java"
3. Copy the connection details:
   ```
   Host: aws.connect.psdb.cloud
   Database: smart-attendance
   Username: [copy this]
   Password: [copy this]
   ```
4. **Save these credentials** - you'll need them for Render!

## Phase 2: Deploy to Render (10 minutes)

### 2.1 Create Render Account
1. Go to https://render.com
2. Sign up with GitHub
3. Click "New +" → "Web Service"
4. Connect your GitHub repository
5. Select your repository

### 2.2 Configure Render Service
**Service Details:**
- Name: `smart-attendance-backend`
- Environment: `Java`
- Region: Choose same as PlanetScale
- Branch: `main`
- Root Directory: Leave empty (unless backend is in subfolder)

**Build & Deploy:**
- Build Command: `mvn clean package -DskipTests`
- Start Command: `java -jar target/smart-attendance-backend-1.0.0.jar`

### 2.3 Set Environment Variables
Click "Environment" tab and add:

```bash
SPRING_PROFILES_ACTIVE=render
PORT=8080
DATABASE_URL=jdbc:mysql://aws.connect.psdb.cloud/smart-attendance?sslMode=REQUIRE&useSSL=true&serverTimezone=UTC
DB_USERNAME=[your-planetscale-username]
DB_PASSWORD=[your-planetscale-password]
JWT_SECRET=YourVeryLongAndSecureJWTSecretKeyForProduction123456789AbCdEf
FACE_RECOGNITION_URL=http://localhost:5000
CORS_ALLOWED_ORIGINS=http://localhost:3000,https://6864bc010b4af4783a60daa4--smart-attendance-ai.netlify.app,https://smart-attendance-ai.netlify.app
```

### 2.4 Deploy
1. Click "Create Web Service"
2. Wait for build to complete (5-10 minutes)
3. Your app will be available at: `https://smart-attendance-backend-xxxx.onrender.com`

## Phase 3: Test Your Deployment

### 3.1 Health Check
Visit: `https://your-app-url.onrender.com/api/health`

Should return:
```json
{
  "status": "UP",
  "timestamp": 1719915600000,
  "service": "Smart Attendance Backend",
  "version": "1.0.0"
}
```

### 3.2 Test CORS
1. Open your Netlify frontend
2. Open browser DevTools → Network tab
3. Try any API call
4. Should see successful requests to your Render backend

## Phase 4: Update Frontend

In your Netlify frontend code, update the API base URL:
```javascript
// Change from:
const API_BASE_URL = 'http://localhost:8080/api';

// To:
const API_BASE_URL = 'https://your-app-url.onrender.com/api';
```

## 🎉 Success!

Your backend is now:
- ✅ Running on Render (free)
- ✅ Connected to PlanetScale MySQL (free)
- ✅ Configured for your Netlify frontend
- ✅ Ready for production use

## 🔧 Troubleshooting

**Build Fails?**
- Check if `mvnw` file exists and is executable
- Try build command: `mvn clean package -DskipTests`

**Database Connection Issues?**
- Verify PlanetScale credentials
- Check SSL configuration in DATABASE_URL

**CORS Errors?**
- Verify your Netlify URL is in CORS_ALLOWED_ORIGINS
- Check browser console for specific error messages

## 💰 Cost Breakdown
- **Render**: Free (750 hours/month)
- **PlanetScale**: Free (5GB storage)
- **Total**: $0/month 🎉
