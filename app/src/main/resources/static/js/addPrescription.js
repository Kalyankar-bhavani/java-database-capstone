//addPrescription.js
import {
    savePrescription,
    getPrescription
} from "./services/prescriptionServices.js";

document.addEventListener("DOMContentLoaded", async () => {
    const savePrescriptionBtn = document.getElementById("savePrescription");
    const patientNameInput = document.getElementById("patientName");
    const medicinesInput = document.getElementById("medicines");
    const dosageInput = document.getElementById("dosage");
    const notesInput = document.getElementById("notes");
    const heading = document.getElementById("heading");

    const urlParams = new URLSearchParams(window.location.search);

    const appointmentId = urlParams.get("appointmentId");
    const patientName = urlParams.get("patientName");
    const mode = urlParams.get("mode");

    const token = localStorage.getItem("token");

    if (!appointmentId) {
        alert("Appointment ID missing. Please open prescription from doctor dashboard.");
        window.location.href = "/doctor-dashboard";
        return;
    }

    if (!token) {
        alert("Token missing. Please login again.");
        window.location.href = "/";
        return;
    }

    if (heading) {
        heading.innerHTML =
            mode === "view"
                ? "View <span>Prescription</span>"
                : "Add <span>Prescription</span>";
    }

    if (patientNameInput) {
        patientNameInput.value = decodeURIComponent(patientName || "");
    }

    try {
        const response = await getPrescription(appointmentId, token);

        if (
            response &&
            response.prescriptions &&
            response.prescriptions.length > 0
        ) {
            const prescription = response.prescriptions[0];

            patientNameInput.value = prescription.patientName || "";
            medicinesInput.value = prescription.medication || "";
            dosageInput.value = prescription.dosage || "";
            notesInput.value = prescription.doctorNotes || "";
        }
    } catch (error) {
        console.warn("No existing prescription found.");
    }

    if (mode === "view") {
        patientNameInput.disabled = true;
        medicinesInput.disabled = true;
        dosageInput.disabled = true;
        notesInput.disabled = true;

        if (savePrescriptionBtn) {
            savePrescriptionBtn.style.display = "none";
        }

        return;
    }

    savePrescriptionBtn?.addEventListener("click", async (e) => {
        e.preventDefault();

        const prescription = {
            patientName: patientNameInput.value.trim(),
            medication: medicinesInput.value.trim(),
            dosage: dosageInput.value.trim(),
            doctorNotes: notesInput.value.trim(),
            appointmentId: Number(appointmentId)
        };

        if (
            !prescription.patientName ||
            !prescription.medication ||
            !prescription.dosage
        ) {
            alert("Please fill patient name, medicine and dosage.");
            return;
        }

        const result = await savePrescription(prescription, token);

        if (result.success) {
            alert("Prescription saved successfully.");
            window.location.href = "/doctor-dashboard";
        } else {
            alert(result.message || "Failed to save prescription.");
        }
    });
});