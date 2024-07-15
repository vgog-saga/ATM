package io.atm.demo.dao;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.atm.demo.entities.Atm;
import io.atm.demo.entities.User;

public interface AtmRepository extends JpaRepository<Atm, Long> {
    public Optional<Atm> findById(Long id);

    public Optional<Atm> findByMachineKey(String machineKey);

    public List<Atm> findAtmsByBankAdmin(User user);
}