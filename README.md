# WorkSpaceFlow

A professional workflow management system built with Java 23, Spring Boot 3+, Spring Kafka, MongoDB, and React.

## 🎯 Features

- **Workflow Management**: Create and manage workflow definitions with multiple steps
- **Instance Execution**: Start workflow instances and track their progress
- **Task Management**: Assign and complete human tasks
- **Event-Driven Architecture**: Kafka-based event bus for decoupled services
- **Real-time Monitoring**: Track workflow and task status in real-time
- **Audit Trail**: Complete audit log of all events
- **Modern UI**: React frontend with TailwindCSS

## 🏗️ Architecture

### Backend (Java 23 + Spring Boot)
- **REST API**: Full CRUD operations for workflows, instances, and tasks
- **MongoDB**: NoSQL database for flexible data storage
- **Apache Kafka**: Event streaming platform for async communication
- **Spring Kafka**: Producer/Consumer with retry and Dead Letter Topic (DLT)
- **OpenAPI/Swagger**: API documentation at `/swagger-ui.html`

### Frontend (React + TypeScript)
- **Vite**: Fast build tool and dev server
- **React Router**: Client-side routing
- **Axios**: HTTP client for API calls
- **TailwindCSS**: Utility-first CSS framework

### Infrastructure
- **Docker Compose**: Orchestrates all services
- **Kafka + Zookeeper**: Message broker setup
- **MongoDB**: Persistent data storage

## 📦 Prerequisites

- Docker and Docker Compose
- Java 23 (for local development)
- Node.js 20+ (for local development)

## 🚀 Quick Start

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   cd WorkSpaceFlow
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

4. **Stop all services**
   ```bash
   docker-compose down
   ```

### Local Development

#### Backend
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 📡 Kafka Topics

The application uses the following Kafka topics:

- **workflow.events**: Workflow instance lifecycle events
- **task.events**: Task lifecycle events
- **notification.events**: Notification events
- **audit.events**: Audit trail events
- **deadletter.events**: Failed messages after retries

## 🔌 API Examples

### Create a Workflow

```bash
curl -X POST http://localhost:8080/api/workflows \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin" \
  -d '{
    "name": "Purchase Approval",
    "description": "Workflow for purchase approvals",
    "steps": [
      {
        "stepId": "step1",
        "name": "Manager Approval",
        "type": "APPROVAL",
        "assigneeRole": "manager",
        "order": 1
      },
      {
        "stepId": "step2",
        "name": "Finance Review",
        "type": "HUMAN_TASK",
        "assigneeRole": "finance",
        "order": 2
      }
    ]
  }'
```

### Start a Workflow Instance

```bash
curl -X POST http://localhost:8080/api/workflows/start \
  -H "Content-Type: application/json" \
  -d '{
    "workflowId": "<workflow-id>",
    "startedBy": "john.doe",
    "variables": {
      "amount": 5000,
      "department": "IT"
    }
  }'
```

### Assign a Task

```bash
curl -X PUT "http://localhost:8080/api/tasks/<task-id>/assign?assignee=jane.smith"
```

### Complete a Task

```bash
curl -X PUT http://localhost:8080/api/tasks/<task-id>/complete \
  -H "Content-Type: application/json" \
  -d '{
    "completedBy": "jane.smith",
    "data": {
      "approved": true,
      "comments": "Approved"
    }
  }'
```

### Get All Workflows

```bash
curl http://localhost:8080/api/workflows
```

### Get All Tasks

```bash
curl http://localhost:8080/api/tasks
```

## 🧪 Testing

### Run Backend Tests

```bash
cd backend
mvn test
```

The tests include:
- Unit tests with JUnit 5 and Mockito
- Integration tests with Testcontainers (Kafka + MongoDB)

### Run Frontend Tests

```bash
cd frontend
npm test
```

## 📊 Kafka Configuration

### Producer Configuration
- **Idempotence**: Enabled
- **Acks**: All
- **Retries**: 3
- **Serialization**: JSON

### Consumer Configuration
- **Group ID**: workspace-group
- **Auto Offset Reset**: earliest
- **Deserialization**: JSON with trusted packages
- **Error Handling**: Retry 3 times with 1s interval, then send to DLT

## 🔧 Environment Variables

### Backend
- `SPRING_DATA_MONGODB_URI`: MongoDB connection string (default: `mongodb://localhost:27017/workspaceflow`)
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka bootstrap servers (default: `localhost:9092`)

### Frontend
- `VITE_API_URL`: Backend API URL (default: `http://localhost:8080/api`)

## 📁 Project Structure

```
WorkSpaceFlow/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/workspaceflow/
│   │   │   │   ├── config/          # Kafka, MongoDB, OpenAPI config
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── repository/      # MongoDB repositories
│   │   │   │   ├── model/           # Entities and DTOs
│   │   │   │   ├── event/           # Kafka producers/consumers
│   │   │   │   ├── mapper/          # MapStruct mappers
│   │   │   │   └── exception/       # Exception handlers
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/                     # API client
│   │   ├── components/              # React components
│   │   ├── pages/                   # Page components
│   │   ├── types/                   # TypeScript types
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── Dockerfile
│   └── package.json
└── docker-compose.yml
```

## 🎨 UI Features

- **Dashboard**: Overview of workflow instances and pending tasks
- **Workflows**: List, create, and manage workflow definitions
- **Instances**: View all workflow instances and their status
- **Tasks**: Manage tasks (assign, complete)
- **Dark Theme**: Modern dark UI with TailwindCSS

## 🔒 Security Notes

This is a demo application. For production use, consider:
- Adding authentication and authorization (JWT, OAuth2)
- Implementing role-based access control (RBAC)
- Securing Kafka with SSL/SASL
- Using MongoDB authentication
- Adding rate limiting
- Implementing CORS properly

## 📝 License

This project is for demonstration purposes.

## 🤝 Contributing

This is a demo project. Feel free to fork and modify for your needs.

## 📧 Support

For issues and questions, please open an issue in the repository.
