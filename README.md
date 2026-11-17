# CampusCare - Campus Maintenance Request System

A beginner-friendly full-stack portfolio project with a React 19/Vite frontend and a Java 17+/Spring Boot 4 backend. It supports secure student, staff, and administrator workflows, private image attachments, notifications, email verification, and password recovery.

## Features

- Students register, verify email, report issues with an optional JPEG/PNG, and track request history.
- Students can edit, remove, or delete only their own pending, unassigned requests.
- Admins create/deactivate staff, filter every request, set priority, and assign or reassign work.
- Staff see only assigned work, start it, and complete it with a required repair note.
- Opaque 24-hour login tokens and single-use verification/reset tokens are stored only as SHA-256 hashes.
- BCrypt passwords, backend-enforced ownership/role checks, token revocation, optimistic locking, and authentication throttling.
- Flyway migrations preserve existing data and backfill old `USER`/role-less accounts as `STUDENT`.

## Windows development setup

Requirements: Java 17+, Node.js 20+, and MySQL 8.

Create a MySQL database/user, then in PowerShell from the repository root:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/campus_maintenance?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'your-password'
$env:FRONTEND_ORIGIN = 'http://localhost:5173'
$env:UPLOAD_DIRECTORY = 'E:\campuscare-data\uploads'
$env:ADMIN_EMAIL = 'admin@campus.edu'
$env:ADMIN_PASSWORD = 'replace-with-a-strong-password'
$env:ADMIN_NAME = 'Campus Administrator'
cd backend
.\mvnw.cmd spring-boot:run
```

The first admin is created only when `ADMIN_EMAIL` does not already exist. Later startups never reset that password or promote an existing account. Remove `ADMIN_PASSWORD` from the environment after the first successful startup.

Alternatively, create the first admin with the secure interactive helper (credentials are kept only in the process environment):

```powershell
cd backend
.\create-admin.ps1
```

In another PowerShell window:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## Email and Mailpit

For local email inspection, run Mailpit:

```powershell
docker run --rm -p 1025:1025 -p 8025:8025 axllent/mailpit
$env:MAIL_ENABLED = 'true'
$env:SMTP_HOST = 'localhost'
$env:SMTP_PORT = '1025'
$env:SMTP_AUTH = 'false'
$env:SMTP_STARTTLS = 'false'
```

Restart the backend and inspect messages at `http://localhost:8025`. Production SMTP variables are `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_AUTH`, and `SMTP_STARTTLS`.

## Environment variables

| Variable | Example / purpose |
| --- | --- |
| `DB_URL` | MySQL JDBC URL |
| `DB_USERNAME`, `DB_PASSWORD` | MySQL credentials |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_NAME` | One-time first-admin bootstrap |
| `FRONTEND_ORIGIN` | Allowed browser origin and email-link base URL |
| `UPLOAD_DIRECTORY` | Private persistent attachment directory |
| `MAIL_ENABLED` | `true` to send verification/reset mail |
| `SMTP_*` | SMTP connection and security settings |
| `VITE_API_URL` | Frontend API URL; default `http://localhost:8080/api` |

## Database migration

Flyway runs versioned scripts from `backend/src/main/resources/db/migration`. On a fresh database, V1 creates the original schema and V2 adds the workflow. On an existing non-empty database, `baseline-on-migrate` records V1 and applies V2 without dropping tables. Back up production data before any migration. Never use `create-drop` outside tests.

## Checks

Run these exact commands from the repository root:

```powershell
cd backend
.\mvnw.cmd test
cd ..\frontend
npm run lint
npm run build
```

## Production outline

1. Run `npm ci` and `npm run build` in `frontend`; serve `frontend/dist` through an HTTPS web server.
2. Build the backend with `backend\mvnw.cmd clean package`, then run the JAR behind the same HTTPS reverse proxy.
3. Use a managed MySQL 8 database, real SMTP credentials, a strong one-time admin password, and a backed-up persistent `UPLOAD_DIRECTORY`.
4. Set `FRONTEND_ORIGIN` and `VITE_API_URL` to the real HTTPS origins. Restrict filesystem/database access and terminate TLS at the reverse proxy.

## Interview demo

1. Register a student, open Mailpit, verify the email, and sign in.
2. Submit a request with an image and show that another student cannot open it.
3. Sign in as admin, create a staff account, assign the request, and show the notification.
4. Sign in as staff, change the temporary password, start work, and finish it with a repair note.
5. Return to the student account to show notifications, completed status, history, and repair note.

The application was intentionally kept as conventional controllers, services, repositories, entities, and small React components so an intern can explain each layer clearly.
