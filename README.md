## Project Overview

Project Overview</h3>
This is a Spring Boot application designed to manage and process live events. It integrates with Kafka for event
publishing and includes a mock external API for fetching event scores. The application is built using Java 21, Spring
Boot and Maven.

---

### Setup & Run Instructions

Prerequisites

- Java 21
- Maven 3.8+
- Kafka (running on localhost:9092)
- Docker (optional, for running Kafka locally)
- Postman or any REST client (for testing the API)

#### Steps to run and manually test the application

1. Clone the repository:

```bash
git clone <repository-url>
```

2. Start Kafka with topic named 'events':

Using Docker:

Use instructions from:
https://docs.docker.com/guides/kafka/

Or start your Kafka server manually on port 9092 and
name used topic 'events'.

3. Build the project:

```bash
mvn clean install
```

4. Run the application:

```bash
mvn spring-boot:run
```

5. Test the API by creating a new event:

REST API: http://localhost:8080/api/events/status
Use Postman to call the API with Body structure for POST operation:

```json
{
  "eventId": "1234",
  "isLive": true
}
```

6. Use Kafka Console to check messages with score updates:

```bash
 docker exec -ti kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server :9092 --topic events --from-beginning
```

---

### How to Run Tests

Run all tests:

```bash
mvn test
```

Run a specific test class:

```bash
mvn -Dtest=<TestClassName> test
```

Test coverage includes:

Unit tests for services and controllers.
Integration tests for Kafka producer functionality using an embedded Kafka broker.
---

### Design Decisions

The project is designed to be modular and maintainable, with clear separation of concerns. The following design
decisions were made:

Lombok was used for reducing boilerplate code.

Event Management:

Used a List<Event> in EventService to simulate an in-memory event storage accessible with EventController.
Added conflict handling for duplicate event creation. DTOs were not created due to the use of an in-memory list of
events, simplifying the implementation.

Mock External API:

Created MockExternalEventScoreController to simulate fetching live event scores.
Implemented a simple REST client to fetch scores from the mock API.

Scheduler:

Added LiveEventScheduler to periodically fetch and update scores for live events.
Used @Scheduled annotation for periodic tasks in LiveEventScheduler.

Kafka Integration:

Configured Kafka producer with retry logic using Spring Retry.
Used @Profile("!test") to exclude Kafka configuration during tests.

Used KafkaTemplate for sending messages to the 'events' topic.
Implemented retry logic for Kafka message publishing in KafkaProducerService.

Configuration:

All configurations were done with default settings to speed up the development process.

Testing:

Used Mockito for unit tests and Spring's embedded Kafka for integration tests.
Verified retry logic and Kafka message publishing.
---

### AI-Assisted / Google search referred parts

Base project structure was generated using Spring Initializr with dependencies for Spring Web, Spring Kafka, Lombok and
Spring Boot DevTools.

Googled sites/documentation used in the project:

- Spring Boot documentation for external API integration:
  https://docs.spring.io/spring-boot/reference/io/rest-client.html#io.rest-client.restclient
- Since it was author's first time working with Kafka, tutorial below was used for producer configuration and message
  sending:
  https://www.geeksforgeeks.org/spring-boot-integration-with-kafka/

AI Code Generation:

The LiveEventScheduler class and its @Scheduled method, the KafkaProducerService class @Retryable and @Recover
annotations
were AI-generated.
Verified by ensuring proper integration with EventService and testing its behavior.

Test Cases:

AI-assisted in generating test cases for EventService, KafkaProducerService and EventController.
Improved by adding edge cases and verifying mock interactions.

Other:

Some code syntax like Random.nextInt() were AI-generated.

Documentation:
README structure and content were AI-generated and refined for clarity and completeness.
All AI-generated code was reviewed, tested, and adjusted manually to meet project requirements.