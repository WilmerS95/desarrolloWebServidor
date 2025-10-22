package com.solutec.loan_application_server.repository;

import com.solutec.loan_application_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole_RoleName(String role);
}