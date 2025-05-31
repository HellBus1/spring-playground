package com.spring_playground.database_exploration.service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.spring_playground.database_exploration.entity.Account;
import com.spring_playground.database_exploration.entity.Transaction;
import com.spring_playground.database_exploration.entity.TransactionStatusHistory;
import com.spring_playground.database_exploration.enumeration.TransactionStatus;
import com.spring_playground.database_exploration.enumeration.TransactionType;
import com.spring_playground.database_exploration.exception.InsufficientBalanceException;
import com.spring_playground.database_exploration.repository.AccountRepository;
import com.spring_playground.database_exploration.repository.TransactionRepository;
import com.spring_playground.database_exploration.repository.TransactionStatusHistoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ConcurrentModificationException;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionStatusHistoryRepository statusHistoryRepository;

    /**
     * Regular transfer with proper transaction handling
     * @throws InsufficientBalanceException 
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Transaction regularTransfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) throws InsufficientBalanceException {
        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
            .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        return executeTransfer(sourceAccount, destinationAccount, amount);
    }

    /**
     * Demonstrates race condition vulnerability
     * @throws InsufficientBalanceException 
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Transaction unsafeTransfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) throws InsufficientBalanceException {
        // Use native query to bypass optimistic locking
        Account sourceAccount = accountRepository.findByAccountNumberForUpdate(sourceAccountNumber)
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        // Simulate delay to increase race condition probability
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Account destinationAccount = accountRepository.findByAccountNumberForUpdate(destinationAccountNumber)
            .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Create transaction record
        Transaction transaction = Transaction.builder()
            .transactionReference(UUID.randomUUID().toString())
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(amount)
            .transactionType(TransactionType.TRANSFER)
            .status(TransactionStatus.PENDING)
            .build();
        
        transaction = transactionRepository.save(transaction);

        try {
            // Use native query to update balances directly
            int updatedRows = accountRepository.updateBalanceUnsafe(
                sourceAccount.getId(), 
                sourceAccount.getBalance().subtract(amount)
            );
            
            if (updatedRows == 0) {
                throw new RuntimeException("Failed to update source account balance");
            }

            updatedRows = accountRepository.updateBalanceUnsafe(
                destinationAccount.getId(), 
                destinationAccount.getBalance().add(amount)
            );
            
            if (updatedRows == 0) {
                throw new RuntimeException("Failed to update destination account balance");
            }

            transaction.setStatus(TransactionStatus.COMPLETED);
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw e;
        }
    }

    /**
     * Demonstrates deadlock prevention using ordered locking
     * @throws InsufficientBalanceException 
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Transaction deadlockSafeTransfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) throws InsufficientBalanceException {
        String firstAccountNumber = sourceAccountNumber.compareTo(destinationAccountNumber) < 0 
            ? sourceAccountNumber : destinationAccountNumber;
        String secondAccountNumber = sourceAccountNumber.compareTo(destinationAccountNumber) < 0 
            ? destinationAccountNumber : sourceAccountNumber;

        Account firstAccount = accountRepository.findByAccountNumberWithPessimisticLock(firstAccountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + firstAccountNumber));
        
        Account secondAccount = accountRepository.findByAccountNumberWithPessimisticLock(secondAccountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + secondAccountNumber));

        Account sourceAccount = sourceAccountNumber.equals(firstAccountNumber) ? firstAccount : secondAccount;
        Account destinationAccount = destinationAccountNumber.equals(firstAccountNumber) ? firstAccount : secondAccount;

        return executeTransfer(sourceAccount, destinationAccount, amount);
    }

    /**
     * Demonstrates dirty read scenario
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public BigDecimal getDirtyBalance(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .map(Account::getBalance)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    /**
     * Demonstrates lost update prevention using optimistic locking
     * @throws InsufficientBalanceException 
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Transaction optimisticLockTransfer(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount) throws InsufficientBalanceException {
        Account sourceAccount = accountRepository.findByAccountNumberWithOptimisticLock(sourceAccountNumber)
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        Account destinationAccount = accountRepository.findByAccountNumberWithOptimisticLock(destinationAccountNumber)
            .orElseThrow(() -> new RuntimeException("Destination account not found"));

        try {
            return executeTransfer(sourceAccount, destinationAccount, amount);
        } catch (OptimisticLockingFailureException e) {
            throw new ConcurrentModificationException("Transaction failed due to concurrent modification");
        }
    }

    @Transactional
    public Transaction demonstratePartialFailure(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount, boolean simulateError) {
        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber)
            .orElseThrow(() -> new RuntimeException("Source account not found"));
        
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber)
            .orElseThrow(() -> new RuntimeException("Destination account not found"));

        // Create initial transaction record
        Transaction transaction = Transaction.builder()
            .transactionReference(UUID.randomUUID().toString())
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(amount)
            .status(TransactionStatus.PENDING)
            .build();
        
        transaction = transactionRepository.save(transaction);

        try {
            // Create initial status history
            TransactionStatusHistory pendingHistory = TransactionStatusHistory.builder()
                .transaction(transaction)
                .status(TransactionStatus.PENDING)
                .notes("Transaction initiated")
                .build();
            
            statusHistoryRepository.save(pendingHistory);

            // Simulate error based on parameter
            if (simulateError) {
                throw new RuntimeException("Simulated system error during transfer");
            }

            // Process transfer
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
            destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transaction.setStatus(TransactionStatus.COMPLETED);
            transaction = transactionRepository.save(transaction);

            TransactionStatusHistory completedHistory = TransactionStatusHistory.builder()
                .transaction(transaction)
                .status(TransactionStatus.COMPLETED)
                .notes("Transfer completed successfully")
                .build();
            
            statusHistoryRepository.save(completedHistory);

            return transaction;
        } catch (Exception e) {
            log.error("Error during transfer: {}", e.getMessage());
            
            try {
                // Try to save failure status
                transaction.setStatus(TransactionStatus.FAILED);
                transaction = transactionRepository.save(transaction);

                TransactionStatusHistory failedHistory = TransactionStatusHistory.builder()
                    .transaction(transaction)
                    .status(TransactionStatus.FAILED)
                    .notes("Transfer failed: " + e.getMessage())
                    .build();
                
                statusHistoryRepository.save(failedHistory);
            } catch (Exception ex) {
                log.error("Failed to save failure status: {}", ex.getMessage());
            }
            
            throw e;
        }
    }

    private Transaction executeTransfer(Account sourceAccount, Account destinationAccount, BigDecimal amount) throws InsufficientBalanceException {
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        Transaction transaction = Transaction.builder()
            .transactionReference(UUID.randomUUID().toString())
            .sourceAccount(sourceAccount)
            .destinationAccount(destinationAccount)
            .amount(amount)
            .transactionType(TransactionType.TRANSFER)
            .status(TransactionStatus.PENDING)
            .build();
        
        transaction = transactionRepository.save(transaction);

        try {
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
            destinationAccount.setBalance(destinationAccount.getBalance().add(amount));

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transaction.setStatus(TransactionStatus.COMPLETED);
            return transactionRepository.save(transaction);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Transfer failed", e);
        }
    }
}
