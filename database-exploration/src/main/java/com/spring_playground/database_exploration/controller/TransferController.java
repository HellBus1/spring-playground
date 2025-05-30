package com.spring_playground.database_exploration.controller;

import com.spring_playground.database_exploration.entity.Account;
import com.spring_playground.database_exploration.entity.Transaction;
import com.spring_playground.database_exploration.enumeration.TransactionStatus;
import com.spring_playground.database_exploration.exception.InsufficientBalanceException;
import com.spring_playground.database_exploration.service.TransferService;

import lombok.RequiredArgsConstructor;

import com.spring_playground.database_exploration.model.TransferRequest;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    Logger logger = LoggerFactory.getLogger(TransferController.class);

    @PostMapping("/regular")
    public ResponseEntity<Transaction> regularTransfer(@RequestBody TransferRequest request) {
        Transaction failedTransaction = getFailedTransaction(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount());
        Transaction transaction;
        try {
            transaction = transferService.regularTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body(failedTransaction);
        } catch (Exception e) {
            logger.error("Error during transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(500).body(failedTransaction);
        }
    }

    @PostMapping("/unsafe")
    public ResponseEntity<Transaction> unsafeTransfer(@RequestBody TransferRequest request) {
        Transaction failedTransaction = getFailedTransaction(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount());
        Transaction transaction;
        try {
            transaction = transferService.unsafeTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for unsafe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body(failedTransaction);
        } catch (Exception e) {
            logger.error("Error during unsafe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(500).body(failedTransaction);
        }
    }

    @PostMapping("/deadlock-safe")
    public ResponseEntity<Transaction> deadlockSafeTransfer(@RequestBody TransferRequest request) {
        Transaction failedTransaction = getFailedTransaction(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount());
        Transaction transaction;
        try {
            transaction = transferService.deadlockSafeTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for deadlock-safe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body(failedTransaction);
        } catch (Exception e) {
            logger.error("Error during deadlock-safe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(500).body(failedTransaction);
        }
    }

    @PostMapping("/optimistic")
    public ResponseEntity<Transaction> optimisticLockTransfer(@RequestBody TransferRequest request) {
        Transaction failedTransaction = getFailedTransaction(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount());
        Transaction transaction;
        try {
            transaction = transferService.optimisticLockTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for optimistic lock transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body(failedTransaction);
        } catch (Exception e) {
            logger.error("Error during optimistic lock transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(500).body(failedTransaction);
        }
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<BigDecimal> getDirtyBalance(@PathVariable String accountNumber) {
        BigDecimal balance = transferService.getDirtyBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }

    private Transaction getFailedTransaction(String sourceAccountNumber, String destinationAccountNumber,
            BigDecimal amount) {
        Account sourceAccount = Account.builder()
                .accountNumber(sourceAccountNumber)
                .build();
        Account destinationAccount = Account.builder()
                .accountNumber(destinationAccountNumber)
                .build();
        Transaction failedTransaction = Transaction.builder()
                .sourceAccount(sourceAccount)
                .destinationAccount(destinationAccount)
                .amount(amount)
                .status(TransactionStatus.FAILED)
                .build();
        return failedTransaction;
    }
}