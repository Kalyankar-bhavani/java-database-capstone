// patientRecordRow.js
export function createPatientRecordRow(appointment) {
    const tr = document.createElement("tr");

    const date = appointment.appointmentTime
        ? appointment.appointmentTime.split("T")[0]
        : appointment.appointmentDate || "";

    tr.innerHTML = `
        <td>${date}</td>
        <td>${appointment.id || ""}</td>
        <td>${appointment.patient?.id || appointment.patientId || ""}</td>
        <td>
            <img
                src="/assets/images/addPrescriptionIcon/addPrescription.png"
                alt="View Prescription"
                class="prescription-btn"
                data-id="${appointment.id}"
            >
        </td>
    `;

    const prescriptionBtn = tr.querySelector(".prescription-btn");

    if (prescriptionBtn) {
        prescriptionBtn.addEventListener("click", () => {
            window.location.href =
                `/pages/addPrescription.html?mode=view&appointmentId=${appointment.id}`;
        });
    }

    return tr;
}