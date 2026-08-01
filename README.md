# 🛒 E-Commerce Microservices Platform

A production-grade distributed e-commerce system built with **Spring Boot** and **Spring Cloud**, demonstrating real-world microservices patterns including service discovery, API gateway, inter-service communication, async messaging, circuit breaking, distributed tracing, and JWT security.

---

## 📐 Architecture Overview

```
                        ┌─────────────────────────────┐
                        │     CLIENT (Postman/UI)      │
                        └──────────────┬──────────────┘
                                       │
                        ┌──────────────▼──────────────┐
                        │      API GATEWAY  :8080      │
                        │  JWT Auth · Routing · LB     │
                        └──┬──────────┬───────────┬───┘
                           │          │           │
             ┌─────────────▼─┐  ┌─────▼──────┐  ┌▼────────────┐
             │  USER-SERVICE  │  │PRODUCT-SVC │  │ ORDER-SVC   │
             │    :8081       │  │   :8082    │  │   :8083     │
             │  JWT Generate  │  │  Catalog   │  │Feign+Circuit│
             └───────────────┘  └────────────┘  └──────┬──────┘
                                                        │ Kafka
                                                        │ ORDER_CREATED
                                                 ┌──────▼──────┐
                                                 │ PAYMENT-SVC │
                                                 │   :8084     │
                                                 │Kafka Consumer│
                                                 └─────────────┘

                        ┌─────────────────────────────┐
                        │   SERVICE REGISTRY (Eureka)  │
                        │          :8761               │
                        └─────────────────────────────┘

                        ┌─────────────────────────────┐
                        │     KAFKA BROKER  :9092      │
                        │   Topic: order-created       │
                        └─────────────────────────────┘

                        ┌─────────────────────────────┐
                        │      ZIPKIN  :9411           │
                        │   Distributed Tracing        │
                        └─────────────────────────────┘
```

---

## 🧩 Services

| Service | Port | Responsibility |
|---|---|---|
| `service-registry` | 8761 | Netflix Eureka — service discovery & registration |
| `api-gateway` | 8080 | Spring Cloud Gateway — routing, JWT auth, load balancing |
| `user-service` | 8081 | User registration, login, JWT token generation |
| `product-service` | 8082 | Product catalog management |
| `order-service` | 8083 | Order creation, Feign client, Kafka producer |
| `payment-service` | 8084 | Kafka consumer, payment processing |
| `common-module` | — | Shared library — events, DTOs shared across services |

---

## 🔧 Tech Stack

| Category | Technology |
|---|---|
| Core Framework | Spring Boot 3.3.x |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway (Servlet) |
| Inter-Service HTTP | OpenFeign + Spring Cloud LoadBalancer |
| Async Messaging | Apache Kafka |
| Resilience | Resilience4j Circuit Breaker |
| Distributed Tracing | Micrometer + Zipkin + Brave |
| Security | Spring Security + JJWT 0.12.x |
| Persistence | Spring Data JPA + H2 (per service) |
| Build Tool | Maven |
| Language | Java 21 |

---

## 🏗️ Key Patterns Implemented

### 1. Service Discovery
Every service registers with Eureka on startup. Services call each other by name — never by hardcoded IP/port.

```
order-service → asks Eureka → "where is product-service?" → :8082
```

### 2. API Gateway Routing
All external traffic enters through a single port. Gateway routes by path:

```yaml
routes:
  - id: product-service
    uri: lb://product-service
    predicates:
      - Path=/products/**
  - id: order-service
    uri: lb://order-service
    predicates:
      - Path=/order/**
```

### 3. Inter-Service Communication (Feign)
`order-service` calls `product-service` using a declarative Feign client — looks like a local method call but triggers a real HTTP request resolved via Eureka + LoadBalancer.

```java
@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("/products/{id}")
    ProductResponse getById(@PathVariable Long id);
}
```

### 4. Price Snapshot Pattern
When an order is created, `order-service` copies `productName` and `priceAtPurchase` from `product-service` into the order record. This ensures historical accuracy — future price changes don't affect past orders.

```java
order.setPriceAtPurchase(product.getPrice()); // snapshot at order time
order.setProductName(product.getName());
```

### 5. Circuit Breaker (Resilience4j)
If `product-service` is down, `order-service` fails fast instead of waiting — preventing cascading failures across the system.

```
CLOSED → normal flow
OPEN   → fail fast, return 503 immediately (after 40% failure rate)
HALF-OPEN → test recovery with limited requests
```

```java
@CircuitBreaker(name = "productService", fallbackMethod = "fallbackProduct")
public ProductResponse getProductDetails(Long id) {
    return productClient.getById(id);
}
```

### 6. Async Event-Driven Communication (Kafka)
After saving an order, `order-service` publishes an `ORDER_CREATED` event to Kafka and returns a response to the client immediately. `payment-service` consumes the event independently in the background.

```
order-service  →  Kafka (order-created topic)  →  payment-service
     ↓
client gets instant response
```

### 7. Distributed Tracing (Zipkin)
Every request gets a `traceId` that propagates across all service hops via HTTP headers. Zipkin dashboard at `http://localhost:9411` shows the complete request journey with timing per service.

```
TraceId: abc123
├── api-gateway        5ms
├── order-service     24ms
│   └── product-service 5ms
└── payment-service   45ms (async)
```

### 8. JWT Security at Gateway
Gateway validates JWT on every request using a custom `OncePerRequestFilter`. Public endpoints (`/users/register`, `/users/login`) bypass validation. User claims are extracted and forwarded as headers.

