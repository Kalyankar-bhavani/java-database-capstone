// patientRows.js
// patientRows.js

export function createPatientRow(appointment, doctorId) {
    const tr = document.createElement("tr");

    const appointmentId =
        appointment.id ||
        appointment.appointmentId;

    const patientId =
        appointment.patient?.id ||
        appointment.patientId ||
        "";

    const patientName =
        appointment.patient?.name ||
        appointment.patientName ||
        "";

    const patientPhone =
        appointment.patient?.phone ||
        appointment.patientPhone ||
        "";

    const patientEmail =
        appointment.patient?.email ||
        appointment.patientEmail ||
        "";

    const date = appointment.appointmentTime
        ? appointment.appointmentTime.split("T")[0]
        : "";

    const time = appointment.appointmentTime
        ? appointment.appointmentTime.split("T")[1]?.substring(0, 5)
        : "";

    tr.innerHTML = `
        <td class="patient-id">${patientId}</td>
        <td>${patientName}</td>
        <td>${patientPhone}</td>
        <td>${patientEmail}</td>
        <td>${date}</td>
        <td>${time}</td>
        <td>
            <img
                src="/assets/images/addPrescriptionIcon/addPrescription.png"
                alt="Add Prescription"
                class="prescription-btn"
            >
        </td>
    `;

    tr.querySelector(".patient-id")?.addEventListener("click", () => {
        window.location.href =
            `/pages/patientRecord.html?id=${patientId}&doctorId=${doctorId}`;
    });

    tr.querySelector(".prescription-btn")?.addEventListener("click", () => {
        if (!appointmentId) {
            alert("Appointment ID missing.");
            return;
        }

        window.location.href =
            `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${encodeURIComponent(patientName)}`;
    });

    return tr;
}