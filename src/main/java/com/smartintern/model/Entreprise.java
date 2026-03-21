package com.smartintern.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entreprises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String nom;

    private String secteur;

    private String adresse;

    private String telephone;

    @Column(columnDefinition = "TEXT")
    private String description;
}
