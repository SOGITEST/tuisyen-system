package com.example.demo.controller;

import com.example.demo.model.CustomUserDetails;
import com.example.demo.model.Student;
import com.example.demo.repository.FeePackageRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.print.attribute.standard.ColorSupported;
import java.util.List;

@Controller
public class DashboardController {

    private final StudentService studentService;
    private final FeePackageRepository feePackageRepository; // Tambah ini
    private final StudentRepository studentRepository; // Tambah ini

    public DashboardController(StudentService studentService, FeePackageRepository feePackageRepository, StudentRepository studentRepository) {
        this.studentService = studentService;
        this.feePackageRepository = feePackageRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login"; // Merujuk kepada login.html
    }

    @GetMapping("/dashboard")
    public String tunjukDashboard(Model model, Authentication authentication) {
        // 1. Safety check: Kalau tak login, hantar ke login page
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return "redirect:/login";
        }
        // Ambil Tenant ID dari user yang tengah login
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer tid = userDetails.getTenantId();
        // 2. Pastikan tid tak null
        if (tid == null) {
            // Log ralat atau beri nilai default 0
            tid = 0;
        }

        // Hanya ambil pelajar milik cikgu ini
        List<Student> students = studentService.dapatkanPelajarIkutTenant(tid);

        // Kira total yuran menggunakan Java Stream
        double totalKutipan = students.stream().mapToDouble(Student::getJumlahYuran).sum();

        model.addAttribute("listStudents", students);
        model.addAttribute("totalPelajar", students.size());
        model.addAttribute("totalKutipan", totalKutipan);
        model.addAttribute("newStudent", new Student());
        model.addAttribute("listPackages", feePackageRepository.findAll());
        return "index";
    }

    // PINDAHKAN KE SINI
    @PostMapping("/dashboard/save")
    public String saveFromWeb(@ModelAttribute Student student, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        studentService.daftarPelajarBaru(student, userDetails.getTenantId());
        return "redirect:/dashboard"; // Sekarang Spring @Controller akan faham ini adalah arahan lompat page
    }

    @GetMapping("/dashboard/edit/{id}")
    public String editStudent(@PathVariable Long id, Model model, Authentication authentication) {
        // 1. Safety Check
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return "redirect:/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer tid = userDetails.getTenantId();

        // 2. Cari pelajar, tapi pastikan dia milik Tenant ID yang betul;
        Student s = studentRepository.findById(id).orElseThrow();

        // Check keselamatan: Kalau cikgu cuba edit pelajar orang lain
        if (!s.getTenantId().equals(tid)) {
            return "redirect:/dashboard?error=anauthorized";
        }

        // 3. Hantar data ke model (Pastikan senarai pelajar juga ditapis!)
        List<Student> students = studentService.dapatkanPelajarIkutTenant(tid);
        double totalKutipan = students.stream().mapToDouble(Student::getJumlahYuran).sum();

        model.addAttribute("newStudent", s); // Kita hantar data pelajar lama ke dalam form
        model.addAttribute("listStudents", students);
        model.addAttribute("totalPelajar", students.size());
        model.addAttribute("totalKutipan", totalKutipan);
        model.addAttribute("listPackages", feePackageRepository.findAll());
        return "index";
    }

    @GetMapping("/dashboard/delete/{id}")
    public String deleteStudent(@PathVariable Long id, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)){
            return "redirect:/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Sebelum delete, check dulu owner
        Student s = studentRepository.findById(id).orElseThrow();
        if (s.getTenantId().equals(userDetails.getTenantId())){
            studentService.padamPelajar(id);
        }

        return "redirect:/dashboard";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }
}