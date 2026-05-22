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
    const mode = urlParams.get("mode");
    const patientName = urlParams.get("patientName");
    const token = localStorage.getItem("token");

    if (!appointmentId) {
        alert("Appointment ID missing");
        return;
    }

    if (!token) {
        alert("Login token missing. Please login again.");
        window.location.href = "/";
        return;
    }

    if (heading) {
        heading.innerHTML = mode === "view"
            ? "View <span>Prescription</span>"
            : "Add <span>Prescription</span>";
    }

    if (patientNameInput && patientName) {
        patientNameInput.value = decodeURIComponent(patientName);
    }

    const result = await getPrescription(appointmentId, token);

    if (result.success && result.prescriptions.length > 0) {
        const prescription = result.prescriptions[0];

        if (patientNameInput) patientNameInput.value = prescription.patientName || patientName || "";
        if (medicinesInput) medicinesInput.value = prescription.medication || "";
        if (dosageInput) dosageInput.value = prescription.dosage || "";
        if (notesInput) notesInput.value = prescription.doctorNotes || "";

        if (savePrescriptionBtn && mode !== "view") {
            savePrescriptionBtn.style.display = "none";
        }
    }

    if (mode === "view") {
        if (patientNameInput) patientNameInput.disabled = true;
        if (medicinesInput) medicinesInput.disabled = true;
        if (dosageInput) dosageInput.disabled = true;
        if (notesInput) notesInput.disabled = true;
        if (savePrescriptionBtn) savePrescriptionBtn.style.display = "none";
    }

    if (savePrescriptionBtn) {
        savePrescriptionBtn.addEventListener("click", async (event) => {
            event.preventDefault();

            const prescription = {
                patientName: patientNameInput?.value.trim(),
                medication: medicinesInput?.value.trim(),
                dosage: dosageInput?.value.trim(),
                doctorNotes: notesInput?.value.trim(),
                appointmentId: Number(appointmentId)
            };

            if (!prescription.patientName) {
                alert("Patient name missing");
                return;
            }

            if (!prescription.medication) {
                alert("Please enter medicine name");
                return;
            }

            if (!prescription.dosage) {
                alert("Please enter dosage");
                return;
            }

            const saveResult = await savePrescription(prescription, token);

            if (saveResult.success) {
                alert("Prescription saved successfully");
                window.location.href = `/doctorDashboard/${token}`;
            } else {
                alert(saveResult.message || "Failed to save prescription");
            }
        });
    }
});