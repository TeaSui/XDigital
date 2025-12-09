# RAG Chat Storage Microservice System

A production-ready microservice-based system for storing and retrieving chat histories from a RAG-based chatbot system.

## System Architecture

This system follows a microservice architecture with the following components:

1. **Service Registry** - Eureka Server for service discovery
2. **API Gateway** - Entry point for all client requests with routing and authentication
3. **Auth Service** - Handles API key authentication
4. **Session Service** - Manages chat sessions
5. **Message Service** - Stores and retrieves chat messages
6. **Context Service** - Manages context information for RAG-based conversations

## Technology Stack

- **Java 21** - Latest Java features with Spring Boot 3.2+
- **Spring Cloud** - For microservices architecture
- **PostgreSQL** - For data persistence
- **Kafka** - For event-driven communication between services
- **Docker** - For containerization
- **Gradle** - For building and managing dependencies
- **Flyway** - For database migrations

## Prerequisites

- **JDK 21**
- **Docker** and **Docker Compose**
- **Gradle 8.5+**
- **PostgreSQL** (for local development without Docker)
- **Kafka** (for local development without Docker)

## Getting Started

### Local Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/rag-chat-system.git
   cd rag-chat-system
   ```

2. Build all services:
   ```bash
   ./gradlew clean build -x test
   ```

3. Start with Docker Compose:
   ```bash
   docker-compose up -d
   ```

4. Access the services:
   - Service Registry: http://localhost:8761
   - API Gateway: http://localhost:8080
   - Swagger UI for each service:
     - Auth Service: http://localhost:8081/swagger-ui.html
     - Session Service: http://localhost:8082/swagger-ui.html
     - Message Service: http://localhost:8083/swagger-ui.html
     - Context Service: http://localhost:8084/swagger-ui.html

### Setup Without Docker

1. Install PostgreSQL and create the following databases:
   - `auth_db`
   - `session_db`
   - `message_db`
   - `context_db`

2. Install Kafka and Zookeeper

3. Start each service individually:
   ```bash
   cd service-registry
   ./gradlew bootRun

   cd ../api-gateway
   ./gradlew bootRun

   cd ../auth-service
   ./gradlew bootRun

   cd ../session-service
   ./gradlew bootRun

   cd ../message-service
   ./gradlew bootRun

   cd ../context-service
   ./gradlew bootRun
   ```

## API Documentation

### API Gateway Endpoints

- **Base URL**: `http://localhost:8080`

| Service | Path | Description |
|---------|------|-------------|
| Auth Service | `/api/auth/**` | Authentication endpoints |
| Session Service | `/api/sessions/**` | Session management endpoints |
| Message Service | `/api/messages/**` | Message management endpoints |
| Context Service | `/api/contexts/**` | Context management endpoints |

### Authentication

All requests must include an API key header:

```
X-API-KEY: your-api-key
```

API keys can be managed through the Auth Service API.

### User Identification

All requests must include a user ID header:

```
X-User-ID: your-user-uuid
```

## Service Details

### Auth Service

Manages API keys for authentication. Endpoints:

- `POST /api/auth/apikeys` - Generate a new API key
- `GET /api/auth/apikeys/{id}` - Get API key information
- `DELETE /api/auth/apikeys/{id}` - Revoke an API key
- `POST /api/auth/validate` - Validate an API key (internal use)

### Session Service

Manages chat sessions. Endpoints:

- `GET /api/sessions` - Get all sessions for a user
- `POST /api/sessions` - Create a new session
- `GET /api/sessions/{id}` - Get a session by ID
- `PUT /api/sessions/{id}` - Update a session
- `DELETE /api/sessions/{id}` - Delete a session

### Message Service

Stores and retrieves chat messages. Endpoints:

- `GET /api/messages?sessionId={sessionId}` - Get messages for a session
- `POST /api/messages` - Create a new message
- `GET /api/messages/{id}` - Get a message by ID
- `PUT /api/messages/{id}` - Update a message
- `DELETE /api/messages/{id}` - Delete a message
- `GET /api/messages/session/{sessionId}` - Get all messages for a session

### Context Service

Manages context information for RAG-based conversations. Endpoints:

- `GET /api/contexts?sessionId={sessionId}` - Get context entries for a session
- `GET /api/contexts/source-type?sessionId={sessionId}&sourceType={sourceType}` - Get context entries by source type
- `POST /api/contexts` - Create a new context entry
- `GET /api/contexts/{id}` - Get a context entry by ID
- `PUT /api/contexts/{id}` - Update a context entry
- `DELETE /api/contexts/{id}` - Delete a context entry
- `POST /api/contexts/search` - Semantic search over context entries
- `GET /api/contexts/message/{messageId}` - Get context entry by message ID

## Event-Driven Architecture

Services communicate using Kafka events:

- **Session Events** - Created, updated, or deleted sessions
- **Message Events** - Created, updated, or deleted messages
- **Context Events** - Created, updated, or deleted context entries

## Monitoring and Management

All services expose Spring Boot Actuator endpoints for monitoring:

- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`

## Testing

To run tests for all services:

```bash
./gradlew test
```

## Deployment

For production deployment, consider:

1. Configuring external PostgreSQL databases
2. Setting up a Kafka cluster
3. Implementing proper security measures (TLS, service mesh)
4. Setting up monitoring and logging
5. Implementing CI/CD pipelines

## Contributing

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Submit a pull request

## License

This project is licensed under the Apache 2.0 License - see the LICENSE file for details.