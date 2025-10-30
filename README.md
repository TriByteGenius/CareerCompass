# CareerCompass — Job Search Management Platform

## Overview
CareerCompass is a microservices-based web platform for discovering job opportunities, saving favorites, tracking application progress, and generating AI-assisted insights from job descriptions. The system consists of a React frontend, multiple Spring Boot services, and a Python service, exposed via an API Gateway. RabbitMQ enables asynchronous inter-service communication, and Redis accelerates AI result retrieval via caching.


## Architecture & Services

### Services
- **API Gateway (Spring Cloud Gateway):** Central entrypoint for routing and cross-cutting concerns.
- **UserService (Java):** Authentication, user profiles, and user lifecycle events publishing.
- **JobService (Java):** Job data and search; AI job analysis; publishes/consumes job-related events; Redis-backed AI result cache.
- **UserJobService (Java):** Favorites and application tracking; consumes user/job events to maintain user–job views.
- **Python Service (FastAPI):** Page fetching/parsing and auxiliary analysis; integrates with RabbitMQ.
- **Frontend (React + Vite):** SPA consuming backend via gateway paths and configurable relative APIs.

### Middleware
- **RabbitMQ (AMQP):**
  - Java services use `spring-boot-starter-amqp` for publishers and `@RabbitListener` consumers.
  - Kubernetes manifests: `k8s-dev/rabbitmq.yml`, `k8s-eks/rabbitmq.yml`; deployment scripted in `deploy.sh`.
- **Redis:**
  - `JobService` includes `spring-boot-starter-data-redis` for caching AI analysis results (24h TTL).
- **PostgreSQL:**
  - Service-scoped schemas via JPA/Hibernate.


## Technology Stack

### Frontend
- React 19, Vite 6
- Redux Toolkit, Material UI
- Axios, React Hook Form, React Hot Toast
- Testing: Vitest, React Testing Library

### Backend
- Spring Boot 3 (JWT, JPA/Hibernate, AMQP, Redis)
- FastAPI (fetch/parse, MQ integration)

### Infra & Delivery
- Docker
- Kubernetes manifests: `k8s-dev/` (dev), `k8s-eks/` (cloud)
- GitHub Actions CI/CD


## AI Capabilities
- `AIAgentService` (in `JobService`):
  - Fetches visible text from job pages (`PageFetchService`) and calls `OpenAIClientService` for structured analysis.
  - Caches results in Redis using a SHA-256 key derived from the source URL (24h TTL).
- `OpenAIClientService`:
  - Calls `https://api.openai.com/v1/chat/completions` with a configurable API key; extracts: `requiredSkills`, `experienceLevel`, `keyRequirements`, `roleType`, `difficulty`.
- Python Service:
  - Supports job crawling/parsing and asynchronous collaboration with Java services via RabbitMQ.


## API Gateway & Routing
- Spring Cloud Gateway routes are configured via `application-*.yml` to forward requests to `UserService`, `JobService`, `UserJobService`, and the Python service.
- Prefer using a single routing configuration approach (YAML or Java DSL) to avoid conflicts.


## Local Development

### Prerequisites
- Node.js 18+
- Java 17+
- Python 3.9+
- PostgreSQL 14+
- RabbitMQ, Redis

### Recommended Startup Order
1. Start PostgreSQL, RabbitMQ, Redis.
2. Start backend services (suggested): `UserService` → `JobService` → `UserJobService` → `ApiGateway` → `python-service`.
3. Start the frontend.

### Frontend (dev)
```bash
cd frontend
npm install
npm run dev
```

The SPA uses relative/gateway paths for backend APIs to avoid hardcoding service URLs [[memory:6234778]].

### Java services (example: JobService)
```bash
cd "backend micro service/JobService"
./mvnw spring-boot:run  # On Windows: mvnw.cmd
```

Configure DB, MQ, Redis, and the OpenAI key via `src/main/resources/application-local.yml` and environment variables (see next section). Apply the same pattern for `UserService`, `UserJobService`, and `ApiGateway`.

### Python service
```bash
cd python-service
python -m venv venv
venv\Scripts\activate  # Windows
pip install -r requirments.txt
uvicorn main:app --reload
```


## Configuration & Environment

### Java services (common)
- Database: `spring.datasource.*`
- RabbitMQ: `spring.rabbitmq.*` or custom `rabbitmq.*` keys as defined per service

### JobService
- Redis: `spring.redis.host` (e.g., `${SPRING_REDIS_HOST:redis}` in `application-prod.yml`)
- OpenAI: `openai.api-key`

### Python service
- RabbitMQ: `RABBITMQ_HOST` (see `k8s-dev/configmap.yml`)
- Additional crawling/analysis settings as needed

### Kubernetes
- Dev: `k8s-dev/` (includes `rabbitmq.yml`, service manifests, and `deploy.sh` automation)
- Cloud: `k8s-eks/`


## Core Features
- Multi-source job discovery with filters (keyword, type, location, freshness)
- JWT-based authentication and user management
- Favorites and application status tracking (New, Applied, Interview, Offer, Rejected)
- Responsive UI with real-time feedback
- AI job insights (skills, experience level, key requirements, role type, difficulty)


## Testing
- Frontend: Vitest, React Testing Library
- Backend: JUnit, Mockito; coverage via JaCoCo (Java) and V8 (JS)


## CI/CD & Deployment
- GitHub Actions for build, test, and image publishing
- Docker images for local and Kubernetes deployments


## Repository Structure (high level)
- `frontend/`
- `backend micro service/ApiGateway/`
- `backend micro service/UserService/`
- `backend micro service/JobService/`
- `backend micro service/UserJobService/`
- `python-service/`
- `k8s-dev/`, `k8s-eks/`
- `database/`


## Contributors
- TriByteGenius