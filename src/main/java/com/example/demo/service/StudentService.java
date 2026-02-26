package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> dapatkanSemuaPelajar() {
        return studentRepository.findAll();
    }

    public Student daftarPelajarBaru(Student student, Integer tenantId) {
        student.setTenantId(tenantId);
        // LOGIK BISNES:
        // Contoh: Secara automatik letak status 'PENDING' jika telefon ibu bapa kosong
        if (student.getParentPhone() == null || student.getParentPhone().isEmpty()) {
            student.setStatus("PENDING_INFO");
        } else {
            student.setStatus("ACTIVE");
        }

        return studentRepository.save(student);
    }

    public String dapatkanStatusYuran(Long id) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Pelajar tidak dijumpai"));
        double total = s.getJumlahYuran();
        return "Pelajar: " + s.getFullName() + " perlu bayar: RM" + total;
    }

    public void padamPelajar(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> dapatkanPelajarIkutTenant(Integer tenantId){
        return studentRepository.findByTenantId(tenantId);
    }
}
