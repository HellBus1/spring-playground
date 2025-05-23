package com.spring_playground.database_exploration.service;

import org.springframework.transaction.annotation.Transactional;

import com.spring_playground.database_exploration.entity.Account;
import com.spring_playground.database_exploration.entity.Transaction;
import com.spring_playground.database_exploration.enumeration.TransactionStatus;
import com.spring_playground.database_exploration.enumeration.TransactionType;
import com.spring_playground.database_exploration.repository.AccountRepository;
import com.spring_playground.database_exploration.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction transfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) {
        // Get accounts with pessimistic lock
        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
            .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Validate balance
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);

        // Perform transfer
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

        // Save all changes
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        return transactionRepository.save(transaction);
    }
}
