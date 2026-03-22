package com.smartintern.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String filiere;
    private Long filiereId;
    private String niveauAcademique;
    private String cvPath;
    private String bio;
    private String email;
}
