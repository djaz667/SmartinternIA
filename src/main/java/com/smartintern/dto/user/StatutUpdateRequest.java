package com.smartintern.dto.user;

import com.smartintern.enums.StatutCompte;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatutUpdateRequest {

    @NotNull(message = "Le statut est obligatoire")
    private StatutCompte statut;
}
