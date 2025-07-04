[![Java CI with Maven](https://github.com/chanakya180/Doc2Chat/actions/workflows/maven.yml/badge.svg)](https://github.com/chanakya180/Doc2Chat/actions/workflows/maven.yml)
# Doc2Chat

This project is a Spring Boot application that demonstrates how to use the `langchain4j` library to build a chat application that can answer questions based on a set of documents.

## Features

*   **Document Ingestion:** Ingests documents from a specified directory and stores their embeddings in a PostgreSQL database with `pgvector`.
*   **Chat with Documents:** Allows users to chat with the ingested documents and get answers to their questions.
*   **OpenAI Integration:** Uses OpenAI's language models for chat and text embedding.

## Prerequisites

*   Java 17
*   Maven
*   Docker
*   An OpenAI API key

## Getting Started

1.  **Clone the repository:**

    ```bash
    git clone <repository-url>
    cd langchain4j-impl
    ```

2.  **Set Environment Variables:**

    Before running the application, you need to set the following environment variables:

    *   `OPENAI_API_KEY`: Your OpenAI API key.
    *   `DOCS_PATH`: The absolute path to the directory containing the documents you want to ingest.

3.  **Start the database:**

    ```bash
    docker-compose up -d
    ```

4.  **Run the application:**

    ```bash
    mvn spring-boot:run
    ```

## API Endpoints

*   `GET /api/documents/ingest`

    Ingests documents from the path specified in the `DOCS_PATH` environment variable.

*   `GET /api/documents/chat`

    Allows chatting with the ingested documents. The request body should be a JSON object with the following format:

    ```json
    {
        "message": "Your question here"
    }
    ```

## Technical Specifications

This project uses the following technologies and libraries:

*   **Framework:** Spring Boot 3.5.3
*   **Language:** Java 17
*   **AI Library:** `langchain4j` 1.1.0
*   **AI Models:**
    *   Chat Model: `gpt-3.5-turbo`
    *   Embedding Model: `text-embedding-ada-002`
*   **Database:** PostgreSQL with `pgvector` extension
*   **Document Parser:** `ApachePdfBoxDocumentParser` for parsing PDF files.
*   **Document Splitter:** `DocumentSplitters.recursive(2048, 0)` is used to split documents into chunks of 2048 characters with no overlap.

### Document Ingestion

The document ingestion process is handled by the `DocumentServiceImpl` class. It recursively scans the directory specified by the `docs.path` property (set via the `DOCS_PATH` environment variable) for PDF files. It then parses them, splits them into text segments, and stores their embeddings in the PostgreSQL database.

### Chat with Documents

When a user sends a message, the application creates an embedding of the message and searches the embedding store for the most relevant document segments. These segments are then used as context in a prompt that is sent to the chat model to generate a response.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
