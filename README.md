# CareerCompass - Job Search Management System

## Project Overview
CareerCompass is a full-stack web application designed to streamline the job search process by providing a centralized platform to discover opportunities, track applications, and manage your career journey.

The application features a React frontend with Material UI components and a robust backend built with Spring Boot Java and FastAPI Python microservices. The system allows users to search for jobs across multiple platforms, save favorites, and track application statuses through an intuitive workflow.

Try our application here:
[`CareerCompass`][demo]

[demo]: http://


## Technology Stack

### Frontend
- **Framework:** React 19.0.0 with Vite 6.2.0
- **State Management:** Redux Toolkit 2.6.1
- **UI Components:** Material UI 6.4.7
- **Form Handling:** React Hook Form 7.54.2
- **HTTP Client:** Axios 1.8.2
- **Notifications:** React Hot Toast 2.5.2
- **Testing:** Vitest 3.0.9, React Testing Library 16.2.0

### Backend
- **Java API:** Spring Boot 3.4.3
    - **Security:** Spring Security with JWT
    - **Database:** PostgreSQL with JPA/Hibernate, RDS for cloud deployment
    - **Testing:** JUnit, Mockito
- **Python API:** FastAPI
    - **HTTP Client:** Requests
    - **Data Processing:** Beautiful Soup 4, Pandas

### DevOps & CI/CD
- **Version Control:** Git
- **Build Tools:** Maven (Java), pip (Python)
- **CI/CD Pipeline:** GitHub Actions
- **Containerization:** Docker
- **Project Management:** ZenHub with Scrum methodology

## Core Features

### Job Search & Discovery
- Integration with multiple job listing platforms (LinkedIn, Indeed, IrishJobs, Jobs.ie)
- Advanced filtering capabilities by keywords, job types, and locations
- Time-based filtering for job freshness
- Customizable search interface with persistent URL parameters

### User Account Management
- Secure JWT-based authentication
- User registration and profile management

### Application Tracking
- Save interesting positions to favorites
- Track application status (New, Applied, Interview, Offer, Rejected)
- Visual status indicators for application progress
- Organization by application stage

### User Interface
- Responsive design for all device sizes
- Intuitive navigation with MUI components
- Real-time notifications for user actions
- Dark/light theme support

## Installation & Setup

### Prerequisites
- Node.js 18+
- Java 17+
- Python 3.9+
- PostgreSQL 14+

### Frontend Setup
1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start development server:
```bash
npm run dev
```

### Java Backend Setup
1. Navigate to the java-api directory:
```bash
cd backend/java-api
```

2. Configure database properties in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/careercompass
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Build and run the application:
```bash
mvn spring-boot:run
```

### Python Backend Setup
1. Navigate to the python-api directory:
```bash
cd backend/python-api
```

2. Set up virtual environment:
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

3. Install dependencies:
```bash
pip install -r requirements.txt
```

4. Set up environment variables:
```bash
export SQLALCHEMY_DATABASE_URL="postgresql://user:password@localhost/careercompass"
export API_KEY="your_google_search_api_key"
export CX_ID="your_custom_search_engine_id"
```

5. Run the application:
```bash
uvicorn main:app --reload
```

## Testing
- Frontend components are tested with Vitest and React Testing Library
- Backend services are tested with JUnit and Mockito
- Test coverage is tracked with JaCoCo for Java and coverage-v8 for JavaScript

## Development Methodology
Our team implemented Agile development practices using the Scrum framework:

- **Sprint Planning:** Bi-weekly sprints with clearly defined goals and deliverables
- **Daily Stand-ups:** Regular team check-ins to discuss progress and roadblocks
- **Sprint Reviews & Retrospectives:** End-of-sprint evaluations to improve processes
- **ZenHub Integration:** For backlog management, sprint planning, and tracking

## CI/CD & Deployment
The project implements a robust CI/CD pipeline using GitHub Actions:

- **Continuous Integration:**
    - Automated testing on pull requests
    - Code quality checks

- **Continuous Deployment:**
    - Automated Docker image building
    - Production deployment after approval


## 🖼️ Application Screenshots

### Jobs interface
- Job Browsing
- Job Details

### User interface
- Job Tracking


## Contributors
- TriByteGenius