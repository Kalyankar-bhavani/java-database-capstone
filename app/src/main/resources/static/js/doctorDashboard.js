/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

// doctorDashboard.js

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

document.addEventListener("DOMContentLoaded", async () => {
    localStorage.setItem("userRole", "doctor");

    const datePicker = document.getElementById("datePicker");
    const searchPatient = document.getElementById("searchPatient");

    if (datePicker) {
        datePicker.value = getTodayDate();
        datePicker.addEventListener("change", loadAppointments);
    }

    if (searchPatient) {
        searchPatient.addEventListener("input", loadAppointments);
    }

    await loadAppointments();
});

function getTodayDate() {
    const today = new Date();

    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

async function loadAppointments() {
    const tableBody = document.getElementById("patientTableBody");
    const datePicker = document.getElementById("datePicker");
    const searchPatient = document.getElementById("searchPatient");

    if (!tableBody) return;

    const token = localStorage.getItem("token");
    const doctorId = localStorage.getItem("doctorId");
    const date = datePicker?.value;
    const patientName = searchPatient?.value.trim() || "all";

    console.log("Doctor ID:", doctorId);
    console.log("Selected Date:", date);
    console.log("Patient Search:", patientName);

    if (!token) {
        tableBody.innerHTML = `<tr><td colspan="7">Token missing. Login again.</td></tr>`;
        return;
    }

    if (!doctorId) {
        tableBody.innerHTML = `<tr><td colspan="7">Doctor ID missing. Login again.</td></tr>`;
        return;
    }

    if (!date) {
        tableBody.innerHTML = `<tr><td colspan="7">Date missing.</td></tr>`;
        return;
    }

    tableBody.innerHTML = `<tr><td colspan="7">Loading appointments...</td></tr>`;

    try {
        const appointments = await getAllAppointments(
            doctorId,
            date,
            patientName,
            token
        );

        console.log("Doctor appointments:", appointments);

        tableBody.innerHTML = "";

        if (!appointments || appointments.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="7">No appointments found for ${date}.</td></tr>`;
            return;
        }

        appointments.forEach((appointment) => {
            tableBody.appendChild(createPatientRow(appointment, doctorId));
        });

    } catch (error) {
        console.error("Failed to load doctor appointments:", error);

        tableBody.innerHTML = `
            <tr>
                <td colspan="7">
                    Failed to load appointments.
                </td>
            </tr>
        `;
    }
}