package com.smartintern.repository;

import com.smartintern.model.Offre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {

    List<Offre> findByEntrepriseId(Long entrepriseId);

    List<Offre> findByActiveTrue();
}
