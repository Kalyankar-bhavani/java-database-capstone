// 1. **Add @Service Annotation**:
//    - This class should be annotated with `@Service` to indicate that it is a service layer class.
//    - The `@Service` annotation marks this class as a Spring-managed bean for business logic.
//    - Instruction: Add `@Service` above the class declaration.

// 2. **Constructor Injection for Dependencies**:
//    - The `DoctorService` class depends on `DoctorRepository`, `AppointmentRepository`, and `TokenService`.
//    - These dependencies should be injected via the constructor for proper dependency management.
//    - Instruction: Ensure constructor injection is used for injecting dependencies into the service.

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.

// 4. **getDoctorAvailability Method**:
//    - Retrieves the available time slots for a specific doctor on a particular date and filters out already booked slots.
//    - The method fetches all appointments for the doctor on the given date and calculates the availability by comparing against booked slots.
//    - Instruction: Ensure that the time slots are properly formatted and the available slots are correctly filtered.

// 5. **saveDoctor Method**:
//    - Used to save a new doctor record in the database after checking if a doctor with the same email already exists.
//    - If a doctor with the same email is found, it returns `-1` to indicate conflict; `1` for success, and `0` for internal errors.
//    - Instruction: Ensure that the method correctly handles conflicts and exceptions when saving a doctor.

// 6. **updateDoctor Method**:
//    - Updates an existing doctor's details in the database. If the doctor doesn't exist, it returns `-1`.
//    - Instruction: Make sure that the doctor exists before attempting to save the updated record and handle any errors properly.

// 7. **getDoctors Method**:
//    - Fetches all doctors from the database. It is marked with `@Transactional` to ensure that the collection is properly loaded.
//    - Instruction: Ensure that the collection is eagerly loaded, especially if dealing with lazy-loaded relationships (e.g., available times). 

// 8. **deleteDoctor Method**:
//    - Deletes a doctor from the system along with all appointments associated with that doctor.
//    - It first checks if the doctor exists. If not, it returns `-1`; otherwise, it deletes the doctor and their appointments.
//    - Instruction: Ensure the doctor and their appointments are deleted properly, with error handling for internal issues.

// 9. **validateDoctor Method**:
//    - Validates a doctor's login by checking if the email and password match an existing doctor record.
//    - It generates a token for the doctor if the login is successful, otherwise returns an error message.
//    - Instruction: Make sure to handle invalid login attempts and password mismatches properly with error responses.

// 10. **findDoctorByName Method**:
//    - Finds doctors based on partial name matching and returns the list of doctors with their available times.
//    - This method is annotated with `@Transactional` to ensure that the database query and data retrieval are properly managed within a transaction.
//    - Instruction: Ensure that available times are eagerly loaded for the doctors.


// 11. **filterDoctorsByNameSpecilityandTime Method**:
//    - Filters doctors based on their name, specialty, and availability during a specific time (AM/PM).
//    - The method fetches doctors matching the name and specialty criteria, then filters them based on their availability during the specified time period.
//    - Instruction: Ensure proper filtering based on both the name and specialty as well as the specified time period.

// 12. **filterDoctorByTime Method**:
//    - Filters a list of doctors based on whether their available times match the specified time period (AM/PM).
//    - This method processes a list of doctors and their available times to return those that fit the time criteria.
//    - Instruction: Ensure that the time filtering logic correctly handles both AM and PM time slots and edge cases.


// 13. **filterDoctorByNameAndTime Method**:
//    - Filters doctors based on their name and the specified time period (AM/PM).
//    - Fetches doctors based on partial name matching and filters the results to include only those available during the specified time period.
//    - Instruction: Ensure that the method correctly filters doctors based on the given name and time of day (AM/PM).

// 14. **filterDoctorByNameAndSpecility Method**:
//    - Filters doctors by name and specialty.
//    - It ensures that the resulting list of doctors matches both the name (case-insensitive) and the specified specialty.
//    - Instruction: Ensure that both name and specialty are considered when filtering doctors.


// 15. **filterDoctorByTimeAndSpecility Method**:
//    - Filters doctors based on their specialty and availability during a specific time period (AM/PM).
//    - Fetches doctors based on the specified specialty and filters them based on their available time slots for AM/PM.
//    - Instruction: Ensure the time filtering is accurately applied based on the given specialty and time period (AM/PM).

// 16. **filterDoctorBySpecility Method**:
//    - Filters doctors based on their specialty.
//    - This method fetches all doctors matching the specified specialty and returns them.
//    - Instruction: Make sure the filtering logic works for case-insensitive specialty matching.

// 17. **filterDoctorsByTime Method**:
//    - Filters all doctors based on their availability during a specific time period (AM/PM).
//    - The method checks all doctors' available times and returns those available during the specified time period.
//    - Instruction: Ensure proper filtering logic to handle AM/PM time periods.


package com.project.back_end.services;

