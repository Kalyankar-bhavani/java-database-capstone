/*
  Import the openModal function to handle showing login popups/modals
  Import the base API URL from the config file
  Define constants for the admin and doctor login API endpoints using the base URL

  Use the window.onload event to ensure DOM elements are available after page load
  Inside this function:
    - Select the "adminLogin" and "doctorLogin" buttons using getElementById
    - If the admin login button exists:
        - Add a click event listener that calls openModal('adminLogin') to show the admin login modal
    - If the doctor login button exists:
        - Add a click event listener that calls openModal('doctorLogin') to show the doctor login modal


  Define a function named adminLoginHandler on the global window object
  This function will be triggered when the admin submits their login credentials

  Step 1: Get the entered username and password from the input fields
  Step 2: Create an admin object with these credentials

  Step 3: Use fetch() to send a POST request to the ADMIN_API endpoint
    - Set method to POST
    - Add headers with 'Content-Type: application/json'
    - Convert the admin object to JSON and send in the body

  Step 4: If the response is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('admin') to proceed with admin-specific behavior

  Step 5: If login fails or credentials are invalid:
    - Show an alert with an error message

  Step 6: Wrap everything in a try-catch to handle network or server errors
    - Show a generic error message if something goes wrong


  Define a function named doctorLoginHandler on the global window object
  This function will be triggered when a doctor submits their login credentials

  Step 1: Get the entered email and password from the input fields
  Step 2: Create a doctor object with these credentials

  Step 3: Use fetch() to send a POST request to the DOCTOR_API endpoint
    - Include headers and request body similar to admin login

  Step 4: If login is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('doctor') to proceed with doctor-specific behavior

  Step 5: If login fails:
    - Show an alert for invalid credentials

  Step 6: Wrap in a try-catch block to handle errors gracefully
    - Log the error to the console
    - Show a generic error message
*/

// index.js

import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = `${API_BASE_URL}/admin/login`;
const DOCTOR_API = `${API_BASE_URL}/doctor/login`;

document.addEventListener("DOMContentLoaded", () => {
    const adminBtn = document.getElementById("adminBtn");
    const doctorBtn = document.getElementById("doctorBtn");
    const patientBtn = document.getElementById("patientBtn");
    const modal = document.getElementById("modal");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }

    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }

    if (patientBtn) {
        patientBtn.addEventListener("click", () => {
            selectRole("patient");
        });
    }

    if (modal) {
        window.addEventListener("click", (event) => {
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });
    }
});

window.selectRole = function (role) {
    localStorage.setItem("userRole", role);

    if (role === "admin") {
        window.location.href = "/admin-dashboard";
    } else if (role === "doctor") {
        window.location.href = "/doctor-dashboard";
    } else if (role === "patient") {
        window.location.href = "/patient-dashboard";
    }
};

window.adminLoginHandler = async function () {
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");

    if (!usernameInput || !passwordInput) {
        alert("Admin login form not found");
        return;
    }

    const admin = {
        username: usernameInput.value.trim(),
        password: passwordInput.value.trim()
    };

    if (!admin.username || !admin.password) {
        alert("Please enter username and password");
        return;
    }

    try {
        const response = await fetch(ADMIN_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(admin)
        });

        const data = await response.json();

        if (response.ok && data.token) {
            localStorage.setItem("token", data.token);
            localStorage.setItem("userRole", "admin");
            selectRole("admin");
        } else {
            alert(data.message || "Invalid admin credentials");
        }

    } catch (error) {
        console.error("Admin login error:", error);
        alert("Something went wrong during admin login");
    }
};

window.doctorLoginHandler = async function () {
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    if (!emailInput || !passwordInput) {
        alert("Doctor login form not found");
        return;
    }

    const doctor = {
        email: emailInput.value.trim(),
        password: passwordInput.value.trim()
    };

    if (!doctor.email || !doctor.password) {
        alert("Please enter email and password");
        return;
    }

    try {
        const response = await fetch(DOCTOR_API, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        if (response.ok && data.token) {
            localStorage.setItem("token", data.token);
            localStorage.setItem("userRole", "doctor");

            if (data.doctor && data.doctor.id) {
                localStorage.setItem("doctorId", data.doctor.id);
            }

            selectRole("doctor");
        } else {
            alert(data.message || "Invalid doctor credentials");
        }

    } catch (error) {
        console.error("Doctor login error:", error);
        alert("Something went wrong during doctor login");
    }
};