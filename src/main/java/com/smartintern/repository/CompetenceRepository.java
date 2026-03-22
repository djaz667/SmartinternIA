package com.smartintern.repository;

import com.smartintern.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {

    List<Competence> findByIdIn(List<Long> ids);
}
