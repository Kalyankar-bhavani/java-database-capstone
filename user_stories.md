# User Stories – Smart Clinic Management System

---

# Admin User Stories

## User Story 1

**Title:**
*As an admin, I want to log into the portal with my username and password so that I can manage the platform securely.*

**Acceptance Criteria:**

1. Admin can enter username and password.
2. System validates login credentials.
3. Admin dashboard opens after successful login.

**Priority:** High
**Story Points:** 5

**Notes:**

* Invalid login credentials should display an error message.

---

## User Story 2

**Title:**
*As an admin, I want to log out of the portal so that I can protect system access.*

**Acceptance Criteria:**

1. Admin can click logout button.
2. Session should terminate successfully.
3. User should redirect to login page.

**Priority:** High
**Story Points:** 3

**Notes:**

* Session timeout should also log out inactive users.

---

## User Story 3

**Title:**
*As an admin, I want to add doctors to the portal so that patients can book appointments.*

**Acceptance Criteria:**

1. Admin can enter doctor details.
2. Doctor information should save successfully.
3. Newly added doctor should appear in doctor list.

**Priority:** High
**Story Points:** 5

**Notes:**

* Doctor email should be unique.

---

## User Story 4

**Title:**
*As an admin, I want to delete doctor profiles from the portal so that inactive doctors are removed.*

**Acceptance Criteria:**

1. Admin can select a doctor profile.
2. System asks for confirmation before deletion.
3. Doctor profile should be removed successfully.

**Priority:** Medium
**Story Points:** 4

**Notes:**

* Deleted doctors should not appear in appointment booking.

---

## User Story 5

**Title:**
*As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month so that I can track usage statistics.*

**Acceptance Criteria:**

1. Stored procedure executes successfully.
2. Monthly appointment count is displayed.
3. Admin can analyze appointment statistics.

**Priority:** Medium
**Story Points:** 6

**Notes:**

* Data should reflect current database records.

---

# Patient User Stories

## User Story 1

**Title:**
*As a patient, I want to view a list of doctors without logging in so that I can explore available options.*

**Acceptance Criteria:**

1. Doctor list should be publicly accessible.
2. Doctor specialization should be visible.
3. Patients should not require login access.

**Priority:** High
**Story Points:** 4

**Notes:**

* Search functionality can be added later.

---

## User Story 2

**Title:**
*As a patient, I want to sign up using my email and password so that I can book appointments.*

**Acceptance Criteria:**

1. Patient can register with email and password.
2. Email validation should be performed.
3. Account should be created successfully.

**Priority:** High
**Story Points:** 5

**Notes:**

* Duplicate email addresses should not be allowed.

---

## User Story 3

**Title:**
*As a patient, I want to log into the portal so that I can manage my bookings.*

**Acceptance Criteria:**

1. Patient can enter credentials.
2. System validates login information.
3. Dashboard opens successfully after login.

**Priority:** High
**Story Points:** 5

**Notes:**

* Failed login attempts should display an error message.

---

## User Story 4

**Title:**
*As a patient, I want to log out of the portal so that I can secure my account.*

**Acceptance Criteria:**

1. Logout button should be available.
2. Session should terminate properly.
3. User should return to login page.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Browser back navigation should not restore session.

---

## User Story 5

**Title:**
*As a patient, I want to book an hour-long appointment so that I can consult with a doctor.*

**Acceptance Criteria:**

1. Patient can select doctor and time slot.
2. Appointment duration should be one hour.
3. Booking confirmation should be displayed.

**Priority:** High
**Story Points:** 6

**Notes:**

* Double booking should not be allowed.

---

# Doctor User Stories

## User Story 1

**Title:**
*As a doctor, I want to log into the portal so that I can manage my appointments.*

**Acceptance Criteria:**

1. Doctor can log in securely.
2. System validates credentials.
3. Doctor dashboard opens successfully.

**Priority:** High
**Story Points:** 5

**Notes:**

* Login activity can be tracked later.

---

## User Story 2

**Title:**
*As a doctor, I want to log out of the portal so that I can protect my data.*

**Acceptance Criteria:**

1. Doctor can click logout button.
2. Session should terminate properly.
3. Login page should appear after logout.

**Priority:** Medium
**Story Points:** 3

**Notes:**

* Session expiration should auto logout inactive users.

---

## User Story 3

**Title:**
*As a doctor, I want to view my appointment calendar so that I can stay organized.*

**Acceptance Criteria:**

1. Doctor can view all appointments.
2. Appointments should display date and time.
3. Calendar should update dynamically.

**Priority:** High
**Story Points:** 5

**Notes:**

* Calendar filters can be added later.

---

## User Story 4

**Title:**
*As a doctor, I want to mark my unavailability so that patients can only book available slots.*

**Acceptance Criteria:**

1. Doctor can mark unavailable dates.
2. Blocked slots should not appear for booking.
3. Availability changes should save successfully.

**Priority:** High
**Story Points:** 6

**Notes:**

* Emergency leave functionality may be added later.

---

## User Story 5

**Title:**
*As a doctor, I want to update my specialization and contact information so that patients have updated details.*

**Acceptance Criteria:**

1. Doctor can edit profile information.
2. Updated details should save successfully.
3. Patients should view updated information immediately.

**Priority:** Medium
**Story Points:** 4

**Notes:**

* Profile image upload can be added later.
