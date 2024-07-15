package io.atm.demo.services;

import io.atm.demo.dao.TransactionRepository;
import io.atm.demo.entities.Transaction;
import io.atm.demo.exceptions.CustomException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    // get transaction by id
    public Transaction getTransactionById(Long id) {
        Optional<Transaction> transactionOptional = this.transactionRepository.findById(id);
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            return transaction;
        } else {
            throw new CustomException("Invalid transactionId");
        }
    }

    // creating new transction
    public Transaction createTransaction(Transaction transaction) {
        Transaction result = transactionRepository.save(transaction);
        return result;
    }

    public void updateStatus(Transaction transaction, int status) {
        transaction.updateStatus(status);
        transactionRepository.save(transaction);
    }

    @Scheduled(fixedRate = 120000) // 2 minutes in milliseconds
    public void checkAndCancelPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findPendingTransactions();
        System.out.println(pendingTransactions);
        LocalDateTime twoMinutesAgo = LocalDateTime.now().minusMinutes(2);
        for (Transaction transaction : pendingTransactions) {
            if (transaction.getUpdatedAt() == null || transaction.getUpdatedAt().isBefore(twoMinutesAgo)) {
                // Cancel the transaction (update its status or perform the cancellation logic)
                transaction.updateStatus(-1);
                transactionRepository.save(transaction);
            }
        }
    }

}
