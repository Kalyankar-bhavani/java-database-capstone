// patientAppointment.js

import {
    getPatientAppointments
} from "./services/patientServices.js";

import {
    cancelAppointment
} from "./services/appointmentRecordService.js";

import {
    getDoctors
} from "./services/doctorServices.js";

let allAppointments = [];
let allDoctors = [];

document.addEventListener("DOMContentLoaded", async () => {
    await loadAppointments();

    const searchInput = document.getElementById("searchAppointment");
    const statusFilter = document.getElementById("statusFilter");

    if (searchInput) {
        searchInput.addEventListener("input", applyFilters);
    }

    if (statusFilter) {
        statusFilter.addEventListener("change", applyFilters);
    }
});

async function loadAppointments() {
    const tableBody = document.getElementById("appointmentTableBody");

    if (!tableBody) {
        console.error("appointmentTableBody not found");
        return;
    }

    const token = localStorage.getItem("token");
    const patientId = localStorage.getItem("patientId");

    if (!token || !patientId) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6">Please login again.</td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = `
        <tr>
            <td colspan="6">Loading appointments...</td>
        </tr>
    `;

    allAppointments = await getPatientAppointments(
        patientId,
        token,
        "patient"
    );

    allDoctors = await getDoctors();

    renderAppointments(allAppointments);
}

function getDoctorName(appointment) {
    if (appointment.doctorName) {
        return appointment.doctorName;
    }

    if (appointment.doctor?.name) {
        return appointment.doctor.name;
    }

    return "N/A";
}

function applyFilters() {
    const searchValue =
        document.getElementById("searchAppointment")
            ?.value
            .toLowerCase()
            .trim() || "";

    const statusValue =
        document.getElementById("statusFilter")
            ?.value
            .toLowerCase() || "all";

    const filteredAppointments = allAppointments.filter((appointment) => {
        const doctorName =
            getDoctorName(appointment).toLowerCase();

        const status =
            appointment.status === 1
                ? "completed"
                : "booked";

        const matchesSearch =
            !searchValue ||
            doctorName.includes(searchValue);

        const matchesStatus =
            statusValue === "all" ||
            status === statusValue;

        return matchesSearch && matchesStatus;
    });

    renderAppointments(filteredAppointments);
}

function renderAppointments(appointments) {
    const tableBody = document.getElementById("appointmentTableBody");

    if (!tableBody) return;

    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6">No appointments found.</td>
            </tr>
        `;
        return;
    }

    appointments.forEach((appointment) => {
        const tr = document.createElement("tr");

        const doctorName = getDoctorName(appointment);

        const date =
            appointment.appointmentTime
                ? appointment.appointmentTime.split("T")[0]
                : "N/A";

        const time =
            appointment.appointmentTime
                ? appointment.appointmentTime
                    .split("T")[1]
                    ?.substring(0, 5)
                : "N/A";

        const status =
            appointment.status === 1
                ? "Completed"
                : "Booked";

        tr.innerHTML = `
            <td>${appointment.id}</td>
            <td>${doctorName}</td>
            <td>${date}</td>
            <td>${time}</td>
            <td>${status}</td>

            <td>
                <button class="cancel-btn">
                    Cancel
                </button>

                <button class="update-btn">
                    Update
                </button>
            </td>
        `;

        const cancelBtn = tr.querySelector(".cancel-btn");
        const updateBtn = tr.querySelector(".update-btn");

        if (cancelBtn) {
            cancelBtn.addEventListener("click", async () => {
                const token = localStorage.getItem("token");
                const patientId = localStorage.getItem("patientId");

                const result = await cancelAppointment(
                    appointment.id,
                    patientId,
                    token
                );

                if (result.success) {
                    alert("Appointment cancelled.");
                    await loadAppointments();
                } else {
                    alert(result.message || "Cancel failed.");
                }
            });
        }

        if (updateBtn) {
            updateBtn.addEventListener("click", () => {
                localStorage.setItem(
                    "selectedAppointment",
                    JSON.stringify(appointment)
                );

                window.location.href =
                    "/pages/updateAppointment.html";
            });
        }

        tableBody.appendChild(tr);
    });
}