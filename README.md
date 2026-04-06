# Nodes

A Spring Boot REST API for managing a simple parent-child node hierarchy.

The application lets you:
- create a root node
- add a child node under an existing parent
- fetch a node and its children
- move a child node to another parent
- delete a node

The project exposes both **JSON** and **XML** request/response formats through the same REST endpoints. The controller is mapped under `/nodes`, and it is configured to produce JSON and XML responses.

## Tech stack

- Java 17
- Spring Boot 4.0.5
- Spring Web
- Spring Data JPA
- H2 in-memory database
- springdoc OpenAPI / Swagger UI
- Jackson XML support

These dependencies are defined in the project `pom.xml`.

## Project structure

- `NodeManagementController` – REST endpoints under `/nodes`
- `NodeServiceImpl` – service layer for mapping entities to DTO responses
- `NodeDaoImpl` – persistence logic and validation rules
- `NodeRepo` – Spring Data JPA repository for `Node`
- `Node` – entity with self-referencing parent/children relationship using JPA

## Prerequisites

- JDK 17
- Maven 3.9+ recommended
- IntelliJ IDEA

## Open in IntelliJ

1. Clone or download the project.
2. Open IntelliJ IDEA.
3. Choose **Open** and select the project folder.
4. Let IntelliJ import the Maven project.
5. Make sure the Project SDK is set to **Java 17**.

## How to run the app

### Option 1: run from IntelliJ

Run the main Spring Boot class from IntelliJ.

Main class:

```java
com.nodemanagement.system.SystemApplication
```

The project includes a standard Spring Boot application entry point.

### Option 2: run with Maven Wrapper

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

### Option 3: package then run jar

```bash
./mvnw clean package
java -jar target/system-0.0.1-SNAPSHOT.jar
```

## Default local URLs

Once the app is running, try these URLs:

- App base: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI docs: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

The project includes `springdoc-openapi-starter-webmvc-ui`, enables the H2 console, and uses an in-memory H2 database URL.

## API summary

Base path:

```text
/nodes
```

Available endpoints from the current controller:

- `GET /nodes/getNode/{parentName}`
- `POST /nodes/addNode`
- `POST /nodes/{parentName}/addChildNode`
- `PUT /nodes/{parentName}/moveChildNode`
- `DELETE /nodes/deleteNode/{name}`

These mappings come from `NodeManagementController`.

## Request and response formats

The controller is set up to:
- **produce** JSON and XML responses
- **consume** JSON and XML on write endpoints

So you can call the same API using either:

- `Content-Type: application/json`
- `Content-Type: application/xml`
- `Accept: application/json`
- `Accept: application/xml`

This is supported by the controller configuration plus the Jackson XML dependency in `pom.xml`.

## How to use the web service

### 1) Create a root node

#### JSON

```bash
curl -X POST "http://localhost:8080/nodes/addNode" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "A"
  }'
```

Example response:

```json
{
  "parent": {
    "name": "A"
  }
}
```

#### XML

```bash
curl -X POST "http://localhost:8080/nodes/addNode" \
  -H "Content-Type: application/xml" \
  -H "Accept: application/xml" \
  -d '<NodeRequest><name>A</name></NodeRequest>'
```

### 2) Add a child node under a parent

#### JSON

```bash
curl -X POST "http://localhost:8080/nodes/A/addChildNode" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "B"
  }'
```

#### XML

```bash
curl -X POST "http://localhost:8080/nodes/A/addChildNode" \
  -H "Content-Type: application/xml" \
  -H "Accept: application/xml" \
  -d '<NodeRequest><name>B</name></NodeRequest>'
```

### 3) Get a node with its children

```bash
curl -X GET "http://localhost:8080/nodes/getNode/A" \
  -H "Accept: application/json"
```

Possible response:

```json
{
  "parent": {
    "name": "A",
    "children": [
      {
        "name": "B"
      }
    ]
  }
}
```

The service maps entity children into nested DTO children sets.

### 4) Move a child node to another parent

Example: move child `B` under destination parent `C`.

```bash
curl -X PUT "http://localhost:8080/nodes/C/moveChildNode" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "name": "B"
  }'
```

### 5) Delete a node

```bash
curl -X DELETE "http://localhost:8080/nodes/deleteNode/B"
```

## Example workflow

```bash
# create root nodes
curl -X POST "http://localhost:8080/nodes/addNode" -H "Content-Type: application/json" -d '{"name":"A"}'
curl -X POST "http://localhost:8080/nodes/addNode" -H "Content-Type: application/json" -d '{"name":"C"}'

# add child B under A
curl -X POST "http://localhost:8080/nodes/A/addChildNode" -H "Content-Type: application/json" -d '{"name":"B"}'

# read A
curl -X GET "http://localhost:8080/nodes/getNode/A" -H "Accept: application/json"

# move B from A to C
curl -X PUT "http://localhost:8080/nodes/C/moveChildNode" -H "Content-Type: application/json" -d '{"name":"B"}'

# read C
curl -X GET "http://localhost:8080/nodes/getNode/C" -H "Accept: application/json"
```

## Business rules in the current implementation

From the current DAO/service code:

- adding a root node with an existing name throws `IllegalArgumentException`
- adding a child requires the parent to exist
- child names are checked using `existsByParentIsNotNullAndName(...)` before insert 
- deleting a missing node throws `IllegalArgumentException`
- moving a node validates the child and destination parent, and updates parent-child links in the DAO layer

## How to test the app

### Run the current test suite

The repository already contains a basic Spring Boot context test in `SystemApplicationTests`.

Run tests with Maven:

```bash
./mvnw test
```

On Windows:

```bat
mvnw.cmd test
```

### Run Mockito / JUnit tests

If you add the Mockito-based unit tests, keep them under:

```text
src/test/java/com/nodemanagement/system/
```

You can then run:

```bash
./mvnw test
```

Or run each test class directly from IntelliJ.

## Using Swagger UI as a built-in “How to use my web service” page

Once the app is running, open:

```text
http://localhost:8080/swagger-ui/index.html
```

That gives you:
- a browser-based list of endpoints
- request/response inspection
- the ability to try endpoints directly from the browser

The `springdoc-openapi-starter-webmvc-ui` dependency is already in `pom.xml`.

## Notes / small issues to check

There are a couple of property entries in `application.properties` that look like typos:

- `spring.datasource.usezname=sa`
- `spring.jpa.defer-datasource-initialization=true`

Those appear in the current repo as written. You may want to change them to:

```properties
spring.datasource.username=sa
spring.jpa.defer-datasource-initialization=true
```

This observation is based on the exact `application.properties` content in the repository.
