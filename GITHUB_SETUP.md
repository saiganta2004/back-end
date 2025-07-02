# Quick GitHub Setup

## If you need to push your code to GitHub:

### Option 1: Create New Repository
1. Go to https://github.com
2. Click "+" → "New repository"
3. Name: `smart-attendance-backend`
4. Make it Public (required for free Render)
5. Don't initialize with README (since you have existing code)
6. Click "Create repository"

### Option 2: Push Your Code
Run these commands in your backend folder:

```bash
# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - Smart Attendance Backend"

# Add your GitHub repository as origin
git remote add origin https://github.com/YOUR_USERNAME/smart-attendance-backend.git

# Push to GitHub
git push -u origin main
```

Replace `YOUR_USERNAME` with your actual GitHub username.
