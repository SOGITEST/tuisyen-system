package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity // Beritahu Spring ini adalah jadual Database
@Table(name = "students") // Nama jadual dalam PostgreSQL
@Data // Automatik buat Getter, Setter, toString
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = Long.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")

public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String parentPhone;
    private String status;
    
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "bilangan_subjek")
    private int bilanganSubjek;

    @ManyToOne // Ramai pelajar boleh kongsi satu pakej yang sama
    @JoinColumn(name = "package_id")
    private FeePackage feePackage;

    public double getJumlahYuran() {
        if (this.feePackage != null){
            return this.bilanganSubjek * this.feePackage.getPricePerSubject();
        }
       return 0.0;
    }
}
