import { openModal } from "./components/modals.js";
import { patientSignup, patientLogin, getPatientData } from "./services/patientServices.js";
import { getDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

let allDoctors = [];

document.addEventListener("DOMContentLoaded", async () => {
    const currentRole = localStorage.getItem("userRole");

    if (!currentRole) {
        localStorage.setItem("userRole", "patient");
    }

    const patientLoginBtn = document.getElementById("patientLogin");
    const patientSignupBtn = document.getElementById("patientSignup");

    if (patientLoginBtn) {
        patientLoginBtn.addEventListener("click", () => {
            openModal("patientLogin");
        });
    }

    if (patientSignupBtn) {
        patientSignupBtn.addEventListener("click", () => {
            openModal("patientSignup");
        });
    }

    await loadDoctors();

    initializeFilters();
});

async function loadDoctors() {
    const content = document.getElementById("content");

    if (!content) {
        console.error("content div not found");
        return;
    }

    content.innerHTML = "<p>Loading doctors...</p>";

    allDoctors = await getDoctors();

    renderDoctors(allDoctors);
}

function initializeFilters() {
    const searchBar = document.getElementById("searchBar");
    const timeFilter = document.getElementById("timeFilter");
    const specialtyFilter = document.getElementById("specialtyFilter");

    if (searchBar) {
        searchBar.addEventListener("input", applyFilters);
    }

    if (timeFilter) {
        timeFilter.addEventListener("change", applyFilters);
    }

    if (specialtyFilter) {
        specialtyFilter.addEventListener("change", applyFilters);
    }
}

function applyFilters() {
    const searchValue =
        document.getElementById("searchBar")?.value.toLowerCase().trim() || "";

    const timeValue =
        document.getElementById("timeFilter")?.value.toLowerCase() || "all";

    const specialtyValue =
        document.getElementById("specialtyFilter")?.value.toLowerCase() || "all";

    const filteredDoctors = allDoctors.filter((doctor) => {
        const doctorName = (doctor.name || "").toLowerCase();
        const doctorSpecialty = (doctor.specialty || "").toLowerCase();
        const availableTimes = doctor.availableTimes || [];

        const matchesName =
            !searchValue || doctorName.includes(searchValue);

        const matchesSpecialty =
            specialtyValue === "all" ||
            doctorSpecialty.includes(specialtyValue);

        const matchesTime =
            timeValue === "all" ||
            availableTimes.some((time) => {
                const hour = Number(time.split(":")[0]);

                if (timeValue === "am") {
                    return hour < 12;
                }

                if (timeValue === "pm") {
                    return hour >= 12;
                }

                return true;
            });

        return matchesName && matchesSpecialty && matchesTime;
    });

    renderDoctors(filteredDoctors);
}

function renderDoctors(doctors) {
    const content = document.getElementById("content");

    if (!content) return;

    content.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        content.innerHTML = "<p>No matching doctors found.</p>";
        return;
    }

    doctors.forEach((doctor) => {
        content.appendChild(createDoctorCard(doctor));
    });
}

window.signupPatient = async function () {
    const patient = {
        name: document.getElementById("name")?.value.trim(),
        email: document.getElementById("email")?.value.trim(),
        password: document.getElementById("password")?.value.trim(),
        phone: document.getElementById("phone")?.value.trim(),
        address: document.getElementById("address")?.value.trim()
    };

    if (
        !patient.name ||
        !patient.email ||
        !patient.password ||
        !patient.phone ||
        !patient.address
    ) {
        alert("Please fill all signup details.");
        return;
    }

    const result = await patientSignup(patient);

    if (result.success) {
        alert("Signup successful. Please login now.");
        window.location.reload();
    } else {
        alert(result.message || "Signup failed.");
    }
};

window.loginPatient = async function () {
    const patient = {
        email: document.getElementById("email")?.value.trim(),
        password: document.getElementById("password")?.value.trim()
    };

    if (!patient.email || !patient.password) {
        alert("Please enter email and password.");
        return;
    }

    const result = await patientLogin(patient);

    console.log("Patient login result:", result);

    if (!result.success) {
        alert(result.message || "Invalid patient email or password.");
        return;
    }

    if (!result.token) {
        alert("Login success but token missing.");
        return;
    }

    localStorage.setItem("token", result.token);
    localStorage.setItem("userRole", "loggedPatient");

    let patientId =
        result.patient?.id ||
        result.patient?.patientId ||
        result.id ||
        result.patientId;

    if (!patientId) {
        const patientData = await getPatientData(result.token);

        patientId =
            patientData?.id ||
            patientData?.patientId;
    }

    if (!patientId) {
        alert("Patient ID missing. Backend is not returning patient ID.");
        return;
    }

    localStorage.setItem("patientId", patientId);

    window.location.href = "/pages/loggedPatientDashboard.html";
};