
// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.

// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.



package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.DTO.AppointmentDTO;
import java.util.ArrayList;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;
import com.project.back_end.repositories.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private com.project.back_end.services.Service service;

    /* ========================================
       BOOK APPOINTMENT
    ======================================== */

    @Transactional
    public int bookAppointment(
            Appointment appointment
    ) {

        try {

            appointmentRepository.save(
                    appointment
            );

            return 1;

        } catch (Exception e) {

            return 0;
        }
    }

    /* ========================================
       UPDATE APPOINTMENT
    ======================================== */

    @Transactional
    public String updateAppointment(
            Long appointmentId,
            Appointment updatedAppointment,
            Long patientId
    ) {

        try {

            Optional<Appointment> optionalAppointment =
                    appointmentRepository.findById(
                            appointmentId
                    );

            if (optionalAppointment.isEmpty()) {

                return "Appointment not found";
            }

            Appointment existingAppointment =
                    optionalAppointment.get();

            if (!existingAppointment
                    .getPatient()
                    .getId()
                    .equals(patientId)) {

                return "Unauthorized patient";
            }

            Doctor doctor =
                    doctorRepository.findById(
                            updatedAppointment
                                    .getDoctor()
                                    .getId()
                    ).orElse(null);

            if (doctor == null) {

                return "Doctor not found";
            }

            int validation =
                    service.validateAppointment(
                            doctor.getId(),
                            updatedAppointment
                                    .getAppointmentTime()
                                    .toLocalDate(),
                            updatedAppointment
                                    .getAppointmentTime()
                    );

            if (validation != 1) {

                return "Doctor not available";
            }

            existingAppointment.setDoctor(
                    updatedAppointment.getDoctor()
            );

            existingAppointment.setAppointmentTime(
                    updatedAppointment.getAppointmentTime()
            );

            existingAppointment.setStatus(
                    updatedAppointment.getStatus()
            );

            appointmentRepository.save(
                    existingAppointment
            );

            return "Appointment updated successfully";

        } catch (Exception e) {

            return "Failed to update appointment";
        }
    }

    /* ========================================
       CANCEL APPOINTMENT
    ======================================== */

    @Transactional
    public String cancelAppointment(
            Long appointmentId,
            Long patientId
    ) {

        try {

            Optional<Appointment> optionalAppointment =
                    appointmentRepository.findById(
                            appointmentId
                    );

            if (optionalAppointment.isEmpty()) {

                return "Appointment not found";
            }

            Appointment appointment =
                    optionalAppointment.get();

            if (!appointment
                    .getPatient()
                    .getId()
                    .equals(patientId)) {

                return "Unauthorized patient";
            }

            appointmentRepository.delete(
                    appointment
            );

            return "Appointment cancelled successfully";

        } catch (Exception e) {

            return "Failed to cancel appointment";
        }
    }

    /* ========================================
       GET APPOINTMENTS
    ======================================== */

    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(
            Long doctorId,
            LocalDate date,
            String patientName
    ) {

        LocalDateTime start =
                date.atStartOfDay();

        LocalDateTime end =
                date.atTime(23, 59, 59);

        if (patientName != null
                && !patientName.isBlank()
                && !patientName.equalsIgnoreCase("all")) {

            return appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId,
                            patientName,
                            start,
                            end
                    );
        }

        return appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        start,
                        end
                );
    }
        /* ========================================
       GET APPOINTMENT DTOS FOR DOCTOR
    ======================================== */

    @Transactional(readOnly = true)
    public List<AppointmentDTO> getAppointmentDTOsForDoctor(
            Long doctorId,
            LocalDate date,
            String patientName
    ) {

        List<Appointment> appointments =
                getAppointments(
                        doctorId,
                        date,
                        patientName
                );

        List<AppointmentDTO> dtoList =
                new ArrayList<>();

        for (Appointment appointment : appointments) {

            AppointmentDTO dto =
                    new AppointmentDTO();

            dto.setId(
                    appointment.getId()
            );

            dto.setDoctorId(
                    appointment.getDoctor().getId()
            );

            dto.setDoctorName(
                    appointment.getDoctor().getName()
            );

            dto.setPatientId(
                    appointment.getPatient().getId()
            );

            dto.setPatientName(
                    appointment.getPatient().getName()
            );

            dto.setPatientEmail(
                    appointment.getPatient().getEmail()
            );

            dto.setPatientPhone(
                    appointment.getPatient().getPhone()
            );

            dto.setAppointmentTime(
                    appointment.getAppointmentTime()
            );

            dto.setStatus(
                    appointment.getStatus()
            );

            dtoList.add(dto);
        }

        return dtoList;
    }

    /* ========================================
       CHANGE STATUS
    ======================================== */

    @Transactional
    public String changeStatus(
            Long appointmentId,
            int status
    ) {

        try {

            Optional<Appointment> optionalAppointment =
                    appointmentRepository.findById(
                            appointmentId
                    );

            if (optionalAppointment.isEmpty()) {

                return "Appointment not found";
            }

            Appointment appointment =
                    optionalAppointment.get();

            appointment.setStatus(status);

            appointmentRepository.save(
                    appointment
            );

            return "Appointment status updated";

        } catch (Exception e) {

            return "Failed to update status";
        }
    }
}