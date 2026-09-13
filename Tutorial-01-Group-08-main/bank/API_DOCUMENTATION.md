# Bank Service API Documentation

## Overview
The Bank Service API provides endpoints for managing financial transactions, including payment processing and transaction status tracking. This service is built with Spring Boot and manages account-to-account transfers with proper validation and status tracking.

## Base URL
```
http://localhost:8080/api/transaction
```

## API Endpoints

### 1. Process Payment
**Process a payment transaction between two accounts**

- **Endpoint:** `POST /api/transaction/pay/{orderNo}`
- **Path Parameters:**
  - `orderNo` (String, required): Unique order number identifier for the transaction
  
- **Request Body:**
  ```json
  {
    "orderNo": "ORDER-001",
    "fromAccountNo": "ACC-FROM-001",
    "toAccountNo": "ACC-TO-001",
    "amount": 150.50
  }
  ```

- **Request Body Fields:**
  - `orderNo` (String, required, not blank): Order number for the transaction
  - `fromAccountNo` (String, required, not blank): Source account number
  - `toAccountNo` (String, required, not blank): Destination account number
  - `amount` (Double, required): Amount to transfer (must be positive)

- **Response:**
  - Status Code: `200 OK`
  - Body: Empty

- **Error Handling:**
  - Invalid request body format will return `400 Bad Request`
  - Transaction processing errors will return `500 Internal Server Error`

- **Example Request:**
  ```bash
  curl -X POST http://localhost:8080/api/transaction/pay/ORDER-001 \
    -H "Content-Type: application/json" \
    -d '{
      "orderNo": "ORDER-001",
      "fromAccountNo": "ACC-FROM-001",
      "toAccountNo": "ACC-TO-001",
      "amount": 150.50
    }'
  ```

---

### 2. Get Payment Status
**Retrieve the status of a transaction by order number**

- **Endpoint:** `GET /api/transaction/status/{orderNo}`
- **Path Parameters:**
  - `orderNo` (String, required): Order number to check status for

- **Response:**
  - Status Code: `200 OK`
  - Body: 
    ```
    PENDING | COMPLETED | FAILED
    ```

- **Response Description:**
  - `PENDING`: Transaction is being processed
  - `COMPLETED`: Transaction has been successfully completed
  - `FAILED`: Transaction has failed

- **Error Handling:**
  - Invalid order number will return `404 Not Found` or appropriate error message
  - Server errors will return `500 Internal Server Error`

- **Example Request:**
  ```bash
  curl -X GET http://localhost:8080/api/transaction/status/ORDER-001
  ```

- **Example Response:**
  ```
  COMPLETED
  ```

---

### 3. Health Check
**Basic endpoint to verify service is running**

- **Endpoint:** `GET /api/transaction/`
- **Response:**
  - Status Code: `200 OK`
  - Body: 
    ```
    Hello World
    ```

- **Example Request:**
  ```bash
  curl -X GET http://localhost:8080/api/transaction/
  ```

---

## Data Models

### Account Model
Represents a bank account in the system.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique account identifier (auto-generated) |
| name | String | Account holder's name (required) |
| balance | Double | Current account balance (default: 0.0) |
| fromTransactionRecords | Collection<TransactionRecord> | List of outgoing transactions |
| toTransactionRecords | Collection<TransactionRecord> | List of incoming transactions |

### TransactionRecord Model
Represents a completed transaction between two accounts.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique transaction identifier (auto-generated) |
| orderNo | String | Order number reference (required) |
| fromAccount | Account | Source account (required) |
| toAccount | Account | Destination account (required) |
| amount | Double | Transaction amount (required) |
| dateTime | LocalDateTime | Timestamp of transaction (auto-set to now) |
| status | String | Transaction status: PENDING, COMPLETED, FAILED (required) |

---

## Status Codes

| Code | Description |
|------|-------------|
| 200 | Request successful |
| 400 | Bad request - validation error |
| 404 | Resource not found |
| 500 | Internal server error |

---

## Validation Rules

- **orderNo**: Must not be blank or null
- **fromAccountNo**: Must not be blank or null
- **toAccountNo**: Must not be blank or null
- **amount**: Must be a valid positive number

---

## Technology Stack

- **Framework**: Spring Boot 3.5.6
- **Java Version**: 17
- **Database**: PostgreSQL
- **Build Tool**: Maven

---

## Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Actuator
- Spring Boot Starter Validation
- PostgreSQL Driver
- Lombok
- Spring Boot Starter Test

---

## Error Handling

The API follows standard HTTP status codes and returns error messages in appropriate formats. Common error scenarios:

1. **Invalid Transaction Details**: Validation fails on required fields
2. **Insufficient Funds**: Account doesn't have enough balance
3. **Invalid Account**: Source or destination account doesn't exist
4. **Database Error**: Transaction record persistence fails

---

## Notes

- All timestamp fields are in ISO 8601 format with local timezone
- Transactions are atomic operations - either fully completed or fully rolled back
- Account balances are updated immediately upon successful transaction completion
- Transaction history is maintained for audit purposes

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2025-10-22 | Initial API documentation |

---

## Support

For issues or questions regarding the Bank Service API, please contact the development team or submit an issue in the project repository.
