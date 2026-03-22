package com.smartintern.controller;

import com.smartintern.model.Competence;
import com.smartintern.repository.CompetenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competences")
@RequiredArgsConstructor
public class CompetenceController {

    private final CompetenceRepository competenceRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Competence>> getAllCompetences() {
        return ResponseEntity.ok(competenceRepository.findAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Competence> createCompetence(@RequestBody Competence competence) {
        Competence saved = competenceRepository.save(competence);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
