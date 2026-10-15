# UrbanAssist Backend — Spring Boot REST API

**Intelligent Civic Complaint and Response System**

---

## Technical Stack & Architecture

- **Framework**: Java 21 / Spring Boot 3.4.1
- **Security**: Spring Security 6, BCrypt Password Encoder (strength 12), Stateless JWT
- **Persistence**: Spring Data JPA, Hibernate, Aiven MySQL (Cloud with SSL/TLS) / H2 (Testing fallback)
- **AI Subsystem**: Modular `AiService` (Hugging Face Inference API with deterministic keyword NLP fallback)
- **Geospatial Engine**: Haversine distance proximity + Jaccard text similarity duplicate detector
- **Priority Engine**: Formulated priority scoring ($0.00$ to $100.00$) combining AI severity, urgency, duplicate boost, and aging factors

---

## Key Features & Endpoints Summary

### 1. Authentication & Security (`/api/auth`)
- `POST /api/auth/register` — Citizen registration
- `POST /api/auth/login` — Returns JWT token + User DTO
- `GET /api/auth/me` — Currently authenticated profile

### 2. Department Management (`/api/departments`)
- `GET /api/departments` — List active departments (Roads, Sanitation, Water, Electricity, Parks, Public Health)
- `GET /api/departments/{id}` — Department details

### 3. Citizen Complaint Intake & Tracking (`/api/complaints`)
- `POST /api/complaints` — Submit complaint with text, geo-coordinates, photos (`BEFORE_SUBMISSION`), and optional voice recording. Triggers AI classification, severity estimation, duplicate check, and priority scoring.
- `GET /api/complaints/my` — List citizen's filed complaints with status badges
- `GET /api/complaints/{id}` — Full complaint profile (media, timeline audit log, feedback)
- `POST /api/complaints/{id}/feedback` — Submit 1-5 star post-resolution rating & review
- `GET /api/complaints/media/{filename:.+}` — Secure media file serving

### 4. Municipal Staff Workflow (`/api/staff`) — Requires `ROLE_STAFF` or `ROLE_ADMIN`
- `GET /api/staff/queue` — Department queue sorted by priority score (`priorityScore DESC`)
- `PUT /api/staff/complaints/{id}/status` — Status transition (`IN_PROGRESS`, `REJECTED`)
- `POST /api/staff/complaints/{id}/resolve` — Mark `RESOLVED` with mandatory verified after-resolution photograph(s)

### 5. Admin Administration (`/api/admin`) — Requires `ROLE_ADMIN`
- `GET /api/admin/complaints` — Master complaint table with category/status/department filters
- `PUT /api/admin/complaints/{id}/assign` — Re-assign complaint to a department
- `GET /api/admin/users` — List citizens and staff
- `POST /api/admin/staff` — Onboard municipal staff tied to a department
- `PUT /api/admin/users/{id}/status` — Activate / Deactivate user accounts
- `POST /api/admin/departments` / `PUT /api/admin/departments/{id}` — Department management

### 6. Maps & Hotspot APIs (`/api/public/map`)
- `GET /api/public/map/complaints` — Publicly safe geo-coordinates for React Leaflet map pins (strips PII)
- `GET /api/public/map/hotspots` — Unresolved grievance density data for heatmap overlays

### 7. Civic Analytics (`/api/analytics`) — Requires `ROLE_STAFF` or `ROLE_ADMIN`
- `GET /api/analytics/summary` — Total, pending, in-progress, resolved, resolution velocity %, average rating, and category/status/department breakdowns for Recharts

---

## Environment Variables

| Variable | Description | Production (Aiven MySQL) | Local Dev / Test Default |
|---|---|---|---|
| `AIVEN_DB_URL` | MySQL Connection URL | `jdbc:mysql://<host>:<port>/urbanassist_db?sslMode=REQUIRED` | `jdbc:h2:mem:urbanassist_db;DB_CLOSE_DELAY=-1;MODE=MySQL` |
| `AIVEN_DB_USER` | MySQL Username | `avnadmin` | `sa` |
| `AIVEN_DB_PASSWORD` | MySQL Password | *(Your Aiven Password)* | *(Empty)* |
| `JWT_SECRET` | Secret key for JWT | *(32+ byte random string)* | `UrbanAssistSuperSecretKey2026...` |
| `HF_API_KEY` | Hugging Face API Token | `hf_xxxxxxxxxxxx` | *(Empty — Uses Rule Fallback)* |

---

## Running the Application & Tests

```bash
cd backend

# Build and execute all unit & integration tests
.\mvnw.cmd clean test

# Package and run locally
.\mvnw.cmd spring-boot:run
```
