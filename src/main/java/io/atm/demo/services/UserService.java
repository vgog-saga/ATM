package io.atm.demo.services;

import io.atm.demo.dao.UserRepository;
import io.atm.demo.entities.User;
import io.atm.demo.entities.Account;
import io.atm.demo.exceptions.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private String superAdminKey = "dummyKey";

    // get user by id
    public User getUserById(Long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user;
        } else {
            throw new CustomException("Invalid userId");
        }
    }

    // creating new user
    public User createUser(User user, String key) {
        if (this.superAdminKey.equals(key)) {
            User result = userRepository.save(user);
            return result;
        } else {
            throw new CustomException("Invalid key");
        }
    }

    public User createUser(User user) {
        if (!user.isManager()) {
            Optional<User> userOptional = this.userRepository.findBySocialSecurityNumber(user.getSocialSecurityNumber());
            if(!userOptional.isPresent()) {
                User result = userRepository.save(user);
                return result;
            } else {
                throw new CustomException("Duplicate Social Security Number");
            }
        } else {
            throw new CustomException("No Access");
        }
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = this.userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user;
        } else {
            throw new CustomException("Invalid username");
        }
    }

    public User getUserBySocialSecurityNumber(String socialSecurityNumber) {
        Optional<User> userOptional = this.userRepository.findBySocialSecurityNumber(socialSecurityNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user;
        } else {
            throw new CustomException("Invalid social security number");
        }
    }

    public User getUserByAuthToken(String authToken) {
        Optional<User> userOptional = this.userRepository.findByAuthToken(authToken);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return user;
        } else {
            throw new CustomException("Invalid authToken");
        }
    }

    public String login(String username, String password) {
        Optional<User> userOptional = this.userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.validatePassword(password)) {
                String authToken = UUID.randomUUID().toString();
                user.setAuthToken(authToken);
                this.userRepository.save(user);
                return authToken;
            } else {
                throw new CustomException("Invalid password");
            }
        } else {
            throw new CustomException("Invalid username");
        }
    }

    public Account loginUsingAtm(Account account, String atmPin) {
        User user = account.getUser();
        if (account.validateAtmPin(atmPin)) {
            String authToken = UUID.randomUUID().toString();
            user.setAuthToken(authToken);
            this.userRepository.save(user);
            return account;
        } else {
            throw new CustomException("Invalid password");
        }
    }

    public void logout(String authToken) {
        Optional<User> userOptional = this.userRepository.findByAuthToken(authToken);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.logout();
            this.userRepository.save(user);
        }
    }

}
