package com.spring_playground.database_exploration.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.spring_playground.database_exploration.entity.Account;
import com.spring_playground.database_exploration.repository.AccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IsolationDemoService {
    private final AccountRepository accountRepository;

    /**
     * READ UNCOMMITTED - Demonstrates dirty reads
     * This level allows a transaction to read changes that have not yet been committed by other transactions
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public BigDecimal getDirtyRead(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .map(Account::getBalance)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    /**
     * READ COMMITTED - Demonstrates non-repeatable reads
     * This level ensures that any data read is committed at the moment it is read
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public boolean demonstrateNonRepeatableRead(String accountNumber) {
        // First read
        BigDecimal initialBalance = accountRepository.findByAccountNumber(accountNumber)
            .map(Account::getBalance)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        // Simulate some processing time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Second read
        BigDecimal laterBalance = accountRepository.findByAccountNumber(accountNumber)
            .map(Account::getBalance)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        // If values are different, we've experienced a non-repeatable read
        return !initialBalance.equals(laterBalance);
    }

    /**
     * REPEATABLE READ - Demonstrates phantom reads
     * This level ensures that if a transaction reads data, it will get the same values if it reads the same data again
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<Account> demonstratePhantomRead(BigDecimal balanceThreshold) {
        // First read
        List<Account> initialAccounts = accountRepository.findByBalanceGreaterThan(balanceThreshold);
        
        // Simulate some processing time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Second read
        List<Account> laterAccounts = accountRepository.findByBalanceGreaterThan(balanceThreshold);
        
        // Log differences in result sets
        log.info("Initial count: {}, Later count: {}", 
            initialAccounts.size(), laterAccounts.size());
        
        return laterAccounts;
    }

    /**
     * SERIALIZABLE - Demonstrates complete isolation
     * This level ensures complete isolation from other transactions
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void demonstrateSerializable(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        // Simulate long-running operation
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }

    /**
     * Helper method to modify balance for testing isolation levels
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void modifyBalance(String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
}