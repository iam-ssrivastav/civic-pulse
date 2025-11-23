# CivicPulse - Smart City Incident Triage System 🏙️🚨

**CivicPulse** is a modern, real-time incident reporting system designed for smart cities. It leverages **Spring Boot**, **Kafka**, and **Local AI (Ollama)** to automatically categorize and prioritize citizen reports (e.g., potholes, broken lights, sanitation issues) without relying on expensive cloud APIs.

![CivicPulse Dashboard](https://github.com/user-attachments/assets/placeholder-image.png)
*(Note: Replace this link with your actual screenshot after pushing)*

## 🚀 Features

*   **Real-time Reporting**: Citizens can submit reports with descriptions and images.
*   **Event-Driven Architecture**: Uses **Apache Kafka** to decouple ingestion from processing.
*   **Local AI Enrichment**: Integrates with **Ollama (Llama3)** to analyze text, assign categories (Infrastructure, Safety, Sanitation), and determine priority (Low, Medium, High).
*   **Live Dashboard**: A reactive frontend that updates incident statuses in real-time.
*   **Privacy-First**: Runs entirely locally—no data leaves your infrastructure.

## 🛠️ Tech Stack

*   **Backend**: Java 17, Spring Boot 3.2, Spring Data JPA
*   **Messaging**: Apache Kafka, Zookeeper
*   **AI/LLM**: Spring AI, Ollama (Llama3)
*   **Database**: PostgreSQL
*   **Frontend**: Vanilla JS, HTML5, CSS3 (Modern Dashboard UI)
*   **Containerization**: Docker, Docker Compose

## 🏗️ Architecture

```mermaid
graph LR
    User[Citizen] -->|Submit Report| API[Spring Boot API]
    API -->|Save PENDING| DB[(PostgreSQL)]
    API -->|Publish Event| Kafka{Kafka Topic}
    Kafka -->|Consume Event| AI_Service[AI Enrichment Service]
    AI_Service -->|Prompt| Ollama[Ollama (Llama3)]
    Ollama -->|Analysis| AI_Service
    AI_Service -->|Update Status| DB
    Dashboard[Live Dashboard] -->|Poll| API
```

## 🏃‍♂️ Getting Started

### Prerequisites

*   **Docker Desktop** (for Kafka & Postgres)
*   **Java 17+** & **Maven**
*   **Ollama** (installed locally)

### 1. Start Infrastructure
Spin up Kafka, Zookeeper, and PostgreSQL using Docker Compose.
*Note: Kafka runs on port `9098` to avoid conflicts.*

```bash
docker-compose up -d
```

### 2. Prepare AI Model
Ensure Ollama is running and pull the `llama3` model.

```bash
ollama pull llama3
ollama serve
```

### 3. Run the Backend
Navigate to the backend directory and start the Spring Boot application.

```bash
cd backend
./mvnw spring-boot:run
```
*The server will start on `http://localhost:8080`.*

### 4. Open the Dashboard
Open `frontend/index.html` in your web browser.

## 🧪 Testing the Flow

1.  **Submit a Report**: Type "There is a massive pothole on Main Street" and click Submit.
2.  **Watch the Magic**: The incident will appear as **PENDING**. Within seconds, the AI will analyze it, changing the status to **ANALYZED** and tagging it as **INFRASTRUCTURE** / **HIGH PRIORITY**.

## 🤝 Contributing

Contributions are welcome! Please fork the repository and submit a Pull Request.

## 📄 License

MIT License
