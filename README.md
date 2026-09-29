# Military Asset Management System (MAMS)

A web app to track military assets (vehicles, weapons, ammunition) across different bases.
Users can record purchases, transfer assets between bases, and assign / expend assets.

## Tech Used
- Backend: Java 17, Spring Boot 3, Spring Security (JWT), Spring Data JPA, MySQL
- Frontend: React + Vite, plain CSS

## How to run

### 1. Backend
1. Install Java 17, Maven and MySQL.
2. Set your MySQL password as an environment variable, then start the app:
   ```
   cd backend
   set DB_PASS=your_mysql_password      (Windows)
   export DB_PASS=your_mysql_password   (Mac / Linux)
   mvn spring-boot:run
   ```
   Other optional variables: `DB_URL`, `DB_USER`, `JWT_SECRET`.
   The database `mams` and all tables are created automatically.

### 2. Frontend
```
cd frontend
npm install
npm run dev
```
Open http://localhost:5173 in the browser.
(For a deployed backend, copy `.env.example` to `.env` and put the backend url in `VITE_API_URL`.)

## Login details
| Username  | Password | Role              |
|-----------|----------|-------------------|
| admin     | admin123 | Admin             |
| commander | cmd123   | Base Commander (Base Alpha)   |
| logistics | log123   | Logistics Officer (Base Alpha) |

## Folder structure (backend)
- `model` - database tables (entities) and enums
- `repository` - database queries
- `dto` - classes for the data we receive from the frontend
- `controller` - REST APIs
- `security` - JWT, login filter, role settings
- `service` - helper to get the logged in user
- `config` - creates the starting users and bases
