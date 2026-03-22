package com.smartintern.dto.offre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffreRequest {

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "Le domaine est obligatoire")
    private String domaine;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "La duree est obligatoire")
    private String duree;

    @NotBlank(message = "Le lieu est obligatoire")
    private String lieu;

    @NotBlank(message = "Le niveau requis est obligatoire")
    private String niveauRequis;

    @NotEmpty(message = "Au moins une competence est requise")
    private List<Long> competenceIds;

    private BigDecimal remuneration;
}
