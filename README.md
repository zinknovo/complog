# CompLog

CompLog is a Spring Boot service for managing activities, tasks, users, and sign-in records. It includes admin/backstage endpoints and a mini‑program interface for activity browsing and user check-ins.

## Features
- Activity management: add/edit/delete/list, statistics, department stats
- Task management under activities
- User and department management
- User joins and sign-in tracking for activities

## Tech Stack
- Java 11
- Spring Boot 2.7.x
- MyBatis-Plus
- MySQL
- Redis

## Project Structure
- `src/main/java/com/example/complog/controller/backagroud` admin/backstage APIs
- `src/main/java/com/example/complog/controller/miniwechat` mini‑program APIs
- `src/main/java/com/example/complog/domain` entity models
- `src/main/resources/mapper` MyBatis XML mappers

## Getting Started
1. Configure database and Redis in `src/main/resources/application.properties`.
2. Run locally with Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

## Configuration Example
Update `src/main/resources/application.properties` with your environment values:
```properties
spring.application.name=complog

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/complog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD

# Redis
spring.redis.host=localhost
spring.redis.port=6379
```

## API Overview
Admin/backstage:
- `POST /activities`
- `PUT /activities/{id}`
- `DELETE /activities/{id}`
- `GET /activities`
- `GET /activities/user-tasks`
- `GET /activities/user-joins`
- `GET /activities/statistics`
- `GET /activities/{activityId}/department-statistics`
- `GET /activities/{activityId}/tasks`
- `POST /activities/{activityId}/tasks/{taskId}`
- `POST /activities/{activityId}/tasks/{taskId}/clone`
- `DELETE /activities/{activityId}/tasks/{taskId}`
- `POST /tasks`
- `PUT /tasks/{id}`
- `DELETE /tasks/{id}`
- `GET /tasks`
- `POST /users`
- `GET /users`
- `POST /departments`
- `GET /departments`

Mini‑program:
- `GET /mini/activities`
- `POST /mini/activities/{activityId}/registrations`
- `GET /mini/activities/{activityId}/sign-days`
- `POST /mini/activities/{activityId}/signs`

## License
See `LICENSE`.
