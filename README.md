# Leaderboard Platform

A high-performance, multi-tenant leaderboard platform with real-time score updates and query capabilities using feature-based scoring.

## Prerequisites

- Docker and Docker Compose
- Java 17 or higher
- Python 3.8+ (for test scripts)
- Maven

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd leaderboard
   ```

2. **Start the infrastructure**
   ```bash
   docker-compose up -d
   ```
   This will start:
   - PostgreSQL database
   - Redis
   - Kafka (for event streaming)

3. **Run database migrations**
   ```bash
   python scripts/run_migration.py
   ```
   This will:
   - Create the required database tables
   - Set up necessary indexes
   - Use default credentials (can be customized with command-line arguments if needed)

4. **Build the application**
   ```bash
   mvn clean package
   ```

5. **Run the application**
   ```bash
   java -jar target/leaderboard-0.0.1-SNAPSHOT.jar
   ```

## API Endpoints

### User Score API
- `POST /api/v1/user-score` - Update a user's score using feature-based calculation
  ```json
  {
    "leaderboardInstanceId": "leaderboard1",
    "userId": "user123",
    "features": {
      "numberOfPayments": 5,
      "totalAmount": 1000
    }
  }
  ```

### Leaderboard API
- `GET /api/v1/leaderboard/user-score/top?leaderboardInstanceId=leaderboard1&limit=10` - Get top users
- `GET /api/v1/leaderboard/user-score/{userId}?leaderboardInstanceId=leaderboard1` - Get rank for a specific user

## Testing with Test Script

1. **Install Python dependencies**
   ```bash
   pip install -r scripts/requirements.txt
   ```

2. **Run the test script**
   ```bash
   python scripts/test_leaderboard.py
   ```

   This will:
   - Start the application (if not already running)
   - Clear existing test data
   - Insert test users with scores
   - Run test queries to verify functionality

## API Endpoints

### Write API
- `POST /api/v1/leaderboard/write/score` - Update a user's score directly
  ```json
  {
    "leaderboardInstanceId": "leaderboard1",
    "userId": "user123",
    "score": 100.0
  }
  ```

- `POST /api/v1/leaderboard/write/score/features` - Update score using feature-based calculation
  ```json
  {
    "leaderboardInstanceId": "leaderboard1",
    "userId": "user123",
    "features": {
      "numberOfPayments": 5,
      "totalAmount": 1000.0
    }
  }
  ```

### Read API
- `GET /api/v1/leaderboard/read/leaderboard1/top?limit=10` - Get top 10 users
- `GET /api/v1/leaderboard/read/leaderboard1/rank/user123` - Get rank for a specific user

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database URL | `jdbc:postgresql://localhost:5432/leaderboard` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `leaderboard` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `leaderboard123` |
| `SPRING_REDIS_HOST` | Redis host | `localhost` |
| `SPRING_REDIS_PORT` | Redis port | `6379` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka bootstrap servers | `localhost:9092` |

## Development

### Project Structure

```
├── src/
│   ├── main/
│   │   ├── kotlin/com/platforms/leaderboard/
│   │   │   ├── common/            # Common components
│   │   │   ├── config/            # Configuration classes
│   │   │   ├── configservice/     # Leaderboard configuration
│   │   │   ├── read/              # Read API implementation
│   │   │   └── write/             # Write API implementation
│   │   └── resources/             # Configuration files
│   └── test/                      # Test code
└── scripts/                       # Test and utility scripts
```

### Building and Running

- **Build**: `mvn clean package`
- **Run**: `java -jar target/leaderboard-0.0.1-SNAPSHOT.jar`
- **Run with dev profile**: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

## Monitoring

The application exposes Prometheus metrics and health endpoints:

- Metrics: `http://localhost:8080/actuator/prometheus`
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`

## Troubleshooting

### Common Issues

1. **Port conflicts**
   - Ensure ports 8080 (app), 5432 (PostgreSQL), 6379 (Redis), and 9092 (Kafka) are available

2. **Database connection issues**
   - Verify PostgreSQL is running and accessible
   - Check credentials in `application.properties`

3. **Redis connection issues**
   - Ensure Redis is running and accessible
   - Check Redis configuration in `application.properties`

## License

[Specify License]
