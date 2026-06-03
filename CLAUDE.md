# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FreeMarket is a marketplace web application with a Spring Boot backend (`platform/`) and a React/TypeScript frontend (`frontend/`). The two are deployed independently: backend to AWS EC2 via Docker/ECR, frontend to AWS S3.

## Commands

### Backend (`platform/`)

```bash
# Run (requires PostgreSQL running locally or via .env)
./mvnw spring-boot:run

# Build (skip tests)
./mvnw package -DskipTests

# Run tests (uses H2 in-memory — no external DB needed)
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyTestClass
```

### Frontend (`frontend/`)

```bash
npm install
npm run dev      # starts Vite dev server on :5173, proxies /api and /auth to :8080
npm run build    # tsc + vite build
npm run lint     # eslint
```

## Architecture

### Backend (`platform/`)

Standard layered Spring Boot app:

- **`controller/`** — REST controllers (`AuthController`, `PostController`, `MarketActorController`, `RatingController`). All business endpoints are under `/api/**`; auth endpoints are under `/auth/**`.
- **`service/`** — Business logic. Services map entities to DTOs and own transactions.
- **`repository/`** — Spring Data JPA repositories. `PostRepository` has custom JPQL queries for search/filter combinations and an atomic `incrementViewCount` update.
- **`entity/`** — JPA entities: `MarketActor` (users), `Post` (listings), `Rating`. Post has `@ElementCollection` tables for `tags` and `images`.
- **`dto/`** — Request/response records. Requests live in `dto/request/`, responses in `dto/response/`.
- **`security/`** — Stateless JWT auth. `JwtService` signs/validates tokens (HMAC, base64-encoded secret). `JwtAuthenticationFilter` extracts the token from `Authorization: Bearer ...`. `CurrentUserRetreiver` pulls the UUID from the authenticated principal.
- **`config/SecurityConfig`** — Public routes: `GET /api/posts/**`, `GET /api/ratings/**`, `/auth/login`, `/auth/register`, Swagger UI. Everything else under `/api/**` requires authentication.
- **`config/DevUserSeeder`** — Seeds `admin/admin` (roles: ADMIN, USER) and `user/user` (role: USER) on startup if they don't exist.
- **`exception/`** — `GlobalExceptionHandler` maps `NotFoundException` → 404, `ForbiddenException` → 403, validation errors → 400 with `fieldErrors` map.

**Database:** PostgreSQL in production (AWS RDS), H2 for tests. Schema managed by Flyway migrations in `src/main/resources/db/migration/` (V1–V9). `spring.jpa.hibernate.ddl-auto=validate` — never let Hibernate modify the schema; always add a new migration file.

**API docs:** Swagger UI available at `/swagger-ui.html` (publicly accessible).

### Frontend (`frontend/`)

React 19 + TypeScript SPA built with Vite.

- **`src/app/router.tsx`** — React Router v7 routes. The app shell wraps all routes; `ProtectedRoute` guards authenticated-only pages.
- **`src/app/auth.tsx`** — `AuthContext` / `useAuth` hook. JWT token stored in `localStorage` under `fm_token`; username under `fm_username`.
- **`src/api/http.ts`** — Central `http<T>()` fetch wrapper. Attaches `Authorization: Bearer` header, handles 401 by clearing auth and redirecting to `/login`, surfaces structured error messages from the backend (`fieldErrors`, `message`, `error`).
- **`src/api/postsApi.ts`** — Typed API functions over `http<T>()`.
- **`src/app/pages/`** — Page components (one per route).
- **`src/components/ui/`** — shadcn/ui components (Radix UI primitives + Tailwind). Do not edit these manually; regenerate via shadcn CLI if needed.
- **`src/lib/utils.ts`** — `cn()` helper (clsx + tailwind-merge).

**Styling:** Tailwind CSS v4 (PostCSS plugin). Path aliases configured via `vite-tsconfig-paths` — use `@/` for `src/`.

**Dev proxy:** Vite proxies `/api` and `/auth` to `http://localhost:8080`, so the frontend can run on `:5173` without CORS issues during development.

## Environment & Configuration

Backend env vars (set in `.env` for docker-compose, or as process env):

| Variable | Default |
|---|---|
| `POSTGRES_HOST` | `localhost` |
| `POSTGRES_PORT` | `5432` |
| `POSTGRES_USER` | `admin` |
| `POSTGRES_PASSWORD` | `password` |
| `POSTGRES_DB` | `freemarket` |
| `SECURITY_JWT_SECRET` | *(required — base64 HMAC key)* |
| `SECURITY_JWT_EXPIRATION_MS` | `3600000` (1 hour) |

Frontend env var: `VITE_API_BASE_URL` — set to the backend origin for production builds (empty = relative URLs, works with the dev proxy).

## Local Kubernetes (Minikube + Helm)

```bash
# One-shot: start Minikube, build image, install chart
./scripts/minikube-dev.sh          # deploy
./scripts/minikube-dev.sh --url    # deploy + print NodePort URL

# Manual steps
minikube start --cpus=2 --memory=3072
eval $(minikube docker-env)
docker build -t freemarket-backend:latest ./platform

helm upgrade --install freemarket ./helm/freemarket \
  -f ./helm/freemarket/values-local.yaml \
  --wait --timeout 3m

minikube service freemarket --url   # get the NodePort URL
```

**Chart layout** (`helm/freemarket/`):
- `templates/configmap.yaml` — non-sensitive env vars (`POSTGRES_HOST/PORT/DB`, `JWT_EXPIRATION_MS`)
- `templates/secret.yaml` — credentials (`POSTGRES_USER`, `POSTGRES_PASSWORD`, `SECURITY_JWT_SECRET`)
- `templates/deployment.yaml` — app Deployment; `initContainer` waits for Postgres; health probes hit `/actuator/health`
- `templates/service.yaml` — ClusterIP (or NodePort via `values-local.yaml`)
- `templates/postgresql-statefulset.yaml` — Postgres 16 StatefulSet with `volumeClaimTemplate` (disable via `postgresql.enabled=false` for production)
- `templates/postgresql-service.yaml` — headless-compatible ClusterIP for the StatefulSet

When `postgresql.enabled=true` (the default), `POSTGRES_HOST` in the ConfigMap automatically resolves to the StatefulSet's Service name. Set `postgresql.enabled=false` and provide `config.postgresHost` to point at an external database.

Credentials are in `values-local.yaml` (local dev) or passed via `--set` / an out-of-tree values file for production. Never commit production credentials.

## Deployment

CI/CD via `.github/workflows/deploy.yml` on every push to `main`:

1. **Backend** — builds Docker image from `platform/Dockerfile`, pushes to AWS ECR (`eu-west-1`), deploys to EC2 via SSH.
2. **Frontend** — runs `npm run build` with `VITE_API_BASE_URL` set to the EC2 host, syncs `dist/` to an S3 bucket (static website hosting).

Secrets required: `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `EC2_HOST`, `EC2_USER`, `EC2_SSH_KEY`, `POSTGRES_*`, `SECURITY_JWT_SECRET`, `S3_BUCKET`.
