
// 1. **Add @Service Annotation**:
//    - The `@Service` annotation is used to mark this class as a Spring service component. 
//    - It will be managed by Spring's container and used for business logic related to patients and appointments.
//    - Instruction: Ensure that the `@Service` annotation is applied above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `PatientService` class has dependencies on `PatientRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies are injected via the constructor to maintain good practices of dependency injection and testing.
//    - Instruction: Ensure constructor injection is used for all the required dependencies.

// 3. **createPatient Method**:
//    - Creates a new patient in the database. It saves the patient object using the `PatientRepository`.
//    - If the patient is successfully saved, the method returns `1`; otherwise, it logs the error and returns `0`.
//    - Instruction: Ensure that error handling is done properly and exceptions are caught and logged appropriately.

// 4. **getPatientAppointment Method**:
//    - Retrieves a list of appointments for a specific patient, based on their ID.
//    - The appointments are then converted into `AppointmentDTO` objects for easier consumption by the API client.
//    - This method is marked as `@Transactional` to ensure database consistency during the transaction.
//    - Instruction: Ensure that appointment data is properly converted into DTOs and the method handles errors gracefully.

// 5. **filterByCondition Method**:
//    - Filters appointments for a patient based on the condition (e.g., "past" or "future").
//    - Retrieves appointments with a specific status (0 for future, 1 for past) for the patient.
//    - Converts the appointments into `AppointmentDTO` and returns them in the response.
//    - Instruction: Ensure the method correctly handles "past" and "future" conditions, and that invalid conditions are caught and returned as errors.

// 6. **filterByDoctor Method**:
//    - Filters appointments for a patient based on the doctor's name.
//    - It retrieves appointments where the doctor’s name matches the given value, and the patient ID matches the provided ID.
//    - Instruction: Ensure that the method correctly filters by doctor's name and patient ID and handles any errors or invalid cases.

// 7. **filterByDoctorAndCondition Method**:
//    - Filters appointments based on both the doctor's name and the condition (past or future) for a specific patient.
//    - This method combines filtering by doctor name and appointment status (past or future).
//    - Converts the appointments into `AppointmentDTO` objects and returns them in the response.
//    - Instruction: Ensure that the filter handles both doctor name and condition properly, and catches errors for invalid input.

// 8. **getPatientDetails Method**:
//    - Retrieves patient details using the `tokenService` to extract the patient's email from the provided token.
//    - Once the email is extracted, it fetches the corresponding patient from the `patientRepository`.
//    - It returns the patient's information in the response body.
    //    - Instruction: Make sure that the token extraction process works correctly and patient details are fetched properly based on the extracted email.

// 9. **Handling Exceptions and Errors**:
//    - The service methods handle exceptions using try-catch blocks and log any issues that occur. If an error occurs during database operations, the service responds with appropriate HTTP status codes (e.g., `500 Internal Server Error`).
//    - Instruction: Ensure that error handling is consistent across the service, with proper logging and meaningful error messages returned to the client.

// 10. **Use of DTOs (Data Transfer Objects)**:
//    - The service uses `AppointmentDTO` to transfer appointment-related data between layers. This ensures that sensitive or unnecessary data (e.g., password or private patient information) is not exposed in the response.
//    - Instruction: Ensure that DTOs are used appropriately to limit the exposure of internal data and only send the relevant fields to the client.



package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;

