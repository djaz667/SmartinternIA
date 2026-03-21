package com.smartintern.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "encadrants_academiques")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncadrantAcademique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String nom;

    private String prenom;

    private String departement;

    private String specialite;
}
