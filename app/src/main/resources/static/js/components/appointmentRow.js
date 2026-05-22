// appointmentRow.js
export function getAppointments(appointment) {
    const tr = document.createElement("tr");

    const patientName = appointment.patient?.name || appointment.patientName || "N/A";
    const doctorName = appointment.doctor?.name || appointment.doctorName || "N/A";

    const date = appointment.appointmentTime
        ? appointment.appointmentTime.split("T")[0]
        : appointment.date || "";

    const time = appointment.appointmentTime
        ? appointment.appointmentTime.split("T")[1]?.substring(0, 5)
        : appointment.time || "";

    tr.innerHTML = `
        <td>${patientName}</td>
        <td>${doctorName}</td>
        <td>${date}</td>
        <td>${time}</td>
        <td>
            <img
                src="/assets/images/addPrescriptionIcon/addPrescription.png"
                alt="Prescription"
                class="prescription-btn"
                data-id="${appointment.id}"
            >
        </td>
    `;

    const prescriptionBtn = tr.querySelector(".prescription-btn");

    if (prescriptionBtn) {
        prescriptionBtn.addEventListener("click", () => {
            window.location.href =
                `/pages/addPrescription.html?mode=view&appointmentId=${appointment.id}&patientName=${encodeURIComponent(patientName)}`;
        });
    }

    return tr;
}