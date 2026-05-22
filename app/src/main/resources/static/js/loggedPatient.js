// loggedPatient.js 
import { getDoctors } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

let allDoctors = [];

document.addEventListener("DOMContentLoaded", async () => {
    localStorage.setItem("userRole", "loggedPatient");

    await loadDoctorCards();

    const searchInput =
        document.getElementById("searchBar") ||
        document.getElementById("searchDoctor") ||
        document.querySelector("input[type='text']");

    const timeFilter =
        document.getElementById("timeFilter") ||
        document.getElementById("timeSlot") ||
        document.querySelector("select");

    const specialtyFilter =
        document.getElementById("specialtyFilter") ||
        document.getElementById("specialization") ||
        document.querySelectorAll("select")[1];

    if (searchInput) {
        searchInput.placeholder = "Search doctor by name...";
        searchInput.addEventListener("input", applyDoctorFilters);
    }

    if (timeFilter) {
        timeFilter.addEventListener("change", applyDoctorFilters);
    }

    if (specialtyFilter) {
        specialtyFilter.addEventListener("change", applyDoctorFilters);
    }
});

async function loadDoctorCards() {
    const content = document.getElementById("content");

    if (!content) {
        console.error("content div not found in loggedPatient");
        return;
    }

    content.innerHTML = "<p>Loading doctors...</p>";

    allDoctors = await getDoctors();

    content.innerHTML = "";

    if (!allDoctors || allDoctors.length === 0) {
        content.innerHTML = "<p>No doctors found.</p>";
        return;
    }

    renderDoctors(allDoctors);
}

function applyDoctorFilters() {
    const searchInput =
        document.getElementById("searchBar") ||
        document.getElementById("searchDoctor") ||
        document.querySelector("input[type='text']");

    const selects = document.querySelectorAll("select");

    const timeFilter =
        document.getElementById("timeFilter") ||
        document.getElementById("timeSlot") ||
        selects[0];

    const specialtyFilter =
        document.getElementById("specialtyFilter") ||
        document.getElementById("specialization") ||
        selects[1];

    const nameValue = searchInput?.value.toLowerCase().trim() || "";
    const timeValue = timeFilter?.value.toLowerCase().trim() || "";
    const specialtyValue = specialtyFilter?.value.toLowerCase().trim() || "";

    const filteredDoctors = allDoctors.filter((doctor) => {
        const doctorName = (doctor.name || "").toLowerCase();
        const doctorSpecialty = (doctor.specialty || "").toLowerCase();

        const availableTimes = doctor.availableTimes || [];

        const matchesName =
            !nameValue || doctorName.includes(nameValue);

        const matchesSpecialty =
            !specialtyValue ||
            specialtyValue === "all" ||
            doctorSpecialty.includes(specialtyValue);

        const matchesTime =
            !timeValue ||
            timeValue === "all" ||
            availableTimes.some((time) => {
                const hour = Number(time.split(":")[0]);

                if (timeValue === "am") {
                    return hour < 12;
                }

                if (timeValue === "pm") {
                    return hour >= 12;
                }

                return time.toLowerCase().includes(timeValue);
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