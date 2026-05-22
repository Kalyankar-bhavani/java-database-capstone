// patientRows.js
export function createPatientRow(appointment, doctorId) {
    const tr = document.createElement("tr");

    const patient = appointment.patient || {};

    const date = appointment.appointmentTime
        ? appointment.appointmentTime.split("T")[0]
        : "";

    const time = appointment.appointmentTime
        ? appointment.appointmentTime.split("T")[1]?.substring(0, 5)
        : "";

    tr.innerHTML = `
        <td class="patient-id">${patient.id || ""}</td>
        <td>${patient.name || ""}</td>
        <td>${patient.phone || ""}</td>
        <td>${patient.email || ""}</td>
        <td>${date}</td>
        <td>${time}</td>
        <td>
            <img
                src="/assets/images/addPrescriptionIcon/addPrescription.png"
                alt="Add Prescription"
                class="prescription-btn"
                data-id="${appointment.id}"
            >
        </td>
    `;

    const patientIdCell = tr.querySelector(".patient-id");
    if (patientIdCell) {
        patientIdCell.addEventListener("click", () => {
            window.location.href =
                `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
        });
    }

    const prescriptionBtn = tr.querySelector(".prescription-btn");
    if (prescriptionBtn) {
        prescriptionBtn.addEventListener("click", () => {
            window.location.href =
                `/pages/addPrescription.html?appointmentId=${appointment.id}&patientName=${encodeURIComponent(patient.name || "")}`;
        });
    }

    return tr;
}