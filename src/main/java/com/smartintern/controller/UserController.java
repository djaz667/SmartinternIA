package com.smartintern.controller;

import com.smartintern.dto.user.CreateEncadrantRequest;
import com.smartintern.dto.user.RoleAssignRequest;
import com.smartintern.dto.user.StatutUpdateRequest;
import com.smartintern.dto.user.UserResponse;
import com.smartintern.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(@PathVariable Long id,
                                                   @Valid @RequestBody RoleAssignRequest request) {
        UserResponse response = userService.assignRole(id, request.getRole());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateStatut(@PathVariable Long id,
                                                     @Valid @RequestBody StatutUpdateRequest request) {
        UserResponse response = userService.updateStatut(id, request.getStatut());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/encadrant-academique")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createEncadrantAcademique(@Valid @RequestBody CreateEncadrantRequest request) {
        UserResponse response = userService.createEncadrantAcademique(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
