package com.smartintern.dto.user;

import com.smartintern.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleAssignRequest {

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
}
