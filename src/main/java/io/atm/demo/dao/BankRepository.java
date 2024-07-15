package io.atm.demo.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.atm.demo.entities.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {
    public Optional<Bank> findById(Long id);
}