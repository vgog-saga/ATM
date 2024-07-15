package io.atm.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.atm.demo.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findById(Long id);

    public Optional<User> findByUsername(String username);

    public Optional<User> findByAuthToken(String authToken);

    public Optional<User> findBySocialSecurityNumber(String socialSecurityNumber);
}