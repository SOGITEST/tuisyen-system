package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "fee_packages")
@Data
public class FeePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packageName; // Contoh: "Standard", "Premium"
    private double pricePerSubject; // Contoh: 50;
}
