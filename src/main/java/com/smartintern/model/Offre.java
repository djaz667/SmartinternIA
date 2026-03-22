package com.smartintern.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offres")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String domaine;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String duree;

    @Column(nullable = false)
    private String lieu;

    @Column(name = "niveau_requis", nullable = false)
    private String niveauRequis;

    private BigDecimal remuneration;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "date_publication", nullable = false)
    private LocalDateTime datePublication;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "offre_competence",
            joinColumns = @JoinColumn(name = "offre_id"),
            inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private Set<Competence> competences = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (datePublication == null) {
            datePublication = LocalDateTime.now();
        }
    }
}
