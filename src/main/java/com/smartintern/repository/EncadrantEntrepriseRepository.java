package com.smartintern.repository;

import com.smartintern.model.EncadrantEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EncadrantEntrepriseRepository extends JpaRepository<EncadrantEntreprise, Long> {

    Optional<EncadrantEntreprise> findByUserId(Long userId);

    List<EncadrantEntreprise> findByEntrepriseId(Long entrepriseId);
}
