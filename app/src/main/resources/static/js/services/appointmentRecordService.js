import { API_BASE_URL } from "../config/config.js";

const APPOINTMENT_API = `${API_BASE_URL}/appointments`;
const PATIENT_API = `${API_BASE_URL}/patient`;

export async function getAllAppointments(doctorId, date, patientName = "all", token) {
    try {
        const safePatientName =
            patientName && patientName.trim() !== "" ? patientName.trim() : "all";

        const response = await fetch(
            `${APPOINTMENT_API}/${doctorId}/${date}/${encodeURIComponent(safePatientName)}/${token}`
        );

        const data = await response.json();

        console.log("Doctor appointments response:", data);

        if (!response.ok) {
            console.error(data.message || "Failed to fetch appointments");
            return [];
        }

        return data.appointments || [];

    } catch (error) {
        console.error("getAllAppointments error:", error);
        return [];
    }
}

export async function bookAppointment(appointment, token) {
    try {
        const response = await fetch(`${APPOINTMENT_API}/${token}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(appointment)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Appointment request completed"
        };

    } catch (error) {
        console.error("bookAppointment error:", error);
        return { success: false, message: "Network error while booking appointment" };
    }
}

export async function updateAppointment(appointment, token) {
    try {
        const appointmentId = appointment.id;
        const patientId = appointment.patient?.id;

        if (!appointmentId || !patientId) {
            return { success: false, message: "Appointment ID or Patient ID missing" };
        }

        const response = await fetch(
            `${APPOINTMENT_API}/${appointmentId}/${patientId}/${token}`,
            {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(appointment)
            }
        );

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Appointment update request completed"
        };

    } catch (error) {
        console.error("updateAppointment error:", error);
        return { success: false, message: "Network error while updating appointment" };
    }
}

export async function cancelAppointment(appointmentId, patientId, token) {
    try {
        const response = await fetch(`${APPOINTMENT_API}/${appointmentId}/${patientId}/${token}`, {
            method: "DELETE"
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Appointment cancel request completed"
        };

    } catch (error) {
        console.error("cancelAppointment error:", error);
        return { success: false, message: "Network error while cancelling appointment" };
    }
}

export async function getAppointmentRecord(patientId, token, user = "patient") {
    try {
        const response = await fetch(`${PATIENT_API}/appointment/${patientId}/${user}/${token}`);
        const data = await response.json();

        return response.ok ? data : [];

    } catch (error) {
        console.error("getAppointmentRecord error:", error);
        return [];
    }
}