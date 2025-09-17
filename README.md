# 📖 API Documentation

---

## 🌐 Overview
This documentation provides details about available API endpoints.  
It covers the following features:  
👤 User Management, 🔑 Authentication, 📝 Problem Management, 🎖️ Badge Management, 📦 Package Management, 🖼️ Media Management, 📤 Code Submission, 💬 Comments, 💡 Hints, 🏆 Leaderboard, and ⚙️ Roles.

---

## 🚀 Endpoints

### 👤 User Management
- [GET] `/api/v1/users/me` – Get current user profile
- [GET] `/api/v1/users/search` – Search users by username or email
- [PATCH] `/api/v1/users/update/{id}` – Update a user by ID
- [DELETE] `/api/v1/users/delete/elastic/{id}` – Delete a user by ID

### 🔑 Authentication
- [POST] `/api/v1/auth/token` – Refresh access tokens
- [POST] `/api/v1/auth/login` – Login to get access tokens
- [POST] `/api/v1/auth/register` – Register a new account
- [POST] `/api/v1/auth/reset-password` – Reset user password via email

### 📝 Problem Management
- [GET] `/api/v1/problems` – Get all problems (verified + unverified)
- [POST] `/api/v1/problems` – Create a new problem
- [GET] `/api/v1/problems/{problemId}` – Get problem by ID
- [PATCH] `/api/v1/problems/{problemId}` – Update problem by ID
- [PUT] `/api/v1/problems/{problemId}/verification` – Verify problem
- [GET] `/api/v1/problems/{problemId}/me` – Get problem for specific user
- [GET] `/api/v1/problems/verified` – Get all verified problems
- [GET] `/api/v1/problems/unverified` – Get all unverified problems
- [GET] `/api/v1/problems/search` – Search problems by title

### 🎖️ Badge Management
- [GET] `/api/v1/badges` – Get all badges (verified + unverified)
- [POST] `/api/v1/badges` – Create a new badge
- [GET] `/api/v1/badges/{id}` – Get badge by ID
- [PATCH] `/api/v1/badges/{id}` – Update badge by ID
- [PUT] `/api/v1/badges/{id}/verification` – Verify badge
- [PATCH] `/api/v1/badges/add-to-package` – Add badge to package
- [GET] `/api/v1/badges/verified` – Get all verified badges
- [GET] `/api/v1/badges/unverified` – Get all unverified badges

### 📦 Package Management
- [PUT] `/api/v1/packages/{id}/verification` – Verify package
- [PUT] `/api/v1/packages/add-problems` – Add problems to package
- [GET] `/api/v1/packages` – Get all packages (verified + unverified)
- [POST] `/api/v1/packages` – Create a new package
- [GET] `/api/v1/packages/{id}` – Get package by ID
- [PATCH] `/api/v1/packages/{id}` – Update package by ID

### 🖼️ Media Management
- [POST] `/api/v1/medias/upload` – Upload image
- [DELETE] `/api/v1/medias/{fileName}` – Delete image by name

### 📤 Submissions
- [POST] `/api/v1/submissions` – Run a single test case
- [POST] `/api/v1/submissions/run/batch` – Run multiple test cases
- [POST] `/api/v1/submissions/batch/{problemId}` – Submit code & save to history
- [GET] `/api/v1/submissions/{token}` – Get submission by token

### 🧑‍🎨 Creator Requests
- [GET] `/api/v1/creator-requests` – Get all requests
- [POST] `/api/v1/creator-requests` – Request creator role
- [PUT] `/api/v1/creator-requests` – Grant creator role

### 💬 Comments
- [PUT] `/api/v1/comments/change-status` – Update comment status
- [POST] `/api/v1/comments/report` – Report a comment
- [POST] `/api/v1/comments/create` – Post a comment

### 💡 Solutions
- [POST] `/api/v1/solutions` – Post a solution
- [PUT] `/api/v1/solutions/problem/{problemId}` – Get solutions for a problem

### 🔓 Hints
- [PUT] `/api/v1/hints/{id}/unlock` – Unlock a hint for a user

### 🏆 Leaderboard
- [GET] `/api/v1/leaderboard/me` – View leaderboard

### ⚙️ Roles
- [PUT] `/api/v1/roles/assign-role` – Assign role to a user
