# Pull Request: Enable Swagger UI Operations

This PR fixes the Swagger UI issue where no operations were displayed.

## Changes Made
- Updated `src/main/resources/application.properties` to remove the configuration that disabled automatic scanning of controllers (`springdoc.packages-to-scan=none` and `springdoc.paths-to-exclude=/**`).
- This allows Springdoc OpenAPI to detect all REST endpoints and correctly populate the Swagger UI.

## Verification
- Run the application and navigate to `http://localhost:8080/swagger-ui.html`.
- All controller operations are now visible in the documentation.

## Impact
- No functional changes to the API logic.
- Improves developer experience by providing accurate API documentation.
