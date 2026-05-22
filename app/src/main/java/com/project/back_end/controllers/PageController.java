package com.project.back_end.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin/adminDashboard";
    }

    @GetMapping("/doctor-dashboard")
    public String doctorDashboard() {
        return "doctor/doctorDashboard";
    }

    @GetMapping("/patient-dashboard")
    public String patientDashboard() {
        return "patient/patientDashboard";
    }
}