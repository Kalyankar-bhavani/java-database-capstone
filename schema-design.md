# Smart Clinic Management System – Database Schema Design

## Section 1: MySQL Database Design

The Smart Clinic Management System uses MySQL to store structured relational data such as patient information, doctor details, appointments, and administrator records. The database design follows normalization principles and uses primary keys and foreign keys to maintain data integrity.

---

# Table: patients

| Column Name   | Data Type    | Constraints                 |
| ------------- | ------------ | --------------------------- |
| patient_id    | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| full_name     | VARCHAR(100) | NOT NULL                    |
| email         | VARCHAR(100) | UNIQUE, NOT NULL            |
| phone_number  | VARCHAR(15)  | NOT NULL                    |
| gender        | VARCHAR(10)  | NOT NULL                    |
| date_of_birth | DATE         | NOT NULL                    |
| password      | VARCHAR(255) | NOT NULL                    |
| created_at    | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP   |

### Notes

* Email must be unique for patient authentication.
* Password should be stored in encrypted format.

---

# Table: doctors

| Column Name         | Data Type    | Constraints                 |
| ------------------- | ------------ | --------------------------- |
| doctor_id           | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| full_name           | VARCHAR(100) | NOT NULL                    |
| specialization      | VARCHAR(100) | NOT NULL                    |
| email               | VARCHAR(100) | UNIQUE, NOT NULL            |
| phone_number        | VARCHAR(15)  | NOT NULL                    |
| availability_status | BOOLEAN      | DEFAULT TRUE                |
| created_at          | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP   |

### Notes

* Doctors can update specialization and availability status.
* Email uniqueness avoids duplicate doctor accounts.

---

# Table: appointments

| Column Name        | Data Type   | Constraints                                 |
| ------------------ | ----------- | ------------------------------------------- |
| appointment_id     | BIGINT      | PRIMARY KEY, AUTO_INCREMENT                 |
| patient_id         | BIGINT      | FOREIGN KEY REFERENCES patients(patient_id) |
| doctor_id          | BIGINT      | FOREIGN KEY REFERENCES doctors(doctor_id)   |
| appointment_date   | DATE        | NOT NULL                                    |
| appointment_time   | TIME        | NOT NULL                                    |
| appointment_status | VARCHAR(20) | DEFAULT 'Scheduled'                         |
| created_at         | TIMESTAMP   | DEFAULT CURRENT_TIMESTAMP                   |

### Notes

* Appointment status can be Scheduled, Completed, or Cancelled.
* Foreign keys maintain relationships between patients and doctors.

---

# Table: admin

| Column Name | Data Type    | Constraints                 |
| ----------- | ------------ | --------------------------- |
| admin_id    | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| username    | VARCHAR(50)  | UNIQUE, NOT NULL            |
| email       | VARCHAR(100) | UNIQUE, NOT NULL            |
| password    | VARCHAR(255) | NOT NULL                    |
| role        | VARCHAR(20)  | DEFAULT 'ADMIN'             |
| created_at  | TIMESTAMP    | DEFAULT CURRENT_TIMESTAMP   |

### Notes

* Admin accounts manage doctors, patients, and appointments.
* Passwords should be securely encrypted.

---

## Section 2: MongoDB Collection Design

The Smart Clinic Management System uses MongoDB for flexible document-based storage. MongoDB is suitable for storing prescription records because prescription details may vary depending on patient conditions and treatments.

---

# Collection: prescriptions

```json id="8kghl8"
{
  "_id": "665f34ab89d12f00123abc45",
  "appointmentId": 101,
  "patient": {
    "patientId": 1,
    "name": "Rahul Sharma",
    "age": 28
  },
  "doctor": {
    "doctorId": 5,
    "name": "Dr. Priya Reddy",
    "specialization": "Cardiology"
  },
  "diagnosis": "High Blood Pressure",
  "medications": [
    {
      "medicineName": "Amlodipine",
      "dosage": "5mg",
      "frequency": "Once Daily",
      "duration": "30 Days"
    },
    {
      "medicineName": "Aspirin",
      "dosage": "75mg",
      "frequency": "Once Daily",
      "duration": "15 Days"
    }
  ],
  "notes": "Patient should reduce salt intake and exercise regularly.",
  "followUpDate": "2026-05-20",
  "createdAt": "2026-05-07T10:30:00Z"
}
```

### Notes

* Nested patient and doctor objects improve document readability.
* The medications array allows multiple medicines in a single prescription document.
* MongoDB provides flexibility for changing prescription formats without altering relational schemas.

---

## Design Justification

1. MySQL is used for structured relational data because appointments, patients, doctors, and admin records require strong relationships and constraints.

2. MongoDB is used for prescriptions because medical prescriptions can contain flexible and nested structures that vary across patients.

3. Foreign keys in MySQL ensure referential integrity between patients, doctors, and appointments.

4. MongoDB arrays and nested documents make prescription storage efficient and scalable.

5. This hybrid database design improves maintainability, flexibility, and scalability for the Smart Clinic Management System.
