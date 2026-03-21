package com.smartintern.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "encadrants_entreprises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncadrantEntreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;

    private String nom;

    private String prenom;

    private String poste;
}
