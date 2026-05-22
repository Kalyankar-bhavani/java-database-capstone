# Smart Clinic Management System – Architecture Design

## Section 1: Architecture Summary

The Smart Clinic Management System is developed using Spring Boot and follows a layered three-tier architecture consisting of the Presentation Layer, Application Layer, and Data Layer. The application supports both MVC-based web pages and RESTful APIs. Thymeleaf templates are used to render dynamic dashboards for administrators and doctors, while REST APIs are used for appointment management, patient records, and other client-server interactions.

The Presentation Layer contains MVC controllers and REST controllers that receive requests from users or API clients. These controllers delegate business operations to the Service Layer, where business logic, validation, and workflow management are implemented. The Service Layer communicates with the Repository Layer to perform database operations.

The application integrates two databases. MySQL is used for storing structured relational data such as patients, doctors, appointments, and admin information using Spring Data JPA and Hibernate. MongoDB is used for storing flexible document-based prescription data using Spring Data MongoDB. JPA entities are used for MySQL tables, while MongoDB document models are used for prescription collections.

The project also follows the Repository Pattern and uses DTOs to transfer data between layers. Static resources such as CSS and images are managed inside the resources/static directory. This architecture improves scalability, maintainability, modularity, and separation of concerns.

---

## Section 2: Numbered Flow of Data and Control

1. Users access the Smart Clinic Management System through Thymeleaf dashboard pages or REST API clients.

2. Requests from the frontend are routed to either MVC controllers or REST controllers based on the URL and request type.

3. MVC controllers return Thymeleaf HTML templates for dashboards, while REST controllers return JSON responses for API requests.

4. Controllers pass the request to the appropriate service classes, where business logic and validations are handled.

5. Service classes communicate with repository classes to perform CRUD operations and database interactions.

6. Spring Data JPA repositories interact with the MySQL database to manage structured relational data such as patients, doctors, appointments, and admin details.

7. Spring Data MongoDB repositories interact with MongoDB to store and retrieve prescription documents.

8. Data retrieved from the databases is mapped into JPA entities or MongoDB document models.

9. The processed data is returned from the repository layer to the service layer.

10. The service layer sends the final processed response back to the controllers.

11. MVC controllers send model data to Thymeleaf templates for HTML rendering in the browser.

12. REST controllers serialize the response data into JSON format and send it back to API clients.

13. Static assets such as CSS files and images are loaded from the resources/static directory to enhance the frontend user interface.

14. The complete request-response cycle ends after the user receives either a rendered HTML page or a JSON API response.
