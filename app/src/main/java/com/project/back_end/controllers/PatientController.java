// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST API controller for patient-related operations.
//    - Use `@RequestMapping("/patient")` to prefix all endpoints with `/patient`, grouping all patient functionalities under a common route.


// 2. Autowire Dependencies:
//    - Inject `PatientService` to handle patient-specific logic such as creation, retrieval, and appointments.
//    - Inject the shared `Service` class for tasks like token validation and login authentication.


// 3. Define the `getPatient` Method:
//    - Handles HTTP GET requests to retrieve patient details using a token.
//    - Validates the token for the `"patient"` role using the shared service.
//    - If the token is valid, returns patient information; otherwise, returns an appropriate error message.


// 4. Define the `createPatient` Method:
//    - Handles HTTP POST requests for patient registration.
//    - Accepts a validated `Patient` object in the request body.
//    - First checks if the patient already exists using the shared service.
//    - If validation passes, attempts to create the patient and returns success or error messages based on the outcome.


// 5. Define the `login` Method:
//    - Handles HTTP POST requests for patient login.
//    - Accepts a `Login` DTO containing email/username and password.
//    - Delegates authentication to the `validatePatientLogin` method in the shared service.
//    - Returns a response with a token or an error message depending on login success.


// 6. Define the `getPatientAppointment` Method:
//    - Handles HTTP GET requests to fetch appointment details for a specific patient.
//    - Requires the patient ID, token, and user role as path variables.
//    - Validates the token using the shared service.
//    - If valid, retrieves the patient's appointment data from `PatientService`; otherwise, returns a validation error.


// 7. Define the `filterPatientAppointment` Method:
//    - Handles HTTP GET requests to filter a patient's appointments based on specific conditions.
//    - Accepts filtering parameters: `condition`, `name`, and a token.
//    - Token must be valid for a `"patient"` role.
//    - If valid, delegates filtering logic to the shared service and returns the filtered result.


package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(
            PatientService patientService,
            Service service
    ) {
        this.patientService = patientService;
        this.service = service;
    }

    /* ========================================
       GET PATIENT DETAILS
    ======================================== */

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(
            @PathVariable String token
    ) {

        boolean valid = service.validateToken(
                "patient",
                token
        );

        if (!valid) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid or expired token"
                            )
                    );
        }

        return patientService.getPatientDetails(
                token
        );
    }

    /* ========================================
       CREATE PATIENT
    ======================================== */

    @PostMapping
    public ResponseEntity<?> createPatient(
            @Valid @RequestBody Patient patient
    ) {

        boolean validPatient = service.validatePatient(
                patient
        );

        if (!validPatient) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Patient already exists"
                            )
                    );
        }

        int result = patientService.createPatient(
                patient
        );

        if (result == 0) {
            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to create patient"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Patient registered successfully"
                )
        );
    }

    /* ========================================
       PATIENT LOGIN
    ======================================== */

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody Login login
    ) {

        return service.validatePatientLogin(
                login.getEmail(),
                login.getPassword()
        );
    }

    /* ========================================
       GET PATIENT APPOINTMENTS
    ======================================== */

    @GetMapping("/appointment/{patientId}/{user}/{token}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long patientId,
            @PathVariable String user,
            @PathVariable String token
    ) {

        boolean valid = service.validateToken(
                user,
                token
        );

        if (!valid) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid or expired token"
                            )
                    );
        }

        return patientService.getPatientAppointment(
                patientId
        );
    }

    /* ========================================
       FILTER PATIENT APPOINTMENTS
    ======================================== */

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {

        boolean valid = service.validateToken(
                "patient",
                token
        );

        if (!valid) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid or expired token"
                            )
                    );
        }

        return ResponseEntity.ok(
                service.filterPatient(
                        token,
                        condition,
                        name
                )
        );
    }
}