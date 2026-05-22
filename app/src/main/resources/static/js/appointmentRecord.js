// appointmentRecord.js
import { getPatientAppointments } from "./services/patientServices.js";
import { getAppointments } from "./components/appointmentRow.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadAppointmentRecords();
});

async function loadAppointmentRecords() {
    const tableBody = document.getElementById("appointmentTableBody");

    if (!tableBody) {
        console.error("appointmentTableBody not found in appointmentRecord");
        return;
    }

    const token = localStorage.getItem("token");
    const patientId = localStorage.getItem("patientId");

    if (!token || !patientId) {
        tableBody.innerHTML = `<tr><td colspan="5">Please login again.</td></tr>`;
        return;
    }

    tableBody.innerHTML = `<tr><td colspan="5">Loading records...</td></tr>`;

    const appointments = await getPatientAppointments(patientId, token, "patient");

    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="5">No records found.</td></tr>`;
        return;
    }

    appointments.forEach((appointment) => {
        tableBody.appendChild(getAppointments(appointment));
    });
}