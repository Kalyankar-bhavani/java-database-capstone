// render.js

window.selectRole = function (role) {
    localStorage.setItem("userRole", role);

    const token = localStorage.getItem("token");

    if (role === "admin") {
        window.location.href = `/adminDashboard/${token}`;
    } else if (role === "doctor") {
        window.location.href = `/doctorDashboard/${token}`;
    } else if (role === "patient") {
        window.location.href = `/patientDashboard/${token || "guest"}`;
    } else if (role === "loggedPatient") {
        window.location.href = "/pages/loggedPatientDashboard.html";
    }
};

window.renderContent = function () {
    const role = localStorage.getItem("userRole");

    if (!role) {
        console.log("No role found");
    }
};