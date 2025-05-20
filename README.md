# RiftStats

A League of Legends champion statistics application that fetches and analyzes champion data from matches across 15 regions using the Riot Games API. Currently, RiftStats focuses on Challenger matches in solo/duo queue.

## ⚡️ Tech Stack

### Client
- React
- TypeScript
- TailwindCSS
- Mantine UI
- Vite

### Server
- Java 17
- Spring Boot
- RabbitMQ
- PostgreSQL


## ➡️ Data Flow
1. RiotService retrieves Challenger players from each region
2. For each player, their latest match IDs are fetched and published to RabbitMQ
3. MatchFetchService listens for match IDs from RabbitMQ
4. MatchFetchService fetches match data for each match ID and publishes to RabbitMQ
5. MatchProcessingService listens for match data from RabbitMQ
6. MatchProcessingService processes each match and updates the database accordingly
7. Clients request data via REST endpoints
8. Controllers retrieve and format data for presentation


## ⚙️ Local Setup

Clone the repository
```
git clone https://github.com/andreluong/RiftStats.git
```

### Client
1. Install dependencies in frontend folder
    ```
    npm install
    ```
3. Create a `.env` file with the API url:
    ```
    VITE_API_URL=http://localhost:8080
    ```
2. Run in development mode
    ```
    npm run dev
    ```

### Server
1. Create an `application.properties` file inside the resources directory. Refer to `example.application.properties` for setting up environment variables
2. Build with Gradle
    ```
    ./gradlew build
    ```
3. Launch a RabbitMQ instance using Docker Compose
    ```
    docker compose up
    ```
4. Run the application
