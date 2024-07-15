package io.atm.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    private String username;
    private String password;
    private String userType;
    private String socialSecurityNumber;

    private String authToken;

    public User(String username, String password, String userType, String socialSecurityNumber) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public void logout() {
        this.authToken = null;
    }

    public boolean validatePassword(String password) {
        if (this.password == null && password == null) {
            return true;
        } else if (this.password == null || password == null) {
            return false;
        }
        return this.password.equals(password);
    }

    public boolean isManager() {
        return this.userType.equalsIgnoreCase("manager");
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", userType=" + userType + "]";
    }

}