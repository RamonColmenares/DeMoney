package com.digitalmoney.msusers.persistency.repository;

import com.digitalmoney.msusers.persistency.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByDni(String dni);

    Optional<User> findById(Long id);
    User findByHash(String hash);
}
