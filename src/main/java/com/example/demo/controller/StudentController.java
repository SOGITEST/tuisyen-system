package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Student;
import com.example.demo.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    // Ini adalah Constructor Injection (Cara Pro yang Intellij suka)
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping List<Student> getAll() {
        return studentService.dapatkanSemuaPelajar();
    }

    @GetMapping("/{id}/yuran")
    public String semakYuran(@PathVariable Long id) {
        return studentService.dapatkanStatusYuran(id);
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student, Authentication authentication) {
        // Kita kena ambil tenantId juga untuk API post ini
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return studentService.daftarPelajarBaru(student, userDetails.getTenantId());
    }

    // Method khas untuk terima data dari Borang Web (Thymeleaf)
    @PostMapping("/web")
    public String createStudentFromWeb(@ModelAttribute Student student, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer tenentId = userDetails.getTenantId();
        studentService.daftarPelajarBaru(student, tenentId);
        return "redirect:/dashboard"; // Selepas simpan, pergi balik ke dashboard
    }
}
