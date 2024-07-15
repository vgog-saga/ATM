package io.atm.demo.services;

import io.atm.demo.dao.BankRepository;
// import io.atm.demo.dao.UserRepository;
import io.atm.demo.entities.Bank;
import io.atm.demo.entities.User;
import io.atm.demo.exceptions.CustomException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    // get transaction by id
    public Bank getBankById(Long id) {
        Optional<Bank> bankOptional = this.bankRepository.findById(id);
        if (bankOptional.isPresent()) {
            Bank bank = bankOptional.get();
            return bank;
        } else {
            throw new CustomException("Invalid BankId");
        }
    }

    // creating new bank
    public Bank createBank(Bank bank) {
        Bank result = bankRepository.save(bank);
        return result;
    }

    public Bank createBank(String name, User admin) {
        Bank bank = new Bank(name, admin);
        Bank result = bankRepository.save(bank);
        return result;
    }

}
