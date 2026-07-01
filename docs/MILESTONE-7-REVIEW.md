# Milestone 7 Review: Project CRUD APIs & Services

## Status
Completed

---

## Capabilities Delivered
*   **✔ Data Transfer Objects (DTOs)**: Created input payload [ProjectRequest.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/dto/ProjectRequest.java) and output view DTO model [ProjectResponse.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/dto/ProjectResponse.java) mapping validation constraints (`@NotBlank`, `@Size`, `@NotNull`).
*   **✔ Component-based Project Mapper**: Implemented [ProjectMapper.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/mapper/ProjectMapper.java) registered as a Spring `@Component` bean.
*   **✔ User Context Abstraction**: Created [CurrentUserProvider.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/security/CurrentUserProvider.java) interface and [HeaderCurrentUserProvider.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/security/HeaderCurrentUserProvider.java) implementation to cleanly read the UUID from the `X-User-Id` request header. This returns `400 Bad Request` if missing or malformed, and decouples the controller layer from future Spring Security token principal resolvers.
*   **✔ Service Layer**: Implemented business operations in [ProjectService.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/service/ProjectService.java) coordinating database lookups, updates, cascading deletions, and authorization ownership checks.
*   **✔ REST Controller CRUD routes**: Implemented [ProjectController.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/controller/ProjectController.java) mapping routes under `/api/projects`:
    *   `POST /api/projects`: Returns `201 Created` with the mapped entity payload.
    *   `GET /api/projects`: Returns a paginated search list using Spring Data's `Pageable` (`?page=0&size=20`).
    *   `GET /api/projects/{id}`: Returns project details by ID.
    *   `PUT /api/projects/{id}`: Performs standard resource field updates.
    *   `DELETE /api/projects/{id}`: Returns `204 No Content` on successful execution.
*   **✔ OpenAPI Swagger Metadata**: Annotated endpoints with OpenAPI details mapping return responses (`200`, `201`, `204`, `400`, `403`, `404`, `500`).
*   **✔ Exceptions Advisory**: Created separate [ResourceNotFoundException.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/exception/ResourceNotFoundException.java) and [AccessDeniedException.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/exception/AccessDeniedException.java) classes. Structured error messages under [ErrorResponse.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/dto/ErrorResponse.java) containing a string-based `errorCode`. Registered [GlobalExceptionHandler.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/exception/GlobalExceptionHandler.java) to catch and format API exceptions.
*   **✔ Decoupled JPA configurations**: Separated `@EnableJpaAuditing` to [JpaConfig.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/main/java/com/vizorix/api/config/JpaConfig.java), allowing lightweight web slice tests (`@WebMvcTest`) to boot without triggering JpaAuditing context dependency crashes.
*   **✔ MockMvc Controller validation tests**: Created [ProjectControllerTests.java](file:///c:/Users/dell/OneDrive/Desktop/CODING/vizorix/apps/api/src/test/java/com/vizorix/api/controller/ProjectControllerTests.java) validating creations, pagination listings, exceptions, error codes, malformed payloads, and header extraction rules.

---

## Deferrals (Not Yet Implemented)
*   **□ Session authentication filters**: Scheduled for Milestone 8.
*   **□ Monaco Editor client configurations**: Scheduled for Milestone 9.
*   **□ Java trace executions parsing**: Scheduled for Milestone 10.
