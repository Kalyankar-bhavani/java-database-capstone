// updateAppointment.js
import { getDoctors } from "./services/doctorServices.js";
import { updateAppointment } from "./services/appointmentRecordService.js";

let selectedAppointment = null;
let allDoctors = [];

document.addEventListener("DOMContentLoaded", async () => {
    const appointmentData = localStorage.getItem("selectedAppointment");

    if (!appointmentData) {
        alert("No appointment selected.");
        window.location.href = "/pages/patientAppointments.html";
        return;
    }

    selectedAppointment = JSON.parse(appointmentData);

    const patientNameInput = document.getElementById("patientName");
    const doctorSelect = document.getElementById("doctorSelect");
    const dateInput = document.getElementById("appointmentDate");
    const timeSelect = document.getElementById("appointmentTime");
    const updateBtn = document.getElementById("updateAppointmentBtn");
    const cancelBtn = document.getElementById("cancelBtn");

    if (patientNameInput) {
        patientNameInput.value =
            selectedAppointment.patient?.name ||
            selectedAppointment.patientName ||
            "";
    }

    if (dateInput && selectedAppointment.appointmentTime) {
        dateInput.value = selectedAppointment.appointmentTime.split("T")[0];
    }

    allDoctors = await getDoctors();

    if (doctorSelect) {
        doctorSelect.innerHTML = `<option value="">Select Doctor</option>`;

        allDoctors.forEach((doctor) => {
            const option = document.createElement("option");

            option.value = doctor.id;
            option.textContent = `Dr. ${doctor.name} - ${doctor.specialty}`;

            if (String(doctor.id) === String(selectedAppointment.doctor?.id)) {
                option.selected = true;
                loadDoctorTimes(doctor, timeSelect);
            }

            doctorSelect.appendChild(option);
        });

        doctorSelect.addEventListener("change", () => {
            const selectedDoctor = allDoctors.find(
                (doctor) => String(doctor.id) === String(doctorSelect.value)
            );

            loadDoctorTimes(selectedDoctor, timeSelect);
        });
    }

    if (updateBtn) {
        updateBtn.addEventListener("click", updateSelectedAppointment);
    }

    if (cancelBtn) {
        cancelBtn.addEventListener("click", () => {
            window.location.href = "/pages/patientAppointments.html";
        });
    }
});

function loadDoctorTimes(doctor, timeSelect) {
    if (!timeSelect) return;

    timeSelect.innerHTML = `<option value="">Select Time</option>`;

    if (!doctor || !doctor.availableTimes) return;

    const currentTime = selectedAppointment.appointmentTime
        ? selectedAppointment.appointmentTime.split("T")[1]?.substring(0, 5)
        : "";

    doctor.availableTimes.forEach((time) => {
        const option = document.createElement("option");

        option.value = time;
        option.textContent = time;

        if (time.startsWith(currentTime)) {
            option.selected = true;
        }

        timeSelect.appendChild(option);
    });
}

async function updateSelectedAppointment(event) {
    event.preventDefault();

    const token = localStorage.getItem("token");

    const doctorSelect = document.getElementById("doctorSelect");
    const dateInput = document.getElementById("appointmentDate");
    const timeSelect = document.getElementById("appointmentTime");

    const selectedDoctor = allDoctors.find(
        (doctor) => String(doctor.id) === String(doctorSelect?.value)
    );

    const date = dateInput?.value;
    const time = timeSelect?.value;

    if (!token) {
        alert("Token missing. Please login again.");
        return;
    }

    if (!selectedDoctor) {
        alert("Please select doctor.");
        return;
    }

    if (!date) {
        alert("Please select date.");
        return;
    }

    if (!time) {
        alert("Please select time.");
        return;
    }

    const startTime = time.includes("-") ? time.split("-")[0] : time;

    const updatedAppointment = {
        ...selectedAppointment,
        doctor: { id: Number(selectedDoctor.id) },
        patient: {
            id:
                selectedAppointment.patient?.id ||
                localStorage.getItem("patientId")
        },
        appointmentTime: `${date}T${startTime}:00`
    };

    const result = await updateAppointment(updatedAppointment, token);

    if (result.success) {
        alert("Appointment updated successfully.");
        localStorage.removeItem("selectedAppointment");
        window.location.href = "/pages/patientAppointments.html";
    } else {
        alert(result.message || "Failed to update appointment.");
    }
}