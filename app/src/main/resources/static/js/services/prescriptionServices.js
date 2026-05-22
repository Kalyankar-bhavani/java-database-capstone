// prescriptionServices.js
import { API_BASE_URL } from "../config/config.js";

const PRESCRIPTION_API = `${API_BASE_URL}/prescription`;

export async function savePrescription(prescription, token) {
    try {
        const response = await fetch(`${PRESCRIPTION_API}/${token}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(prescription)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Prescription save request completed"
        };

    } catch (error) {
        console.error("savePrescription error:", error);
        return { success: false, message: "Network error while saving prescription" };
    }
}

export async function getPrescription(appointmentId, token) {
    try {
        const response = await fetch(`${PRESCRIPTION_API}/${appointmentId}/${token}`);
        const data = await response.json();

        return {
            success: response.ok,
            prescriptions: data.prescriptions || [],
            message: data.message || "Prescription fetch request completed"
        };

    } catch (error) {
        console.error("getPrescription error:", error);
        return {
            success: false,
            prescriptions: [],
            message: "Network error while fetching prescription"
        };
    }
}