import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    /* ========================================
       CREATE PATIENT
    ======================================== */

    public int createPatient(
            Patient patient
    ) {

        try {

            patientRepository.save(patient);

            return 1;

        } catch (Exception e) {

            System.out.println(
                    "Create Patient Error: "
                            + e.getMessage()
            );

            return 0;
        }
    }
    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    /* ========================================
       GET PATIENT APPOINTMENTS
    ======================================== */

    @Transactional(readOnly = true)
    public ResponseEntity<?> getPatientAppointment(
            Long patientId
    ) {

        try {

            List<Appointment> appointments =
                    appointmentRepository
                            .findByPatientId(
                                    patientId
                            );

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTOs(
                            appointments
                    );

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "appointments",
                    appointmentDTOs
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
                                    "Failed to fetch appointments"
                            )
                    );
        }
    }

    /* ========================================
       FILTER BY CONDITION
    ======================================== */

    @Transactional(readOnly = true)
    public ResponseEntity<?> filterByCondition(
            Long patientId,
            String condition
    ) {

        try {

            int status;

            if (condition.equalsIgnoreCase(
                    "future"
            )) {

                status = 0;

            } else if (condition.equalsIgnoreCase(
                    "past"
            )) {

                status = 1;

            } else {

                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid condition"
                                )
                        );
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                    patientId,
                                    status
                            );

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTOs(
                            appointments
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "appointments",
                            appointmentDTOs
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to filter appointments"
                            )
                    );
        }
    }

    /* ========================================
       FILTER BY DOCTOR
    ======================================== */

    @Transactional(readOnly = true)
    public ResponseEntity<?> filterByDoctor(
            Long patientId,
            String doctorName
    ) {

        try {

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientId(
                                    doctorName,
                                    patientId
                            );

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTOs(
                            appointments
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "appointments",
                            appointmentDTOs
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to filter by doctor"
                            )
                    );
        }
    }

    /* ========================================
       FILTER BY DOCTOR AND CONDITION
    ======================================== */

    @Transactional(readOnly = true)
    public ResponseEntity<?> filterByDoctorAndCondition(
            Long patientId,
            String doctorName,
            String condition
    ) {

        try {

            int status;

            if (condition.equalsIgnoreCase(
                    "future"
            )) {

                status = 0;

            } else if (condition.equalsIgnoreCase(
                    "past"
            )) {

                status = 1;

            } else {

                return ResponseEntity.badRequest()
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid condition"
                                )
                        );
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientIdAndStatus(
                                    doctorName,
                                    patientId,
                                    status
                            );

            List<AppointmentDTO> appointmentDTOs =
                    convertToDTOs(
                            appointments
                    );

            return ResponseEntity.ok(
                    Map.of(
                            "appointments",
                            appointmentDTOs
                    )
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to filter appointments"
                            )
                    );
        }
    }

    /* ========================================
       GET PATIENT DETAILS
    ======================================== */

    public ResponseEntity<?> getPatientDetails(
            String token
    ) {

        try {

            String email =
                    tokenService.extractUsername(
                            token
                    );

            Patient patient =
                    patientRepository.findByEmail(
                            email
                    );

            if (patient == null) {

                return ResponseEntity
                        .status(
                                HttpStatus.NOT_FOUND
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Patient not found"
                                )
                        );
            }

            return ResponseEntity.ok(
                    patient
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to fetch patient details"
                            )
                    );
        }
    }

    /* ========================================
       FILTER PATIENT
    ======================================== */

    public ResponseEntity<?> filterPatient(
            String token,
            String condition,
            String doctorName
    ) {

        try {

            String email =
                    tokenService.extractUsername(
                            token
                    );

            Patient patient =
                    patientRepository.findByEmail(
                            email
                    );

            if (patient == null) {

                return ResponseEntity
                        .status(
                                HttpStatus.NOT_FOUND
                        )
                        .body(
                                Map.of(
                                        "message",
                                        "Patient not found"
                                )
                        );
            }

            Long patientId =
                    patient.getId();

            if ((condition == null
                    || condition.isBlank())
                    &&
                    (doctorName == null
                            || doctorName.isBlank())) {

                return getPatientAppointment(
                        patientId
                );
            }

            if (doctorName == null
                    || doctorName.isBlank()) {

                return filterByCondition(
                        patientId,
                        condition
                );
            }

            if (condition == null
                    || condition.isBlank()) {

                return filterByDoctor(
                        patientId,
                        doctorName
                );
            }

            return filterByDoctorAndCondition(
                    patientId,
                    doctorName,
                    condition
            );

        } catch (Exception e) {

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .body(
                            Map.of(
                                    "message",
                                    "Failed to filter patient data"
                            )
                    );
        }
    }

    /* ========================================
       CONVERT APPOINTMENTS TO DTO
    ======================================== */

    private List<AppointmentDTO> convertToDTOs(
            List<Appointment> appointments
    ) {

        List<AppointmentDTO> appointmentDTOs =
                new ArrayList<>();

        for (Appointment appointment : appointments) {

            AppointmentDTO dto =
                    new AppointmentDTO();

            dto.setId(
                    appointment.getId()
            );

            dto.setDoctorName(
                    appointment.getDoctor()
                            .getName()
            );

            dto.setPatientName(
                    appointment.getPatient()
                            .getName()
            );

            dto.setAppointmentTime(
                    appointment.getAppointmentTime()
            );

            dto.setStatus(
                    appointment.getStatus()
            );

            appointmentDTOs.add(dto);
        }

        return appointmentDTOs;
    }
}