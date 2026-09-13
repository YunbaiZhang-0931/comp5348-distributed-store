# DeliveryGo Service API Documentation

## Overview
The DeliveryGo Service API provides endpoints for managing delivery operations, including creating deliveries and tracking delivery status updates. This service is built with Spring Boot and manages end-to-end delivery workflows with status tracking.

## Base URL
```
http://localhost:8080/api/delivery
```

## API Endpoints

### 1. Create Delivery
**Create a new delivery order**

- **Endpoint:** `POST /api/delivery/create/{orderNo}`
- **Path Parameters:**
  - `orderNo` (String, required): Unique order number identifier for the delivery
  
- **Request Body:**
  - None (order number is passed via path parameter)

- **Response:**
  - Status Code: `200 OK`
  - Body: Empty

- **Error Handling:**
  - Invalid order number format will return `400 Bad Request`
  - Duplicate order number will return `409 Conflict`
  - Server errors will return `500 Internal Server Error`

- **Example Request:**
  ```bash
  curl -X POST http://localhost:8080/api/delivery/create/ORDER-001
  ```

- **Side Effects:**
  - Creates a new Delivery record in the database
  - Initializes delivery tracking for the specified order
  - Sets initial delivery status to tracking

---

### 2. Create Delivery Status
**Update or create a delivery status record**

- **Endpoint:** `POST /api/delivery/create/status/{deliveryID}`
- **Path Parameters:**
  - `deliveryID` (Long, required): Unique delivery ID to create status for

- **Request Body:**
  ```json
  {
    "status": "IN_TRANSIT",
    "location": "Warehouse A"
  }
  ```
  - `status` (String, required): Status value (e.g., PENDING, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED, CANCELLED)
  - `location` (String, optional): Location associated with this status update

- **Response:**
  - Status Code: `200 OK`
  - Body: Empty

- **Error Handling:**
  - Invalid deliveryID will return `404 Not Found`
  - Invalid status format will return `400 Bad Request`
  - Server errors will return `500 Internal Server Error`

- **Example Request:**
  ```bash
  curl -X POST http://localhost:8080/api/delivery/create/status/1 \
    -H "Content-Type: application/json" \
    -d '{"status":"IN_TRANSIT","location":"Warehouse A"}'
  ```

- **Side Effects:**
  - Creates a new DeliveryStatus record linked to the specified delivery
  - Updates the delivery's status history
  - Records timestamp automatically

---

### 3. Get Delivery Details
**Retrieve all deliveries for a specific order number**

- **Endpoint:** `GET /api/delivery/{orderNo}`
- **Path Parameters:**
  - `orderNo` (String, required): Order number to retrieve

- **Response:**
  - Status Code: `200 OK`
  - Body: Array of Delivery objects with status history

- **Response Body Format:**
  ```json
  [
    {
      "id": 1,
      "version": 1,
      "orderNo": "ORDER-001",
      "deliveryStatuses": [
        {
          "id": 1,
          "status": "PICKED_UP",
          "dateTime": "2025-10-22T10:30:00",
          "location": "Warehouse A"
        },
        {
          "id": 2,
          "status": "IN_TRANSIT",
          "dateTime": "2025-10-22T11:00:00",
          "location": "Highway Route 1"
        },
        {
          "id": 3,
          "status": "DELIVERED",
          "dateTime": "2025-10-22T14:30:00",
          "location": "Recipient Address"
        }
      ]
    }
  ]
  ```

- **Error Handling:**
  - Invalid order number returns `404 Not Found`
  - Server errors return `500 Internal Server Error`

- **Example Request:**
  ```bash
  curl -X GET http://localhost:8080/api/delivery/ORDER-001
  ```

---

### 4. Get Delivery by Delivery ID (In Development)
**Retrieve a specific delivery by its delivery ID**

- **Endpoint:** `GET /api/delivery/deliveryNo/{deliveryId}`
- **Path Parameters:**
  - `deliveryId` (Long, required): Unique delivery identifier

- **Response:**
  - Status Code: `200 OK`
  - Body: Single Delivery object with status history

- **Response Body Format:**
  ```json
  {
    "id": 1,
    "version": 1,
    "orderNo": "ORDER-001",
    "deliveryStatuses": [
      {
        "id": 1,
        "status": "PICKED_UP",
        "dateTime": "2025-10-22T10:30:00",
        "location": "Warehouse A"
      }
    ]
  }
  ```

- **Error Handling:**
  - Invalid deliveryId returns `404 Not Found`
  - Server errors return `500 Internal Server Error`

- **Example Request:**
  ```bash
  curl -X GET http://localhost:8080/api/delivery/deliveryNo/1
  ```

- **Note:** This endpoint is currently under development.

---

## Data Models

### Delivery Model
Represents a delivery order in the system.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique delivery identifier (auto-generated) |
| version | Integer | Optimistic lock version for concurrent updates |
| orderNo | String | Order number reference (required, unique) |
| deliveryStatuses | Collection<DeliveryStatus> | List of status updates for this delivery |

