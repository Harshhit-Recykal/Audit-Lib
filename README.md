# Audit Library

A lightweight **audit logging library** which can be integrated accross different microservices. It captures `CREATE`, `UPDATE`, and `DELETE` actions using Spring AOP and sends the audit logs to **RabbitMQ** for downstream processing, consumed by the Central Audit logging system which stores the logs in DB.

## ✨ Features

- Intercepts and logs `CREATE`, `UPDATE`, `DELETE` operations using AOP.
- Sends audit events asynchronously to RabbitMQ.
- Easily configurable and non-intrusive.
---

## 📦 Installation Process for Local Setup

- Clone this repository locally. Then run,
- ```mvn clean install```
- Add below dependency into your pom.xml
```xml

<dependency>
    <groupId>com.recykal</groupId>
    <artifactId>audit</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

```

## ⚙️ Configuration

Add the following properties in your (the microservice) `application.yml`:

``` yaml
audit:
  enabled: true
  audit-pointcut-type: REQUEST_MAPPING

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    queue: audit.log.queue
    exchange: audit.log.topic
    routingKey: audit.log.route.key

  entity-matching:
    map:
      products: Product
```
- The `enabled:` is the flag to enable/disable the logging. You can disable it using `enabled: false`.

- The `audit-pointcut-type` is used to configure the pointcut strategy,  which is detailed in the section below.

- The `rabbitmq` is the configuration for the message queue to which the audit event is sent.

- The `entity-matching` is the mapping for the entity in the URI to the real entity name.More descriptively, we need to fetch entire detail about an object , so we need entity_id and entity_name corresponding to the JPA repository. We are assuming that the first keyword after ".../api/" in the request being sent is related to the entity. Thus this entity-matching is a global map that stores the key-value pairs where key is the possibleEntityName extracted from uri and value is the actual JPA repository with which it is associated. 


## 🔀 Pointcut Strategy Options

The audit logging library supports **three mutually exclusive strategies** for intercepting methods using AOP. Only one strategy is applied per microservice.

You can choose your preferred strategy by changing the ```audit-pointcut-type``` in the configuration described above.

Three strategies are listed below, you may configure any one of them
```
REQUEST_MAPPING,
SERVICE_CLASS,
AUDITABLE_ANNOTATION
```

For e.g. 
``` yaml
audit-pointcut-type: AUDITABLE_ANNOTATION
```

---

### 🏷️ Strategy 1 (```REQUEST_MAPPING```): Based on `@PostMapping`, `@PutMapping`, `@DeleteMapping`

This is the **default strategy**.

**Use Case**: Automatically capture all `POST`, `PUT`, `DELETE` controller calls.

Change the config to 
``` yaml
audit-pointcut-type: REQUEST_MAPPING
```


### 🏷️ Strategy 2 (```AUDITABLE_ANNOTATION```): Based on `@Auditable` Annotation

**Use Case:** Selectively audit methods by annotating them explicitly.

Change the config to 
``` yaml
audit-pointcut-type: AUDITABLE_ANNOTATION
```

In your service method you need to annotate like below, the action could be ```UPDTAE, CREATE, DELETE```

```java
@Auditable(actionType = ActionType.UPDATE)
public Product updateProduct(ProductDto dto) {
    ...
}
```

### 🏷️ Strategy 3 (```SERVICE_CLASS```): Based on All Methods in @Service Classes
**Use Case**: Automatically audit every public method inside @Service classes.

Change the config to 
``` yaml
audit-pointcut-type: SERVICE_CLASS
```

This intercepts all public methods in any class annotated with:

```java
@Service
```

## 📤 Audit Event
The audit event which is build and sent to rabbitmq structure is as belows

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    private String entityName;
    private String entityId;
    private String action;
    private String message;
    private LocalDateTime timestamp;
    private String changedBy;
    private String requestId;
    private Map<String, List<Object>> fieldChanges;
    private Object rawDataBefore;
    private Object rawDataAfter;
}
```

Sample audit event stored processed on consumer side
```json
{
    "id": 36,
    "entityName": "Product",
    "entityId": "14",
    "action": "UPDATE",
    "changedBy": "USER",
    "changedAt": "2025-06-30T17:15:16.679495",
    "requestId": "b8557c55-b07c-42b2-b2b7-a197c593c4cb",
    "fieldChanges": "{\"name\":[\"Laptop\",\"Mobile\"],\"description\":[\"Touch screen!!\",\"64MP camera\"],\"price\":[20.0,20000.0],\"quantity\":[2,20],\"imageUrl\":[\"https://www.laptop.com\",\"https://www.mobile.com\"],\"createdAt\":[\"2025-06-30T17:14:44.329165\",\"2025-06-30T17:14:44.329164781\"],\"updatedAt\":[\"2025-06-30T17:15:16.679495283\",null]}",
    "rawDataBefore": "{\"id\":14,\"name\":\"Mobile\",\"description\":\"64MP camera\",\"price\":20000.0,\"quantity\":20,\"imageUrl\":\"https://www.mobile.com\",\"createdAt\":\"2025-06-30T17:14:44.329164781\",\"updatedAt\":null}",
    "rawDataAfter": "{\"id\":14,\"name\":\"Laptop\",\"description\":\"Touch screen!!\",\"price\":20.0,\"quantity\":2,\"imageUrl\":\"https://www.laptop.com\",\"createdAt\":\"2025-06-30T17:14:44.329165\",\"updatedAt\":\"2025-06-30T17:15:16.679495283\"}"
}
````

