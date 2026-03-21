package com.smartintern.repository;

import com.smartintern.model.EncadrantAcademique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncadrantAcademiqueRepository extends JpaRepository<EncadrantAcademique, Long> {

    Optional<EncadrantAcademique> findByUserId(Long userId);
}