### DeliveryStatus Model
Represents a status update in a delivery's lifecycle.

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique status record identifier (auto-generated) |
| version | Integer | Optimistic lock version for concurrent updates |
| status | String | Current status (e.g., PENDING, PICKED_UP, IN_TRANSIT, DELIVERED) (required) |
| dateTime | LocalDateTime | Timestamp of status update (auto-set to now) (required) |
| location | String | Location associated with this status (optional) |
| delivery | Delivery | Reference to the parent Delivery entity (required) |

---

## Delivery Status Lifecycle

The typical delivery status progression follows this pattern:

```
PENDING → PICKED_UP → IN_TRANSIT → DELIVERED
```

| Status | Description |
|--------|-------------|
| PENDING | Delivery order created, awaiting pickup |
| PICKED_UP | Package has been collected from origin |
| IN_TRANSIT | Package is on the way to destination |
| DELIVERED | Package has been successfully delivered |
| FAILED | Delivery attempt failed |
| CANCELLED | Delivery was cancelled |

---

## Status Codes

| Code | Description |
|------|-------------|
| 200 | Request successful |
| 400 | Bad request - invalid input |
| 404 | Resource not found |
| 409 | Conflict - duplicate order or business rule violation |
| 500 | Internal server error |

---

## Validation Rules

- **orderNo**: Must not be blank or null, should be unique per delivery
- **status**: Must not be blank or null
- **dateTime**: Automatically set, cannot be manually provided

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
- PostgreSQL Driver
- Lombok
- Spring Boot Starter Test

---

## Database Considerations

### Optimistic Locking
Both `Delivery` and `DeliveryStatus` entities use versioning for optimistic locking to handle concurrent update scenarios safely:

- Version field is automatically incremented on each update
- If concurrent modification is detected, a `StaleObjectStateException` will be thrown
- Clients should handle this exception and retry the operation

---

## Error Handling

The API follows standard HTTP status codes and returns error messages in appropriate formats:

1. **Invalid Order Number**: Empty or null orderNo
2. **Duplicate Order**: Attempting to create delivery for existing orderNo
3. **Not Found**: Order number doesn't exist in the system
4. **Concurrent Modification**: Version conflict detected during update
5. **Database Error**: Persistence layer failures

---

## Implementation Status

| Endpoint | Status | Notes |
|----------|--------|-------|
| `POST /api/delivery/create/{orderNo}` | ✅ Implemented | Fully functional |
| `POST /api/delivery/create/status/{deliveryID}` | ✅ Implemented | Fully functional, accepts optional location parameter |
| `GET /api/delivery/{orderNo}` | ✅ Implemented | Retrieves all deliveries for an order number |
| `GET /api/delivery/deliveryNo/{deliveryId}` | ✅ Implemented | Retrieves single delivery by delivery ID |

---

## API Usage Examples

### Create a New Delivery

```bash
curl -X POST http://localhost:8080/api/delivery/create/ORDER-001
```

### Add Status Update to Delivery

```bash
curl -X POST http://localhost:8080/api/delivery/create/status/1 \
  -H "Content-Type: application/json" \
  -d '{"status":"IN_TRANSIT","location":"Warehouse A"}'
```

### Retrieve Delivery by Order Number

```bash
curl -X GET http://localhost:8080/api/delivery/ORDER-001
```

### Retrieve Delivery by Delivery ID

```bash
curl -X GET http://localhost:8080/api/delivery/deliveryNo/1
```

### Expected Workflow

1. Create delivery order via `POST /api/delivery/create/{orderNo}`
2. System creates Delivery entity
3. Update status via `POST /api/delivery/create/status/{deliveryID}` with status and optional location
4. Retrieve full delivery history via `GET /api/delivery/{orderNo}` by order number
5. Or retrieve specific delivery via `GET /api/delivery/deliveryNo/{deliveryId}` by delivery ID

---

## Integration Points

This service is designed to integrate with:

- **Order Service**: Receives order events for delivery creation
- **Notification Service**: Sends delivery status updates to customers
- **Tracking Service**: Provides real-time delivery location tracking

---

## Notes

- All timestamp fields are in ISO 8601 format with local timezone
- Delivery status is immutable once created (new status records are added, not modified)
- Optimistic locking ensures data consistency in concurrent environments
- Status history is maintained for full delivery audit trail

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.1.0 | 2025-10-25 | Updated API documentation with implemented endpoints and correct parameters |
| 1.0.0 | 2025-10-22 | Initial API documentation |

---

## Future Enhancements

- Real-time delivery tracking with GPS coordinates
- Automated status transitions based on geolocation
- Customer notifications for each status change
- SLA monitoring and alerts
- Delivery assignment and routing optimization
- Integration with mapping services for ETA calculation

---

## Support

For issues or questions regarding the DeliveryGo Service API, please contact the development team or submit an issue in the project repository.
