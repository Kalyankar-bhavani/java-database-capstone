// patientRecordServices.js
import { getPatientAppointments } from "./services/patientServices.js";
import { createPatientRecordRow } from "./components/patientRecordRow.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadPatientRecord();
});

async function loadPatientRecord() {
    const tableBody = document.getElementById("patientRecordTableBody");

    if (!tableBody) {
        console.error("patientRecordTableBody not found");
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    const patientId = urlParams.get("id") || localStorage.getItem("patientId");
    const token = localStorage.getItem("token");

    if (!patientId || !token) {
        tableBody.innerHTML = `<tr><td colspan="4">Patient ID or token missing.</td></tr>`;
        return;
    }

    tableBody.innerHTML = `<tr><td colspan="4">Loading patient record...</td></tr>`;

    const appointments = await getPatientAppointments(patientId, token, "doctor");

    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="4">No patient records found.</td></tr>`;
        return;
    }

    appointments.forEach((appointment) => {
        tableBody.appendChild(createPatientRecordRow(appointment));
    });
}