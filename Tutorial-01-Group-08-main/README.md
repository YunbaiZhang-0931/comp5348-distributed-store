#  COMP5348 Group Project — Online Store System

This project implements a **distributed enterprise-scale online store** consisting of six Spring Boot microservices that communicate asynchronously via RabbitMQ.
It demonstrates **high availability**, **fault-tolerant orchestration**, and **asynchronous event-driven workflows** across multiple subsystems.

---

##  System Overview

| Service             | Description                                                                                                            | Port |
| ------------------- | ---------------------------------------------------------------------------------------------------------------------- | ---- |
|  **store**        | Handles user authentication, order creation, and main business logic. Publishes events to trigger downstream services. | 8080 |
|  **orchestrator** | Coordinates workflows between services. Listens to stock, payment, and delivery events.                                | 8084 |
|  **warehouse**   | Manages warehouse data and stock levels. Responds to reservation and release requests.                                 | 8081 |
|  **bank**         | Simulates payment and refund operations between accounts.                                                              | 8082 |
|  **deliveryco**   | Simulates delivery status updates through stages (pickup, in transit, delivered).                                      | 8083 |
|  **emailservice** | Simulates sending email notifications to customers (printed in console).                                               | 8085 |

---

##  Architecture Summary

All services communicate via **RabbitMQ topics** defined in the shared `Topics` class.

### Core Workflow

1. Customer places an order (Store → Orchestrator)
2. Warehouse reserves stock (Warehouse → Orchestrator)
3. Orchestrator requests payment (Orchestrator → Bank)
4. Bank confirms payment (Bank → Orchestrator)
5. Orchestrator triggers delivery (Orchestrator → DeliveryCo)
6. DeliveryCo updates status → EmailService sends notifications
7. If any step fails → Orchestrator rolls back and compensates (refund + stock release)

---

##  Technologies Used

* Java 17, Spring Boot 3.x
* Spring Data JPA (H2 / MySQL)
* RabbitMQ (AMQP message broker)
* Lombok, Jackson, RestTemplate
* Maven multi-module build
* Docker (optional, for RabbitMQ)

---

##  Running the System

### 1️⃣ Start RabbitMQ

If you don’t have RabbitMQ running, start it using Docker:

docker run -d --name rabbitmq 
-p 5672:5672 -p 15672:15672 
rabbitmq:management

RabbitMQ management UI: [http://localhost:15672](http://localhost:15672)
Default credentials: `guest / guest`

---

### 2️⃣ Start Each Service

Run each service in separate terminals:

cd store && mvn spring-boot:run
cd orchestrator && mvn spring-boot:run
cd warehouse && mvn spring-boot:run
cd bank && mvn spring-boot:run
cd deliveryco && mvn spring-boot:run
cd emailservice && mvn spring-boot:run

**Recommended startup order:**

1. RabbitMQ
2. Warehouse
3. Bank
4. DeliveryCo
5. EmailService
6. Store
7. Orchestrator

---

## 🧾 Default Data Initialization

Each service auto-initializes demo data at startup:

| Service         | Initialized Data                                                       |
| --------------- | ---------------------------------------------------------------------- |
|  Bank         | Two accounts: `"2"` (Store) and `"1"` (Customer) with initial balances |
|  Warehouse   | Two warehouses with stock for `ITEM-001`, `ITEM-002`, `ITEM-003`       |
|  Store        | Three default items automatically inserted                             |
|  EmailService | Prints all emails to console                                           |
|  DeliveryCo   | Randomly updates delivery progress every few seconds                   |

---

## 👤 Demo User

| Field    | Value      |
| -------- | ---------- |
| Username | `customer` |
| Password | `COMP5348` |

You can log in to the Store front-end (if available) or use Postman to place an order.

---

##  Typical Order Flow

1. `POST /orders` → Create a new order in Store
2. Store publishes `ORDER_CREATED` event
3. Warehouse reserves stock → emits `STOCK_RESERVED`
4. Orchestrator requests payment → emits `PAYMENT_REQUEST`
5. Bank confirms → emits `PAYMENT_COMPLETED`
6. Orchestrator triggers delivery → `DELIVERY_REQUEST`
7. DeliveryCo updates → EmailService logs notifications

---

##  Cancellation & Refunds

* Customers can cancel orders **before delivery request** is sent.
* Orchestrator will:

  * Notify Warehouse to release stock
  * Notify Bank to refund payment
  * Notify EmailService to inform customer

---

##  Fault Tolerance Scenarios

Demonstrated failure handling:

1. **Bank unavailable** → Orchestrator retries or marks order as failed
2. **Warehouse reservation failure** → Refund and cancellation
3. **DeliveryCo delay/loss** → Logged and user notified via email

---

##  Notes

* All components are **event-driven** via RabbitMQ.
* EmailService, DeliveryCo, and Bank are **simulated** (no real external API).
* Data is persisted in embedded H2 databases for simplicity.
* To reset the system, stop all services and delete `/data` or `/h2` files.

---

**© 2025 University of Sydney – COMP5348 Group Project**
