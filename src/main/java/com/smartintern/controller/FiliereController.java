package com.smartintern.controller;

import com.smartintern.model.Filiere;
import com.smartintern.service.FiliereService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/filieres")
@RequiredArgsConstructor
public class FiliereController {

    private final FiliereService filiereService;

    @GetMapping
    public ResponseEntity<List<Filiere>> getAllFilieres() {
        return ResponseEntity.ok(filiereService.getAllFilieres());
    }
}
