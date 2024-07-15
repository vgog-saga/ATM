package io.atm.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.atm.demo.entities.Account;
import io.atm.demo.entities.Bank;
import io.atm.demo.entities.User;
import io.atm.demo.exceptions.CustomException;
import io.atm.demo.services.AccountService;
import io.atm.demo.services.BankService;
import io.atm.demo.services.UserService;

@RestController
public class bankController {

    @Autowired
    private BankService bankService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/createBank")
    public ResponseEntity<?> createBank(@RequestParam String bankName, @RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            if (user.isManager()) {
                Bank responseBank = bankService.createBank(bankName, user);
                Map<String, Bank> response = new HashMap<>();
                response.put("bank", responseBank);
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("No Permission");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/openAccount")
    public ResponseEntity<?> openAccount(@RequestParam Long bankId, @RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            Bank bank = bankService.getBankById(bankId);
            Account account = accountService.createAccount(bank, user);
            Map<String, Account> response = new HashMap<>();
            response.put("account", account);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
