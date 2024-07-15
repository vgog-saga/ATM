package io.atm.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bank")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bank_id")
    private Long id;

    private String name;

    @ManyToOne
    private User admin;

    public Bank(String name, User admin) {
        this.name = name;
        this.admin = admin;
    }

    public Bank() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBankAdmin(User user) {
        if (this.admin.getId() == user.getId()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Bank [id=" + id + ", name=" + name + "]";
    }

}