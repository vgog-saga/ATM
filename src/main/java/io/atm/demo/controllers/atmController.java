package io.atm.demo.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.atm.demo.entities.Bank;
import io.atm.demo.entities.Atm;
import io.atm.demo.entities.User;
import io.atm.demo.exceptions.CustomException;
import io.atm.demo.services.AtmService;
import io.atm.demo.services.BankService;
import io.atm.demo.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class atmController {
    
    @Autowired
    AtmService atmService;

    @Autowired
    BankService bankService;

    @Autowired
    UserService userService;

    @GetMapping("/createAtm")
    public ResponseEntity<?> createAtm(@RequestParam Long bankId, @RequestParam String atmName,
            @RequestParam String authToken) {
        try {
            Bank bank = bankService.getBankById(bankId);
            User user = userService.getUserByAuthToken(authToken);
            if (user.isManager()) {
                Atm atm = new Atm(atmName, bank);
                Atm responseAtm = atmService.createAtm(atm, user, bank);
                Map<String, Atm> response = new HashMap<>();
                response.put("atm", responseAtm);
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("No Permission");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getAtmBalance")
    public ResponseEntity<?> createAtm(@RequestParam Long atmId, @RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            if(user.isManager()) {
                Atm atm = atmService.getAtmById(atmId);
                Map<String, Double> response = new HashMap<>();
                response.put("balance", atm.getBalance());
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("No Permission");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/addFundsToAtm")
    public ResponseEntity<?> addFunds(@RequestParam Long atmId, @RequestParam Double amount,
            @RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            if(user.isManager()) {
                Atm atm = atmService.getAtmById(atmId);
                atmService.addBalance(atm, amount, user);
                Map<String, Double> response = new HashMap<>();
                response.put("balance", atm.getBalance());
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("No Permission");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getAllAtms")
    public ResponseEntity<?> addFunds(@RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            if (user.isManager()) {
                List<Atm> atms = atmService.findAtmsByBankManager(user);
                Map<String, List<Atm>> response = new HashMap<>();
                response.put("atms", atms);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Permission");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getAtmDetails")
    public ResponseEntity<?> addFunds(@RequestParam Long atmId) {
        try {
            Atm atm = atmService.getAtmById(atmId);
            Map<String, Atm> response = new HashMap<>();
            response.put("atm", atm);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/updateAtmWorkingStatus")
    public ResponseEntity<?> updateStatus(@RequestParam Long atmId, @RequestParam boolean status,
            @RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            if (user.isManager()) {
                Atm atm = atmService.getAtmById(atmId);
                atmService.updateWorkingStatus(atm, status);
                Map<String, String> response = new HashMap<>();
                response.put("status", "ok");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No Permission");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
