package com.smartintern.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEtudiantRequest {

    private String nom;
    private String prenom;
    private Long filiereId;
    private String niveauAcademique;
    private String bio;
}
