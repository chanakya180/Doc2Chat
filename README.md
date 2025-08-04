[![Java CI with Maven](https://github.com/chanakya180/Doc2Chat/actions/workflows/maven.yml/badge.svg)](https://github.com/chanakya180/Doc2Chat/actions/workflows/maven.yml)

# Doc2Chat

Doc2Chat is a Spring Boot application that leverages the `langchain4j` library and OpenAI models to enable conversational question answering over your own documents. It ingests documents, stores their embeddings in a vector database, and provides a secure API for chat-based retrieval augmented generation (RAG).

## Features

- **Document Ingestion:** Recursively scans a specified directory for PDF files, parses and splits them, and stores their embeddings in a PostgreSQL database with `pgvector`.
- **Chat with Documents:** Allows users to ask questions and receive answers grounded in the ingested documents.
- **OpenAI Integration:** Uses OpenAI's GPT-3.5-turbo for chat and text-embedding-ada-002 for embeddings.
- **Secure Endpoints:** All API endpoints are protected with OAuth2/JWT authentication.
- **CORS Support:** Configurable CORS settings for cross-origin requests.
- **Error Handling:** Global exception handler for consistent error responses.

## Prerequisites

- Java 17
- Maven
- Docker
- An OpenAI API key
- Keycloak (recommended) or any OAuth2/JWT provider for authentication

## Keycloak Setup

Keycloak is included in the `docker-compose.yml` file and will be started alongside PostgreSQL.

1. **Start Keycloak and PostgreSQL:**
    ```bash
    docker-compose up -d
    ```
    This will start both the database and Keycloak. By default, Keycloak will be available at [http://localhost:8081](http://localhost:8082).

2. **Import Realm Configuration:**
    - Log in to the Keycloak admin console at [http://localhost:8082](http://localhost:8082).
    - Go to "Realm Settings" > "Import" and upload `src/main/resources/realm-export.json`.

3. **Create/Open a Client:**
    - Ensure a client exists for your app (e.g., `doc2chat-client`).
    - Set "Access Type" to "confidential" or "public" as needed.
    - Note the client ID and secret (if confidential).

4. **Create a User:**
    - Add a user and set a password.
    - Assign roles as needed.

5. **Obtain a Token:**
    - Use Keycloak's token endpoint to get a JWT for API requests.
    - Example (password grant, for development only):
      ```bash
      curl -X POST 'http://localhost:8082/realms/<realm-name>/protocol/openid-connect/token' \
        -d 'client_id=<client-id>' \
        -d 'username=<username>' \
        -d 'password=<password>' \
        -d 'grant_type=password'
      ```

## Getting Started

1. **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd Doc2Chat
    ```

2. **Set Environment Variables:**
    Before running the application, set the following environment variables:
    - `OPENAI_API_KEY`: Your OpenAI API key.

3. **Configure Authentication:**
    - By default, endpoints require a valid JWT in the `Authorization` header.
    - For Keycloak, update `application.yml` with your Keycloak server URL, realm, and client details if needed.
    - Use the provided `realm-export.json` for quick Keycloak setup.

4. **Start the services:**
    ```bash
    docker-compose up -d
    ```
    This will start both PostgreSQL and Keycloak.

5. **Run the application:**
    ```bash
    mvn spring-boot:run
    ```

## API Endpoints

All endpoints require a valid Bearer token (JWT) in the `Authorization` header.

- **Ingest Documents**
    ```
    POST /api/ingest
    ```
    Ingests documents from the API.

- **Chat with Documents**
    ```
    POST /api/chat
    Content-Type: application/json
    Authorization: Bearer <your-jwt-token>
    ```
    Request body:
    ```json
    {
      "message": "Your question here"
    }
    ```
    Response:
    ```json
    {
      "response": "AI-generated answer based on your documents"
    }
    ```

- **List Documents**
    ```
    GET /api/list
    ```
    Returns a list of ingested documents for the authenticated user.

## Directory Structure

- `src/main/java/com/chanakya/doc2chat/`
    - `Doc2ChatApplication.java`: Main entry point.
    - `controller/DocumentController.java`: REST API endpoints for document ingestion, chat, and listing.
    - `service/DocumentService.java` & `service/impl/DocumentServiceImpl.java`: Business logic for document processing and chat.
    - `ai/Assistant.java`: AI chat interface.
    - `config/`: Configuration for AI, embeddings, CORS, and security.
    - `security/`: OAuth2/JWT authentication and authorization utilities.
    - `dto/ChatRequest.java`, `dto/ChatResponse.java`: Request/response models for chat API.

## Configuration

- **application.yml:** Main application configuration.
- **Environment Variables:** Used for sensitive data (API keys, document path).
- **CORS:** Configurable via `CorsConfigurationProps`.
- **Embedding Store:** Configured for PostgreSQL with `pgvector`.

## Security

- All endpoints are protected with OAuth2/JWT authentication, with Keycloak as the recommended provider.
- Use the included `realm-export.json` to quickly set up a Keycloak realm for development.
- Custom claim and authority converters for flexible role mapping.
- Example (using curl with a Keycloak JWT token):
    ```bash
    curl -X POST http://localhost:8080/api/chat \
      -H "Authorization: Bearer <your-keycloak-jwt-token>" \
      -H "Content-Type: application/json" \
      -d '{"message": "What is in my documents?"}'
    ```

## Error Handling

- Consistent error responses via `GlobalExceptionHandler`.

## Technical Specifications

- **Framework:** Spring Boot 3.5.3
- **Language:** Java 17
- **AI Library:** `langchain4j` 1.1.0
- **AI Models:**
    - Chat: `gpt-3.5-turbo`
    - Embedding: `text-embedding-ada-002`
- **Database:** PostgreSQL with `pgvector`
- **Document Parser:** `ApachePdfBoxDocumentParser` for PDFs
- **Document Splitter:** `DocumentSplitters.recursive(2048, 0)`

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
