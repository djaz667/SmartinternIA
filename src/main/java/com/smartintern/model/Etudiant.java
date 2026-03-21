package com.smartintern.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "etudiants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String nom;

    private String prenom;

    @ManyToOne
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    @Column(name = "niveau_academique")
    private String niveauAcademique;

    @Column(name = "cv_path")
    private String cvPath;

    @Column(columnDefinition = "TEXT")
    private String bio;
}
