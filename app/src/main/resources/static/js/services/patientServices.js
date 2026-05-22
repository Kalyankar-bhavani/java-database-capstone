// patientServices.js

import { API_BASE_URL } from "../config/config.js";

const PATIENT_API = `${API_BASE_URL}/patient`;

export async function patientSignup(patient) {
    try {
        const response = await fetch(PATIENT_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(patient)
        });

        const data = await response.json();

        return {
            success: response.ok,
            message: data.message || "Patient signup request completed"
        };

    } catch (error) {
        console.error("patientSignup error:", error);
        return { success: false, message: "Network error while registering patient" };
    }
}

export async function patientLogin(patient) {
    try {
        const response = await fetch(`${PATIENT_API}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(patient)
        });

        const data = await response.json();

        return {
            success: response.ok,
            token: data.token || null,
            patient: data.patient || null,
            message: data.message || "Patient login request completed"
        };

    } catch (error) {
        console.error("patientLogin error:", error);
        return {
            success: false,
            token: null,
            patient: null,
            message: "Network error while patient login"
        };
    }
}

export async function getPatientData(token) {
    try {
        const response = await fetch(`${PATIENT_API}/${token}`);
        const data = await response.json();

        return response.ok ? data.patient || data : null;

    } catch (error) {
        console.error("getPatientData error:", error);
        return null;
    }
}

export async function getPatientProfile(token) {
    return await getPatientData(token);
}

export async function getPatientAppointments(patientId, token, user = "patient") {
    try {
        const response = await fetch(`${PATIENT_API}/appointment/${patientId}/${user}/${token}`);
        const data = await response.json();

        return response.ok ? data.appointments || data || [] : [];

    } catch (error) {
        console.error("getPatientAppointments error:", error);
        return [];
    }
}

export async function filterAppointments(condition = "all", name = "all", token) {
    try {
        const safeCondition = condition && condition.trim() !== "" ? condition.trim() : "all";
        const safeName = name && name.trim() !== "" ? name.trim() : "all";

        const response = await fetch(
            `${PATIENT_API}/filter/${encodeURIComponent(safeCondition)}/${encodeURIComponent(safeName)}/${token}`
        );

        const data = await response.json();

        return response.ok ? data.appointments || data || [] : [];

    } catch (error) {
        console.error("filterAppointments error:", error);
        return [];
    }
}