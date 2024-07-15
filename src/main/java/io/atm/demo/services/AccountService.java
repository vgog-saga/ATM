package io.atm.demo.services;

import java.util.List;
import java.util.Optional;

import io.atm.demo.dao.AccountRepository;
import io.atm.demo.dao.AtmRepository;
import io.atm.demo.dao.TransactionRepository;
import io.atm.demo.entities.Account;
import io.atm.demo.entities.Atm;
import io.atm.demo.entities.Bank;
import io.atm.demo.entities.Transaction;
import io.atm.demo.entities.User;
import io.atm.demo.exceptions.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AtmRepository atmRepository;

    @Autowired
    private AtmService atmService;

    // Method to get all transactions for a given account
    public List<Transaction> getAllTransactionsForAccount(Account account) {
        return transactionRepository.findByAccountOrderByTransactionDateAsc(account);
    }

    // get account by id
    public Account getAccountById(Long id) {
        Optional<Account> accountOptional = this.accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            return account;
        } else {
            throw new CustomException("Invalid accountId");
        }
    }

    // creating new account
    public Account createAccount(Account account) {
        Account result = accountRepository.save(account);
        return result;
    }

    public Account createAccount(Bank bank, User user) {
        List<Account> accounts = accountRepository.findByUser(user);
        if(accounts.isEmpty()) {
            Account account = new Account(bank, user);
            Account result = accountRepository.save(account);
            return result;
        } else {
            throw new CustomException("Already had an account");
        }
    }

    public Account getAccountFromUser(User user) {
        List<Account> accounts = accountRepository.findByUser(user);
        if (accounts.isEmpty()) {
            throw new CustomException("No Accounts Found");
        } else {
            return accounts.get(0);
        }
    }

    // update atm pin
    public void updateAtmPin(Account account, String pin) {
        boolean result = account.setAtmPin(pin);
        if (result) {
            accountRepository.save(account);
        } else {
            throw new CustomException("Atm pin should be of 4 digits.");
        }
    }

    // verify transaction
    public boolean verifyTransaction(Transaction transaction, Long accountId) {
        // Retrieve the account by its ID
        Optional<Account> accountOptional = this.accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            double amount = transaction.getAmount();
            if (amount < 0 && account.getBalance() >= -amount) {
                return false;
            } else {
                return true;
            }
        } else {
            throw new CustomException("Account not found");
        }
    }

    // apply transaction
    public void applyTransaction(Transaction transaction) {
        Account account = transaction.getAccount();
        boolean result = account.applyTransaction(transaction);
        if (result) {
            accountRepository.save(account);
            transaction.updateStatus(2);
            Atm atm = transaction.getSourceMachine();
            atm.addBalance(transaction.getAmount());
            this.atmRepository.save(atm);
            this.transactionRepository.save(transaction);
        } else {
            transaction.updateStatus(-1);
            this.transactionRepository.save(transaction);
            throw new CustomException("Insufficient Balance.");
        }
    }

    // revert transaction
    public void revertTransaction(Transaction transaction) {
        Account account = transaction.getAccount();
        account.revertTransaction(transaction);
        accountRepository.save(account);
        transaction.updateStatus(-2);
        this.transactionRepository.save(transaction);
    }

    public void cancelTransaction(Transaction transaction) {
        transaction.updateStatus(-1);
        Atm atm = transaction.getSourceMachine();
        atmService.updateWorkingStatus(atm, false);
        this.transactionRepository.save(transaction);
    }

    public Account getAccountByAccountNumber(String AccountNumber) {
        Optional<Account> accountOptional = this.accountRepository.findByAccountNumber(AccountNumber);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            return account;
        } else {
            throw new CustomException("Invalid AccountNumber");
        }
    }

    public Account getAccountByAtmNumber(String atmNumber) {
        Optional<Account> accountOptional = this.accountRepository.findByAtmNumber(atmNumber);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            return account;
        } else {
            throw new CustomException("Invalid AccountNumber");
        }
    }

    // get pending transactions
    // public

}
