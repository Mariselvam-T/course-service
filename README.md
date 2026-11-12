# Course Service

Part of the **Student Registration Microservices System** (SEZG583 Scalable Services, BITS Pilani WILP).

| | |
|---|---|
| Author | Mariselvam (2025MT03120) |
| Port | 8082 |
| Stack | Java 17, Spring Boot 3.2, Spring Data JPA, H2, RestTemplate, Maven, Docker, Kubernetes |
| Depends on | `student-service` (REST, URL taken from `STUDENT_SERVICE_URL`) |

## What it does
Owns the course catalogue and the registrations. When a student is registered to a course it calls
the Student Service (`GET /api/students/{id}`) to confirm the student exists, then checks for
duplicates and free seats before saving the registration.

## REST API
| Method | URL | Description | Success |
|---|---|---|---|
| POST | `/api/courses` | Create course | 201 |
| GET | `/api/courses` | Get all courses (with seats available) | 200 |
| GET | `/api/courses/{id}` | Get one course | 200 / 404 |
| POST | `/api/registrations` | Register student to course | 201 / 404 / 409 / 503 |
| GET | `/api/registrations` | View all registrations | 200 |
| GET | `/api/registrations/course/{courseId}` | View registrations of a course | 200 / 404 |
| GET | `/api/registrations/student/{studentId}` | View registrations of a student | 200 |

Course body: `{ "code": "SEZG583", "title": "Scalable Services", "credits": 4, "capacity": 30 }`
Registration body: `{ "studentId": 1, "courseId": 1 }`

Errors: 400 validation, 404 course or student not found, 409 duplicate registration or course full,
503 Student Service not reachable. Health: `/actuator/health`. H2 console: `/h2-console` (`jdbc:h2:mem:coursedb`).

## Configuration
| Property / env variable | Default | Meaning |
|---|---|---|
| `STUDENT_SERVICE_URL` | `http://localhost:8081` | Base URL of the Student Service |

## Prerequisites
Java 17+, Maven 3.8+, Docker Desktop. The Student Service must be running for registrations.

## Run locally
```bash
mvn clean package
java -jar target/course-service.jar
```

## Docker
```bash
mvn clean package
docker build -t course-service:1.0 .
docker network create sreg-net        # skip if it already exists
docker run -d --name course-service --network sreg-net -p 8082:8082 \
  -e STUDENT_SERVICE_URL=http://student-service:8081 course-service:1.0
docker logs course-service
```

## Kubernetes
```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/course-service-configmap.yaml
kubectl apply -f k8s/course-service-deployment.yaml
kubectl apply -f k8s/course-service-service.yaml
kubectl get pods -n student-registration
kubectl port-forward -n student-registration svc/course-service 8082:8082
```
Optional autoscaling: `kubectl apply -f k8s/course-service-hpa.yaml` (needs metrics-server).

## Project structure
```
course-service/
├── .github/workflows/ci.yml
├── .gitignore
├── Dockerfile
├── pom.xml
├── README.md
├── k8s/
│   ├── namespace.yaml
│   ├── course-service-configmap.yaml
│   ├── course-service-deployment.yaml
│   ├── course-service-service.yaml
│   └── course-service-hpa.yaml
└── src/
    ├── main/java/com/bits/sreg/course/
    │   ├── CourseServiceApplication.java
    │   ├── client/StudentClient.java
    │   ├── config/RestClientConfig.java
    │   ├── controller/{CourseController, RegistrationController}.java
    │   ├── service/{CourseService, RegistrationService}.java
    │   ├── repository/{CourseRepository, RegistrationRepository}.java
    │   ├── entity/{Course, Registration}.java
    │   ├── dto/{CourseRequest, CourseResponse, RegistrationRequest, RegistrationResponse, StudentDto}.java
    │   └── exception/{ResourceNotFoundException, ConflictException, ServiceUnavailableException, ErrorResponse, GlobalExceptionHandler}.java
    ├── main/resources/application.properties
    └── test/java/com/bits/sreg/course/CourseServiceApplicationTests.java
```

## Known limitation
H2 is in memory (data lost on restart, not shared across replicas). Student name and email are copied
into the registration so that the registration list does not need a call to the Student Service.
