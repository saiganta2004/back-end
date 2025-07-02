# Railway Deployment Guide

## Quick Deploy to Railway:

1. **Go to https://railway.app and sign up**
2. **Click 'New Project' > 'Deploy from GitHub repo'**
3. **Connect your GitHub repository**
4. **Railway will auto-detect it's a Java/Spring Boot app**
5. **Set these environment variables in Railway:**

```
DATABASE_URL=railway_will_provide_mysql_url
DB_USERNAME=railway_generated_username  
DB_PASSWORD=railway_generated_password
JWT_SECRET=your_secure_jwt_secret_here_make_it_long_and_random
SPRING_PROFILES_ACTIVE=prod
FACE_RECOGNITION_URL=your_python_face_service_url
PORT=8080
```

6. **Railway will provide you with a URL like:** `https://your-app-name.railway.app`

## After Deployment:

1. **Test your backend:** Visit `https://your-app-name.railway.app/api/health`
2. **Update your frontend:** Change API base URL from localhost to your Railway URL
3. **Your Netlify frontend will now connect to your deployed backend!**

## Alternative: Render.com

If Railway doesn't work, try Render.com:
1. Go to render.com
2. Create new Web Service from GitHub
3. Use these settings:
   - Build Command: `./mvnw clean package -DskipTests`
   - Start Command: `java -jar target/smart-attendance-backend-1.0.0.jar`
   - Environment: Same variables as above
