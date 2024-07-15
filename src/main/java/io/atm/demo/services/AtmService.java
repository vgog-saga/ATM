package io.atm.demo.services;

import io.atm.demo.dao.AtmRepository;
import io.atm.demo.entities.Atm;
import io.atm.demo.entities.Bank;
import io.atm.demo.entities.User;
import io.atm.demo.exceptions.CustomException;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtmService {

    @Autowired
    private AtmRepository atmRepository;

    // get transaction by id
    public Atm getAtmById(Long id) {
        Optional<Atm> atmOptional = this.atmRepository.findById(id);
        if (atmOptional.isPresent()) {
            Atm atm = atmOptional.get();
            return atm;
        } else {
            throw new CustomException("Invalid AtmId");
        }
    }

    public Atm getAtmByMachineKey(String machineKey) {
        Optional<Atm> atmOptional = this.atmRepository.findByMachineKey(machineKey);
        if (atmOptional.isPresent()) {
            Atm atm = atmOptional.get();
            return atm;
        } else {
            throw new CustomException("Invalid Machine Key");
        }
    }

    // creating new bank
    public Atm createAtm(Atm atm, User user, Bank bank) {
        if (bank.isBankAdmin(user)) {
            Atm result = atmRepository.save(atm);
            return result;
        } else {
            throw new CustomException("Didn't have permission to create atm");
        }
    }

    public void addBalance(Atm atm, Double amount, User user) {
        Bank bank = atm.getBank();
        if (bank.isBankAdmin(user)) {
            atm.addBalance(amount);
            this.atmRepository.save(atm);
        } else {
            throw new CustomException("Didn't have permission to perform this action");
        }
    }

    public void updateWorkingStatus(Atm atm, boolean isWorking) {
        atm.setIsWorking(isWorking);
        this.atmRepository.save(atm);
    }

    public List<Atm> findAtmsByBankManager(User user) {
        return atmRepository.findAtmsByBankAdmin(user);
    }

}
