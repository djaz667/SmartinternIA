package com.smartintern.repository;

import com.smartintern.enums.Role;
import com.smartintern.enums.StatutCompte;
import com.smartintern.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByResetToken(String resetToken);

    List<User> findByStatutCompte(StatutCompte statutCompte);

    List<User> findByRole(Role role);

    List<User> findByRoleAndStatutCompte(Role role, StatutCompte statutCompte);

    long countByStatutCompte(StatutCompte statutCompte);
}
