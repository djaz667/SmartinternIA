package com.smartintern.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "competences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nom;

    private String categorie;
}
