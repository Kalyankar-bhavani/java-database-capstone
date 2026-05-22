// 1. **@Service Annotation**
// The @Service annotation marks this class as a service component in Spring. This allows Spring to automatically detect it through component scanning
// and manage its lifecycle, enabling it to be injected into controllers or other services using @Autowired or constructor injection.

// 2. **Constructor Injection for Dependencies**
// The constructor injects all required dependencies (TokenService, Repositories, and other Services). This approach promotes loose coupling, improves testability,
// and ensures that all required dependencies are provided at object creation time.

// 3. **validateToken Method**
// This method checks if the provided JWT token is valid for a specific user. It uses the TokenService to perform the validation.
// If the token is invalid or expired, it returns a 401 Unauthorized response with an appropriate error message. This ensures security by preventing
// unauthorized access to protected resources.

// 4. **validateAdmin Method**
// This method validates the login credentials for an admin user.
// - It first searches the admin repository using the provided username.
// - If an admin is found, it checks if the password matches.
// - If the password is correct, it generates and returns a JWT token (using the admin’s username) with a 200 OK status.
// - If the password is incorrect, it returns a 401 Unauthorized status with an error message.
// - If no admin is found, it also returns a 401 Unauthorized.
// - If any unexpected error occurs during the process, a 500 Internal Server Error response is returned.
// This method ensures that only valid admin users can access secured parts of the system.

// 5. **filterDoctor Method**
// This method provides filtering functionality for doctors based on name, specialty, and available time slots.
// - It supports various combinations of the three filters.
// - If none of the filters are provided, it returns all available doctors.
// This flexible filtering mechanism allows the frontend or consumers of the API to search and narrow down doctors based on user criteria.

// 6. **validateAppointment Method**
// This method validates if the requested appointment time for a doctor is available.
// - It first checks if the doctor exists in the repository.
// - Then, it retrieves the list of available time slots for the doctor on the specified date.
// - It compares the requested appointment time with the start times of these slots.
// - If a match is found, it returns 1 (valid appointment time).
// - If no matching time slot is found, it returns 0 (invalid).
// - If the doctor doesn’t exist, it returns -1.
// This logic prevents overlapping or invalid appointment bookings.

// 7. **validatePatient Method**
// This method checks whether a patient with the same email or phone number already exists in the system.
// - If a match is found, it returns false (indicating the patient is not valid for new registration).
// - If no match is found, it returns true.
// This helps enforce uniqueness constraints on patient records and prevent duplicate entries.

// 8. **validatePatientLogin Method**
// This method handles login validation for patient users.
// - It looks up the patient by email.
// - If found, it checks whether the provided password matches the stored one.
// - On successful validation, it generates a JWT token and returns it with a 200 OK status.
// - If the password is incorrect or the patient doesn't exist, it returns a 401 Unauthorized with a relevant error.
// - If an exception occurs, it returns a 500 Internal Server Error.
// This method ensures only legitimate patients can log in and access their data securely.

// 9. **filterPatient Method**
// This method filters a patient's appointment history based on condition and doctor name.
// - It extracts the email from the JWT token to identify the patient.
// - Depending on which filters (condition, doctor name) are provided, it delegates the filtering logic to PatientService.
// - If no filters are provided, it retrieves all appointments for the patient.
// This flexible method supports patient-specific querying and enhances user experience on the client side.


package com.project.back_end.services;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;

import com.project.back_end.repositories.AdminRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    /* ========================================
       VALIDATE TOKEN
    ======================================== */

    public boolean validateToken(String user, String token) {
        try {
            String username = tokenService.extractUsername(token);

            if (user.equals("admin")) {
                return adminRepository.findByUsername(username) != null;
            }

            if (user.equals("doctor")) {
                return doctorService.getDoctorByEmail(username) != null;
            }

            if (user.equals("patient")) {
                return patientService.getPatientByEmail(username) != null;
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }

    /* ========================================
       VALIDATE ADMIN LOGIN
    ======================================== */

    public ResponseEntity<?> validateAdmin(
            String username,
            String password
    ) {

        try {

            Admin admin =
                    adminRepository.findByUsername(
                            username
                    );

            if (admin == null) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Admin not found"
                                )
                        );
            }

            if (!admin.getPassword()
                    .equals(password)) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid password"
                                )
                        );
            }

            String token =
                    tokenService.generateToken(
                            admin.getUsername()
                    );

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Login successful"
            );

            response.put(
                    "token",
                    token
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Server Error"
                            )
                    );
        }
    }

    /* ========================================
       FILTER DOCTORS
    ======================================== */

    public List<Doctor> filterDoctors(
            String name,
            String time,
            String speciality
    ) {

        return doctorService.filterDoctors(
                name,
                time,
                speciality
        );
    }

    /* ========================================
       VALIDATE APPOINTMENT
    ======================================== */

    public int validateAppointment(
            Long doctorId,
            LocalDate date,
            LocalDateTime appointmentTime
    ) {

        Doctor doctor =
                doctorRepository.findById(
                        doctorId
                ).orElse(null);

        if (doctor == null) {

            return -1;
        }

        List<String> availableTimes =
                doctor.getAvailableTimes();

        String requestedTime =
                appointmentTime.toLocalTime()
                        .toString();

        for (String time : availableTimes) {

            String startTime = time.contains("-") ? time.split("-")[0] : time;

            if (startTime.equals(requestedTime)) {
                return 1;
            }
        }

        return 0;
    }

    /* ========================================
       VALIDATE PATIENT
    ======================================== */

    public boolean validatePatient(
            Patient patient
    ) {

        Patient existingPatient =
                patientRepository.findByEmailOrPhone(
                        patient.getEmail(),
                        patient.getPhone()
                );

        return existingPatient == null;
    }

    /* ========================================
       VALIDATE PATIENT LOGIN
    ======================================== */

    public ResponseEntity<?> validatePatientLogin(
            String email,
            String password
    ) {

        try {

            Patient patient =
                    patientRepository.findByEmail(
                            email
                    );

            if (patient == null) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Patient not found"
                                )
                        );
            }

            if (!patient.getPassword()
                    .equals(password)) {

                return ResponseEntity
                        .status(
                                HttpStatus.UNAUTHORIZED
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid password"
                                )
                        );
            }

            String token =
                    tokenService.generateToken(
                            patient.getEmail()
                    );

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Login successful"
            );

            response.put(
                    "token",
                    token
            );

            return ResponseEntity.ok(
                    response
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Server Error"
                            )
                    );
        }
    }

    /* ========================================
       FILTER PATIENT
    ======================================== */

    public Object filterPatient(
            String token,
            String condition,
            String doctorName
    ) {

        return patientService.filterPatient(
                token,
                condition,
                doctorName
        );
    }
}