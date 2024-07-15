package io.atm.demo.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.atm.demo.entities.Account;
import io.atm.demo.entities.Atm;
import io.atm.demo.entities.Transaction;
import io.atm.demo.entities.User;
import io.atm.demo.exceptions.CustomException;
import io.atm.demo.services.AccountService;
import io.atm.demo.services.AtmService;
import io.atm.demo.services.TransactionService;
import io.atm.demo.services.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class accountController {

    @Autowired
    AccountService accountService;

    @Autowired
    UserService userService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    AtmService atmService;

    @GetMapping("/getAccountDetails")
    public ResponseEntity<?> getAccountDetails(@RequestParam String atmNumber, @RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            Account account = accountService.getAccountByAtmNumber(atmNumber);
            if (account.getUser() == user) {
                Map<String, Account> response = new HashMap<>();
                response.put("account", account);
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("No Access to account");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getAccountDetailsFromUser")
    public ResponseEntity<?> getAccountDetailsFromUser(@RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            Account account = accountService.getAccountFromUser(user);
            Map<String, Account> response = new HashMap<>();
            response.put("account", account);
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/resetAtmPin")
    public ResponseEntity<?> resetAtmPin(@RequestParam String accountNo, @RequestParam String authToken,
            @RequestParam String newPin) {
        try {
            Account account = accountService.getAccountByAccountNumber(accountNo);
            User user = userService.getUserByAuthToken(authToken);
            if (account.getUser() == user) {
                accountService.updateAtmPin(account, newPin);
                Map<String, String> response = new HashMap<>();
                response.put("status", "ok");
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("No Access to account");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/changeAtmPin")
    public ResponseEntity<?> changeAtmPin(@RequestParam String accountNo, @RequestParam String newPassword,
            @RequestParam String authToken) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            Account account = accountService.getAccountByAccountNumber(accountNo);
            if (account.hasAccess(user)) {
                accountService.updateAtmPin(account, newPassword);
                Map<String, String> response = new HashMap<>();
                response.put("status", "ok");
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("No Access to account");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/getBalance")
    public ResponseEntity<?> getBalance(@RequestParam String accountNo, @RequestParam String authToken,
            @RequestParam String pin) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            Account account = accountService.getAccountByAccountNumber(accountNo);
            if (account.getUser() == user) {
                if (account.validateAtmPin(pin)) {
                    Map<String, Double> response = new HashMap<>();
                    response.put("Balance", account.getBalance());
                    return ResponseEntity.ok(response);
                } else {
                    throw new CustomException("Invalid Pin");
                }
            } else {
                throw new CustomException("No Access to account");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/withDraw")
    public ResponseEntity<?> withDrawMoney(@RequestParam String accountNo, @RequestParam Double amount,
            @RequestParam String pin, @RequestParam String authToken, @RequestParam String machineKey) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            Atm sourceMachine = atmService.getAtmByMachineKey(machineKey);
            Account account = accountService.getAccountByAccountNumber(accountNo);
            if (account.hasAccess(user)) {
                if (account.validateAtmPin(pin)) {
                    if (account.getBalance() >= amount) {
                        if (sourceMachine.getBalance() >= amount) {
                            Transaction transaction = new Transaction(account, -amount, "withDraw", sourceMachine);
                            transactionService.createTransaction(transaction);
                            Map<String, Transaction> response = new HashMap<>();
                            response.put("transaction", transaction);
                            return ResponseEntity.ok(response);
                        } else {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds in Machine");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Balance");
                    }
                } else {
                    throw new CustomException("Invalid ATM pin");
                }
            } else {
                throw new CustomException("No Access to account");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/depositMoney")
    public ResponseEntity<?> depositMoney(@RequestParam String accountNo, @RequestParam Double amount,
            @RequestParam String pin, @RequestParam String authToken, @RequestParam String machineKey) {
        try {
            User user = userService.getUserByAuthToken(authToken);
            Atm sourceMachine = atmService.getAtmByMachineKey(machineKey);
            Account account = accountService.getAccountByAccountNumber(accountNo);
            if (account.hasAccess(user)) {
                if (account.validateAtmPin(pin)) {
                    Transaction transaction = new Transaction(account, amount, "withDraw", sourceMachine);
                    transactionService.createTransaction(transaction);
                    Map<String, Transaction> response = new HashMap<>();
                    response.put("transaction", transaction);
                    return ResponseEntity.ok(response);
                } else {
                    throw new CustomException("Invalid ATM pin");
                }
            } else {
                throw new CustomException("No Access to account");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/updateTransactionStatus")
    public ResponseEntity<?> updateTransactionStatus(@RequestParam int status, @RequestParam Long transactionId,
            @RequestParam String machineKey) {
        try {
            Atm atm = atmService.getAtmByMachineKey(machineKey);
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (transaction.getSourceMachine() == atm) {
                transactionService.updateStatus(transaction, status);
                if(status == 2) {
                    accountService.applyTransaction(transaction);
                }
                Map<String, Transaction> response = new HashMap<>();
                response.put("transaction", transaction);
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("Invalid request");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/applyTransaction")
    public ResponseEntity<?> applyTransaction(@RequestParam Long transactionId, @RequestParam String machineKey) {
        try {
            Atm atm = atmService.getAtmByMachineKey(machineKey);
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (transaction.getSourceMachine() == atm) {
                accountService.applyTransaction(transaction);
                Map<String, Transaction> response = new HashMap<>();
                response.put("transaction", transaction);
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("Invalid request");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/cancelTransaction")
    public ResponseEntity<?> cancelTransaction(@RequestParam Long transactionId, @RequestParam String machineKey) {
        try {
            Atm atm = atmService.getAtmByMachineKey(machineKey);
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (transaction.getSourceMachine() == atm) {
                accountService.cancelTransaction(transaction);
                Map<String, Transaction> response = new HashMap<>();
                response.put("transaction", transaction);
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("Invalid request");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/revertTransaction")
    public ResponseEntity<?> revertTransactio (@RequestParam Long transactionId, @RequestParam String machineKey) {
        try {
            Atm atm = atmService.getAtmByMachineKey(machineKey);
            Transaction transaction = transactionService.getTransactionById(transactionId);
            if (transaction.getSourceMachine() == atm) {
                accountService.revertTransaction(transaction);
                Map<String, Transaction> response = new HashMap<>();
                response.put("transaction", transaction);
                return ResponseEntity.ok(response);
            } else {
                throw new CustomException("Invalid request");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

}
