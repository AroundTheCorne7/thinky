# Thinky - AI Learning Assistant Backend

A Spring Boot 3.x backend application that integrates with OpenRouter API to provide an AI-powered learning assistant that guides users to find answers themselves rather than providing direct solutions.

## Features

- REST API endpoint for guided learning interactions
- Conversation history persistence using H2 database
- Integration with OpenRouter API for AI responses
- Support for different AI models
- Socratic teaching approach that helps users discover answers themselves

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle
- OpenRouter API key

### Environment Setup

Set the OpenRouter API key as an environment variable:

```bash
export OPENROUTER_API_KEY=your_api_key_here
```

### Running the Application

```bash
./gradlew bootRun
```

The application will start on port 8080.

### Testing the API

You can test the `/ask` endpoint using curl:

```bash
curl -X POST http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "prompt": "Hello, how are you?",
    "history": [],
    "model": "google/gemma-3-12b-it:free"
  }'
```

For subsequent requests, you can either:
1. Use the same userId and omit the history (the backend will retrieve conversation history from the database)
2. Provide the history explicitly in the request (useful for stateless clients)

Example with explicit history:
```bash
curl -X POST http://localhost:8080/ask \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "prompt": "What can you help me with?",
    "history": [
      "Hello, how are you?",
      "I'm doing well! I'm here to help guide you through problems rather than giving direct answers. How can I assist you today?"
    ],
    "model": "google/gemma-3-12b-it:free"
  }'
```

## Database

The application uses an H2 in-memory database for storing conversation history. You can access the H2 console at:

```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:chatdb`
- Username: `sa`
- Password: (leave empty)

## License

This project is licensed under the MIT License - see the LICENSE file for details.
