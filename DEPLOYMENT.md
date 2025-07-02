# Smart Attendance Backend Deployment Guide

## Current Status
Your backend is configured for local development but needs modifications for production deployment to work with your Netlify frontend.

## Key Changes Made

### 1. CORS Configuration Updated
- Added your Netlify URL to allowed origins
- Configured for both preview and production Netlify URLs
- Environment variable support for flexible CORS configuration

### 2. Environment Variables Support
- All configurations now use environment variables
- Separate production profile created
- Database URL, credentials, and other settings externalized

### 3. Health Check Endpoint
- Added `/api/health` endpoint for monitoring
- Helps with deployment health checks

## Deployment Options

### Option 1: Railway (Recommended)
1. Create account at railway.app
2. Connect your GitHub repository
3. Set environment variables from `.env.example`
4. Deploy automatically

### Option 2: Heroku
1. Create Heroku app
2. Add MySQL addon (JawsDB or ClearDB)
3. Set environment variables
4. Deploy using Git

### Option 3: Docker Deployment
```bash
# Build the jar file
mvn clean package

# Build Docker image
docker build -t smart-attendance-backend .

# Run with docker-compose (includes MySQL)
docker-compose up -d
```

## Required Environment Variables for Production

```env
# Database (Use cloud database like PlanetScale, AWS RDS, etc.)
DATABASE_URL=jdbc:mysql://your-host:3306/smart_attendance?useSSL=true&serverTimezone=UTC
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Security
JWT_SECRET=YourVeryLongAndSecureJWTSecretKey

# CORS (Your Netlify URLs)
CORS_ALLOWED_ORIGINS=https://6864bc010b4af4783a60daa4--smart-attendance-ai.netlify.app,https://smart-attendance-ai.netlify.app
```

## Database Setup

### Cloud Database Options:
1. **PlanetScale** (MySQL, Free tier available)
2. **AWS RDS** (MySQL)
3. **Google Cloud SQL** (MySQL)
4. **Digital Ocean Managed Databases**

## Face Recognition Service
The current configuration points to `localhost:5000` which won't work in production. You need to:
1. Deploy the Python face recognition service separately
2. Update `FACE_RECOGNITION_URL` environment variable
3. Consider using cloud services like AWS Rekognition or Azure Face API

## Testing Deployment

1. **Health Check**: `GET https://your-backend-url/api/health`
2. **CORS Test**: Check browser console for CORS errors
3. **Authentication**: Test login/register endpoints

## Next Steps

1. Choose a deployment platform
2. Set up a cloud database
3. Deploy the face recognition service
4. Configure environment variables
5. Update your frontend API URLs to point to the deployed backend

## Important Notes

- Never commit real credentials to Git
- Use strong JWT secrets in production
- Enable SSL/HTTPS for production
- Monitor your application logs
- Set up database backups
