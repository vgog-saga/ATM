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
import io.atm.demo.entities.User;
import io.atm.demo.exceptions.CustomException;
import io.atm.demo.services.AccountService;
import io.atm.demo.services.UserService;

@RestController
public class userController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public ResponseEntity<?> Login(@RequestParam(required = false) String username,
            @RequestParam(required = false) String password, @RequestParam(required = false) String atmNumber,
            @RequestParam(required = false) String atmPin) {
        try {
            if (username != null && password != null) {
                String authToken = userService.login(username, password);
                // Create a response with the token
                Map<String, String> response = new HashMap<>();
                response.put("authToken", authToken);

                // Return the response with the token
                return ResponseEntity.ok(response);
            } else if (atmNumber != null && atmPin != null) {
                Account account = accountService.getAccountByAtmNumber(atmNumber);
                account = userService.loginUsingAtm(account, atmPin);
                // Create a response with the token
                Map<String, Account> response = new HashMap<>();
                response.put("account", account);

                // Return the response with the token
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Invalid login request");
            }
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/createManager")
    public ResponseEntity<?> createManager(@RequestParam String username, @RequestParam String password,
            @RequestParam String key) {
        try {
            User user = new User(username, password, "manager", "BankId");
            User responseUser = userService.createUser(user, key);
            Map<String, User> response = new HashMap<>();
            response.put("user", responseUser);
            // Return the response with the token
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/createUser")
    public ResponseEntity<?> Register(@RequestParam String username, @RequestParam String password,
            @RequestParam String socialSecurityNumber) {
        try {
            User user = new User(username, password, "user", socialSecurityNumber);
            User responseUser = userService.createUser(user);
            Map<String, User> response = new HashMap<>();
            response.put("user", responseUser);
            // Return the response with the token
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> Logout(@RequestParam String authToken) {
        try {
            userService.logout(authToken);
            Map<String, String> response = new HashMap<>();
            response.put("status", "ok");
            return ResponseEntity.ok(response);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
