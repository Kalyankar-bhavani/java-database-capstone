// 1. Set Up the Controller Class:
//    - Annotate the class with `@RestController` to define it as a REST controller that serves JSON responses.
//    - Use `@RequestMapping("${api.path}doctor")` to prefix all endpoints with a configurable API path followed by "doctor".
//    - This class manages doctor-related functionalities such as registration, login, updates, and availability.


// 2. Autowire Dependencies:
//    - Inject `DoctorService` for handling the core logic related to doctors (e.g., CRUD operations, authentication).
//    - Inject the shared `Service` class for general-purpose features like token validation and filtering.


// 3. Define the `getDoctorAvailability` Method:
//    - Handles HTTP GET requests to check a specific doctor’s availability on a given date.
//    - Requires `user` type, `doctorId`, `date`, and `token` as path variables.
//    - First validates the token against the user type.
//    - If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.


// 4. Define the `getDoctor` Method:
//    - Handles HTTP GET requests to retrieve a list of all doctors.
//    - Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.


// 5. Define the `saveDoctor` Method:
//    - Handles HTTP POST requests to register a new doctor.
//    - Accepts a validated `Doctor` object in the request body and a token for authorization.
//    - Validates the token for the `"admin"` role before proceeding.
//    - If the doctor already exists, returns a conflict response; otherwise, adds the doctor and returns a success message.


// 6. Define the `doctorLogin` Method:
//    - Handles HTTP POST requests for doctor login.
//    - Accepts a validated `Login` DTO containing credentials.
//    - Delegates authentication to the `DoctorService` and returns login status and token information.


// 7. Define the `updateDoctor` Method:
//    - Handles HTTP PUT requests to update an existing doctor's information.
//    - Accepts a validated `Doctor` object and a token for authorization.
//    - Token must belong to an `"admin"`.
//    - If the doctor exists, updates the record and returns success; otherwise, returns not found or error messages.


// 8. Define the `deleteDoctor` Method:
//    - Handles HTTP DELETE requests to remove a doctor by ID.
//    - Requires both doctor ID and an admin token as path variables.
//    - If the doctor exists, deletes the record and returns a success message; otherwise, responds with a not found or error message.


// 9. Define the `filter` Method:
//    - Handles HTTP GET requests to filter doctors based on name, time, and specialty.
//    - Accepts `name`, `time`, and `speciality` as path variables.
//    - Calls the shared `Service` to perform filtering logic and returns matching doctors in the response.

package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private Service service;


    /* ========================================
       GET DOCTOR AVAILABILITY
    ======================================== */

    @GetMapping(
            "/availability/{user}/{doctorId}/{date}/{token}"
    )
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token
    ) {

        boolean valid =
                service.validateToken(
                        user,
                        token
                );

        if (!valid) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid token"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "availableTimes",
                        doctorService.getDoctorAvailability(
                                doctorId,
                                LocalDate.parse(date)
                        )
                )
        );
    }


    /* ========================================
       GET ALL DOCTORS
    ======================================== */

    @GetMapping
    public ResponseEntity<?> getDoctor() {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "doctors",
                doctorService.getDoctors()
        );

        return ResponseEntity.ok(
                response
        );
    }


    /* ========================================
       SAVE DOCTOR
    ======================================== */

    @PostMapping("/{token}")
    public ResponseEntity<?> saveDoctor(
            @Valid
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {

        boolean valid =
                service.validateToken(
                        "admin",
                        token
                );

        if (!valid) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid token"
                            )
                    );
        }

        int result =
                doctorService.saveDoctor(
                        doctor
                );

        if (result == -1) {

            return ResponseEntity
                    .status(409)
                    .body(
                            Map.of(
                                    "message",
                                    "Doctor already exists"
                            )
                    );
        }

        if (result == 0) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to save doctor"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Doctor saved successfully"
                )
        );
    }


    /* ========================================
       DOCTOR LOGIN
    ======================================== */

    @PostMapping("/login")
    public ResponseEntity<?> doctorLogin(
            @Valid
            @RequestBody Login login
    ) {

        return doctorService.validateDoctor(
                login.getEmail(),
                login.getPassword()
        );
    }


    /* ========================================
       UPDATE DOCTOR
    ======================================== */

    @PutMapping("/{token}")
    public ResponseEntity<?> updateDoctor(
            @Valid
            @RequestBody Doctor doctor,
            @PathVariable String token
    ) {

        boolean valid =
                service.validateToken(
                        "admin",
                        token
                );

        if (!valid) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid token"
                            )
                    );
        }

        int result =
                doctorService.updateDoctor(
                        doctor
                );

        if (result == -1) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Doctor not found"
                            )
                    );
        }

        if (result == 0) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to update doctor"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Doctor updated successfully"
                )
        );
    }


    /* ========================================
       DELETE DOCTOR
    ======================================== */

    @DeleteMapping("/{doctorId}/{token}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable Long doctorId,
            @PathVariable String token
    ) {

        boolean valid =
                service.validateToken(
                        "admin",
                        token
                );

        if (!valid) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid token"
                            )
                    );
        }

        int result =
                doctorService.deleteDoctor(
                        doctorId
                );

        if (result == -1) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Doctor not found"
                            )
                    );
        }

        if (result == 0) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to delete doctor"
                            )
                    );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Doctor deleted successfully"
                )
        );
    }


    /* ========================================
       FILTER DOCTORS
    ======================================== */

    @GetMapping(
            "/filter/{name}/{time}/{speciality}"
    )
    public ResponseEntity<?> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "doctors",
                service.filterDoctors(
                        name,
                        time,
                        speciality
                )
        );

        return ResponseEntity.ok(
                response
        );
    }
}