import com.project.back_end.models.Doctor;
import com.project.back_end.repositories.AppointmentRepository;
import com.project.back_end.repositories.DoctorRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    @Transactional
    public List<String> getDoctorAvailability(
            Long doctorId,
            LocalDate date
    ) {

        Doctor doctor =
                doctorRepository.findById(doctorId)
                        .orElse(null);

        if (doctor == null) {
            return new ArrayList<>();
        }

        List<String> availableTimes =
                new ArrayList<>(
                        doctor.getAvailableTimes()
                );

        LocalDateTime start =
                date.atStartOfDay();

        LocalDateTime end =
                date.atTime(23, 59);

        appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(
                        doctorId,
                        start,
                        end
                )
                .forEach(appointment -> {

                    String bookedTime =
                            appointment
                                    .getAppointmentTime()
                                    .toLocalTime()
                                    .toString();

                    availableTimes.remove(
                            bookedTime
                    );
                });

        return availableTimes;
    }

    public int saveDoctor(
            Doctor doctor
    ) {

        try {

            Doctor existingDoctor =
                    doctorRepository.findByEmail(
                            doctor.getEmail()
                    );

            if (existingDoctor != null) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(
            Doctor doctor
    ) {

        try {

            Doctor existingDoctor =
                    doctorRepository.findById(
                                    doctor.getId()
                            )
                            .orElse(null);

            if (existingDoctor == null) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {

        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(
            Long doctorId
    ) {

        try {

            Doctor doctor =
                    doctorRepository.findById(
                                    doctorId
                            )
                            .orElse(null);

            if (doctor == null) {
                return -1;
            }

            appointmentRepository
                    .deleteAllByDoctorId(
                            doctorId
                    );

            doctorRepository.deleteById(
                    doctorId
            );

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<?> validateDoctor(
            String email,
            String password
    ) {

        try {

            Doctor doctor =
                    doctorRepository.findByEmail(
                            email
                    );

            if (doctor == null) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                                Map.of(
                                        "message",
                                        "Doctor not found"
                                )
                        );
            }

            if (!doctor.getPassword().equals(password)) {

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                                Map.of(
                                        "message",
                                        "Invalid password"
                                )
                        );
            }

            String token =
                    tokenService.generateToken(
                            doctor.getEmail()
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

            response.put(
                    "doctor",
                    doctor
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "message",
                                    "Server error"
                            )
                    );
        }
    }

    @Transactional
    public List<Doctor> findDoctorByName(
            String name
    ) {

        return doctorRepository
                .findByNameContainingIgnoreCase(
                        name
                );
    }

    public List<Doctor> filterDoctors(
            String name,
            String time,
            String speciality
    ) {

        boolean hasName =
                name != null &&
                        !name.isBlank() &&
                        !name.equalsIgnoreCase("all");

        boolean hasTime =
                time != null &&
                        !time.isBlank() &&
                        !time.equalsIgnoreCase("all");

        boolean hasSpeciality =
                speciality != null &&
                        !speciality.isBlank() &&
                        !speciality.equalsIgnoreCase("all");

        List<Doctor> doctors;

        if (hasName && hasSpeciality) {

            doctors =
                    doctorRepository
                            .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                                    name,
                                    speciality
                            );

        } else if (hasName) {

            doctors =
                    doctorRepository
                            .findByNameContainingIgnoreCase(
                                    name
                            );

        } else if (hasSpeciality) {

            doctors =
                    doctorRepository
                            .findBySpecialtyIgnoreCase(
                                    speciality
                            );

        } else {

            doctors =
                    doctorRepository.findAll();
        }

        if (hasTime) {

            doctors =
                    filterDoctorByTime(
                            doctors,
                            time
                    );
        }

        return doctors;
    }
    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    public List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String time
    ) {

        List<Doctor> filteredDoctors =
                new ArrayList<>();

        for (Doctor doctor : doctors) {

            if (doctor.getAvailableTimes() == null) {
                continue;
            }

            for (String availableTime :
                    doctor.getAvailableTimes()) {

                String startTime =
                        availableTime.split("-")[0];

                int hour =
                        Integer.parseInt(
                                startTime.split(":")[0]
                        );

                if (time.equalsIgnoreCase("AM")
                        && hour < 12) {

                    filteredDoctors.add(doctor);
                    break;
                }

                if (time.equalsIgnoreCase("PM")
                        && hour >= 12) {

                    filteredDoctors.add(doctor);
                    break;
                }
            }
        }

        return filteredDoctors;
    }

    public List<Doctor> filterDoctorByNameAndTime(
            String name,
            String time
    ) {

        List<Doctor> doctors =
                findDoctorByName(name);

        return filterDoctorByTime(
                doctors,
                time
        );
    }

    public List<Doctor> filterDoctorByNameAndSpecility(
            String name,
            String speciality
    ) {

        return doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name,
                        speciality
                );
    }

    public List<Doctor> filterDoctorByTimeAndSpecility(
            String speciality,
            String time
    ) {

        List<Doctor> doctors =
                doctorRepository
                        .findBySpecialtyIgnoreCase(
                                speciality
                        );

        return filterDoctorByTime(
                doctors,
                time
        );
    }

    public List<Doctor> filterDoctorBySpecility(
            String speciality
    ) {

        return doctorRepository
                .findBySpecialtyIgnoreCase(
                        speciality
                );
    }

    public List<Doctor> filterDoctorsByTime(
            String time
    ) {

        List<Doctor> doctors =
                doctorRepository.findAll();

        return filterDoctorByTime(
                doctors,
                time
        );
    }
}