```
Authorization: Bearer <token>
        ↓
Gateway validates → extracts claims
        ↓
X-User-Id: 123
X-User-Role: USER
        ↓
downstream services read headers directly
```

---

## 📁 Project Structure

```
ecommerce/
├── service-registry/          # Eureka Server
├── api-gateway/               # Gateway + JWT Filter
├── user-service/              # Auth + JWT Generation
├── product-service/           # Product Catalog
├── order-service/             # Orders + Feign + Kafka Producer
├── payment-service/           # Kafka Consumer
└── common-module/             # Shared: OrderEvent, DTOs
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- Apache Kafka (KRaft mode, no Zookeeper needed)
- Zipkin Server

### 1. Start Kafka (KRaft mode)
```bash
# Format storage (first time only)
bin/windows/kafka-storage.bat format -t <UUID> -c config/kraft/server.properties --standalone

# Start Kafka
bin/windows/kafka-server-start.bat config/kraft/server.properties

# Create topic
bin/windows/kafka-topics.bat --create --topic order-created --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### 2. Start Zipkin
```bash
java -jar zipkin-server-*exec.jar
```

### 3. Start Services (in order)
```bash
# 1. Service Registry
cd service-registry && mvn spring-boot:run

# 2. Core Services
cd user-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run

# 3. Gateway (last)
cd api-gateway && mvn spring-boot:run
```

### 4. Verify Everything is Running
- Eureka Dashboard: http://localhost:8761
- Zipkin Dashboard: http://localhost:9411
- API Gateway: http://localhost:8080

---

## 📡 API Reference

All requests go through the **API Gateway at `localhost:8080`**.

### Auth (Public — no JWT needed)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/users/register` | Register a new user |
| POST | `/users/login` | Login and receive JWT token |

**Register:**
```json
POST /users/register
{
  "userName": "Ayush",
  "email": "ayush@example.com",
  "password": "secret123",
  "age": 21
}
```

**Login:**
```json
POST /users/login
{
  "email": "ayush@example.com",
  "password": "secret123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Products (JWT required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/products` | Get all products |
| GET | `/products/{id}` | Get product by ID |
| POST | `/products` | Create a product |

**Create Product:**
```json
POST /products
Authorization: Bearer <token>
{
  "name": "iPhone 15",
  "price": 79999.0,
  "stock": 50
}
```

### Orders (JWT required)

| Method | Endpoint | Description |
|---|---|---|
| POST | `/order/create` | Create an order |

**Create Order:**
```json
POST /order/create
Authorization: Bearer <token>
{
  "userId": 1,
  "productId": 1,
  "quantity": 2,
  "status": "PLACED"
}

Response:
{
  "id": 1,
  "userId": 1,
  "productId": 1,
  "productName": "iPhone 15",
  "priceAtPurchase": 79999.0,
  "quantity": 2,
  "status": "PLACED"
}
```

After this — check `payment-service` logs:
```
Received Order event: 1
Processing payment
Payment Completed for Order: 1
```

### Payments

| Method | Endpoint | Description |
|---|---|---|
| GET | `/payments/{orderId}` | Get payment by order ID |

---

## ⚙️ Configuration

Each service has its own `application.yml`. Key configurations:

**Eureka Registration (all services):**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

**Kafka Producer (order-service):**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

**Kafka Consumer (payment-service):**
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: payment-group
      auto-offset-reset: earliest
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"
        spring.json.value.default.type: com.microservicesprojectone.common_module.events.OrderEvent
```

**Circuit Breaker (order-service):**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      productService:
        failure-rate-threshold: 40
        wait-duration-in-open-state: 10s
        sliding-window-size: 10
        permitted-number-of-calls-in-half-open-state: 3
```

**Zipkin Tracing (all services):**
```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

---

## 🔒 Security Flow

```
1. User registers → password hashed with BCrypt → saved to DB
2. User logs in → credentials verified → JWT generated (24hr expiry)
3. Client sends JWT in Authorization header with every request
4. API Gateway intercepts all requests via JwtAuthenticationFilter
5. Public endpoints (/users/register, /users/login) bypass validation
6. All other endpoints → JWT validated → userId & role extracted
7. Gateway adds X-User-Id and X-User-Role headers
8. Downstream services read headers — no JWT logic needed
```

---

## 🧠 Design Decisions

**Why does each service have its own DB?**
Loose coupling — a schema change in one service never breaks another. Services own their data completely.

**Why snapshot price at order creation?**
Product prices change. An order is a historical record — it must preserve what the customer actually paid, not the current price.

**Why Kafka instead of REST for order → payment?**
Payment processing doesn't need to block the order creation response. Async messaging decouples services and improves resilience — if payment-service is down, messages queue up and process when it recovers.

**Why validate JWT only at the gateway?**
Single responsibility. Gateway handles authentication centrally — no JWT logic duplicated across services, no secret key distributed everywhere.

---

## 📊 Observability

Open Zipkin at `http://localhost:9411` after making requests to visualize:
- Complete request traces across services
- Time spent in each service
- Service dependency graph
- Failed request traces

---

## 🗺️ Roadmap

- [ ] Complete JWT filter in API Gateway
- [ ] Docker Compose for entire stack
- [ ] Push to GitHub with full documentation
- [ ] Saga pattern for distributed transactions
- [ ] Centralized Config Server
- [ ] Notification service (email on order)

---

**Ayush Kumar Maury**
- GitHub: [@Ayush4O4](https://github.com/Ayush4O4)
- Email: ayushagzp@gmail.com
