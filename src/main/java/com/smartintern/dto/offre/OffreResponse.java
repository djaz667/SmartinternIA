package com.smartintern.dto.offre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffreResponse {

    private Long id;
    private String titre;
    private String domaine;
    private String description;
    private String duree;
    private String lieu;
    private String niveauRequis;
    private BigDecimal remuneration;
    private boolean active;
    private LocalDateTime datePublication;
    private String entrepriseNom;
    private Long entrepriseId;
    private List<String> competences;
}
