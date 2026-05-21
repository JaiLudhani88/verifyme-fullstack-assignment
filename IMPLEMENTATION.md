# Invoice Calculation Backend - Implementation Summary

## Overview
The backend service is a Quarkus-based REST API that calculates invoice totals with multi-currency support using real-time exchange rates from the Frankfurter API.

## Architecture

### Technology Stack
- **Framework**: Quarkus 3.35.3
- **Language**: Java 17
- **Build Tool**: Maven
- **REST Client**: RESTEasy Reactive with Jackson
- **Exchange Rate API**: Frankfurter API (https://api.frankfurter.app)

### Project Structure
```
src/main/java/org/verifyme/invoice/
├── controller/
│   └── InvoiceResource.java          # REST endpoint handler
├── service/
│   └── InvoiceCalculationService.java # Business logic
├── client/
│   └── FrankfurterClient.java        # HTTP client for Frankfurter API
├── dto/
│   ├── InvoiceRequest.java           # Request body model
│   ├── Invoice.java                  # Invoice data model
│   ├── InvoiceLine.java              # Line item model
│   └── ExchangeRateResponse.java     # Exchange rate response model
├── exception/                         # Exception handling (reserved)
├── util/                             # Utilities (reserved)
└── GreetingResource.java             # Sample resource
```

## API Specification

### Endpoint: POST /invoice/total

#### Request Body
```json
{
  "invoice": {
    "currency": "NZD",
    "date": "2020-07-07",
    "lines": [
      {
        "description": "Intel Core i9",
        "currency": "USD",
        "amount": 700
      },
      {
        "description": "ASUS ROG Strix",
        "currency": "AUD",
        "amount": 500
      }
    ]
  }
}
```

#### Successful Response (200 OK)
- **Content-Type**: text/plain
- **Body**: Invoice total rounded to 2 decimal places
- **Example**: `1600.86`

#### Error Responses

**400 Bad Request**
- Missing or invalid required fields
- Invalid currency codes
- Zero or negative amounts
- Example: `Error: Invalid request body`

**404 Not Found**
- Exchange rate data unavailable for the requested currency/date combination
- Example: `Error: Exchange rate not found for USD to NZD on 2020-07-07`

**500 Internal Server Error**
- Unexpected server errors during calculation
- Example: `Error: Failed to fetch exchange rate: Connection timeout`

All error responses are text/plain with "Error: " prefix.

## Core Calculation Logic

### Algorithm
1. **Validate Input**: Check for null/missing required fields (invoice, date, currency, lines)
2. **Process Each Line**:
   - If line currency matches invoice currency: use amount as-is
   - If currencies differ:
     - Fetch exchange rate from Frankfurter API for the specified date
     - Apply rate to line amount
   - Round line total to 2 decimal places (HALF_UP)
3. **Calculate Total**: Sum all line totals
4. **Return**: Total rounded to 2 decimal places (HALF_UP)

### Rounding Rules
- **Exchange rates**: Rounded to 4 decimal places (HALF_UP)
- **Line totals**: Rounded to 2 decimal places (HALF_UP)
- **Invoice total**: Rounded to 2 decimal places (HALF_UP)
- **Rounding Mode**: HALF_UP (standard financial rounding)

### Example Calculation
```
Invoice Currency: NZD, Date: 2020-07-07

Line 1:
  Amount: 700 USD
  Rate USD→NZD: 1.5432 (rounded from Frankfurter)
  Converted: 700 × 1.5432 = 1080.24

Line 2:
  Amount: 500 AUD
  Rate AUD→NZD: 1.1236 (rounded from Frankfurter)
  Converted: 500 × 1.1236 = 561.80

Total: 1080.24 + 561.80 = 1642.04
```

## Implementation Details

### Key Classes

#### InvoiceResource (Controller)
- **Path**: `/invoice`
- **Endpoint**: POST `/total`
- Handles HTTP requests and routes to service
- Manages error responses with appropriate HTTP status codes
- Uses `@Inject` for dependency injection of service

#### InvoiceCalculationService
- Business logic for invoice total calculation
- Validates input data
- Calls FrankfurterClient to fetch exchange rates
- Performs mathematical calculations with BigDecimal for precision
- Returns formatted total as string

#### FrankfurterClient
- Interface using MicroProfile REST Client
- Configured via `quarkus.rest-client."frankfurter".url`
- Endpoint: `GET /{date}?from={sourceCurrency}&to={targetCurrency}`
- Returns exchange rate data with rates map

#### DTOs (Data Transfer Objects)
- **InvoiceRequest**: Wrapper containing invoice object
- **Invoice**: Contains currency, date, and list of lines
- **InvoiceLine**: Contains description, currency, and amount (BigDecimal)
- **ExchangeRateResponse**: Contains rates map from Frankfurter API

All DTOs use Jakarta validation annotations:
- `@NotBlank`: Currency and date fields
- `@NotNull`: Nested objects and lists
- `@Positive`: Amount values must be positive

## Configuration

### application.properties
```properties
quarkus.rest-client."frankfurter".url=https://api.frankfurter.app
quarkus.http.port=8080
```

### pom.xml Key Dependencies
- `quarkus-rest`: REST framework
- `quarkus-rest-client-jackson`: HTTP client with JSON support
- `quarkus-arc`: Dependency injection
- `quarkus-hibernate-validator`: Input validation
- `quarkus-smallrye-openapi`: OpenAPI documentation

## Building and Running

### Build the Application
```bash
mvn clean package -DskipTests
```

### Run the Application
```bash
java -jar target/code-assignment-1.0.0-SNAPSHOT-runner.jar
```

The application will start on `http://localhost:8080`

### Running in Development Mode
```bash
mvn quarkus:dev
```

### Running Tests
```bash
mvn test
```

## Error Handling Strategy

1. **Input Validation**: DTOs use Jakarta validation annotations
2. **Service Layer**: Validates data and throws `IllegalArgumentException` with descriptive messages
3. **Controller Layer**: Catches exceptions and maps to appropriate HTTP status codes:
   - 400: Bad Request (invalid input)
   - 404: Not Found (exchange rate unavailable)
   - 500: Internal Server Error (unexpected failures)

## Exchange Rate API Integration

### Frankfurter API Details
- **URL**: https://api.frankfurter.app
- **Endpoint**: `GET /{date}?from={source}&to={target}`
- **Date Format**: ISO 8601 (YYYY-MM-DD)
- **Response Format**: JSON with rates map
- **Rate Precision**: Provides rates with appropriate precision

### Supported Currencies
Common currencies including: USD, EUR, GBP, JPY, AUD, CAD, CHF, CNY, INR, MXN, NZD, SGD, ZAR, etc.

## Notes for Deployment

1. **Java Version**: Requires Java 17 or higher
2. **Maven Version**: 3.6.0 or higher recommended
3. **Dependencies**: All dependencies managed by Maven BOM
4. **Build Artifacts**: JAR with all dependencies bundled by Quarkus Maven plugin
5. **Port**: Configurable via `quarkus.http.port` property

## Future Enhancements

1. Add caching for exchange rates
2. Add request rate limiting
3. Add detailed audit logging
4. Support batch invoice processing
5. Add webhook support for exchange rate updates
6. Add database persistence for invoice history
