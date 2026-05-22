/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/

import { deleteDoctor } from "../services/doctorServices.js";
import { bookAppointment } from "../services/appointmentRecordService.js";

export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    const role = localStorage.getItem("userRole");

    card.innerHTML = `
        <h3>Dr. ${doctor.name || "Doctor"}</h3>
        <p><strong>Specialty:</strong> ${doctor.specialty || "N/A"}</p>
        <p><strong>Email:</strong> ${doctor.email || "N/A"}</p>
        <p><strong>Phone:</strong> ${doctor.phone || "N/A"}</p>
        <p><strong>Available Times:</strong> ${
            doctor.availableTimes && doctor.availableTimes.length > 0
                ? doctor.availableTimes.join(", ")
                : "Not Available"
        }</p>
    `;

    if (role === "admin") {
        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "Delete Doctor";
        deleteBtn.classList.add("delete-btn");

        deleteBtn.addEventListener("click", async () => {
            const token = localStorage.getItem("token");

            const result = await deleteDoctor(doctor.id, token);

            if (result.success) {
                alert("Doctor deleted successfully");
                card.remove();
            } else {
                alert(result.message || "Failed to delete doctor");
            }
        });

        card.appendChild(deleteBtn);
    }

    if (role === "loggedPatient") {
        const bookBtn = document.createElement("button");
        bookBtn.textContent = "Book Appointment";
        bookBtn.classList.add("book-btn");

        bookBtn.addEventListener("click", () => {
            openBookingForm(doctor);
        });

        card.appendChild(bookBtn);
    }

    return card;
}

function openBookingForm(doctor) {
    const oldBox = document.getElementById("bookingBox");
    if (oldBox) oldBox.remove();

    const box = document.createElement("div");
    box.id = "bookingBox";

    box.style.position = "fixed";
    box.style.top = "50%";
    box.style.left = "50%";
    box.style.transform = "translate(-50%, -50%)";
    box.style.background = "white";
    box.style.padding = "25px";
    box.style.borderRadius = "10px";
    box.style.boxShadow = "0 0 20px rgba(0,0,0,0.3)";
    box.style.zIndex = "9999";

    box.innerHTML = `
        <h2>Book Appointment</h2>

        <p><strong>Doctor:</strong> Dr. ${doctor.name}</p>
        <p><strong>Specialty:</strong> ${doctor.specialty}</p>

        <input type="date" id="appointmentDate" class="input-field">

        <select id="appointmentTime" class="input-field">
            <option value="">Select Time</option>
            ${
                doctor.availableTimes
                    ? doctor.availableTimes.map(time => `<option value="${time}">${time}</option>`).join("")
                    : ""
            }
        </select>

        <br><br>

        <button id="confirmBookingBtn" class="dashboard-btn">Confirm</button>
        <button id="closeBookingBtn" class="dashboard-btn">Cancel</button>
    `;

    document.body.appendChild(box);

    document.getElementById("closeBookingBtn").addEventListener("click", () => {
        box.remove();
    });

    document.getElementById("confirmBookingBtn").addEventListener("click", async () => {
        const token = localStorage.getItem("token");
        const patientId = localStorage.getItem("patientId");

        const date = document.getElementById("appointmentDate").value;
        const time = document.getElementById("appointmentTime").value;

        if (!token) {
            alert("Token missing. Please login again.");
            return;
        }

        if (!patientId) {
            alert("Patient ID missing. Please login again.");
            return;
        }

        if (!date || !time) {
            alert("Please select date and time.");
            return;
        }

        const startTime = time.includes("-") ? time.split("-")[0] : time;

        const appointment = {
            doctor: {
                id: doctor.id
            },
            patient: {
                id: Number(patientId)
            },
            appointmentTime: `${date}T${startTime}:00`,
            status: 0
        };

        const result = await bookAppointment(appointment, token);

        if (result.success) {
            alert("Appointment booked successfully");
            box.remove();
            window.location.href = "/pages/patientAppointments.html";
        } else {
            alert(result.message || "Failed to book appointment");
        }
    });
}