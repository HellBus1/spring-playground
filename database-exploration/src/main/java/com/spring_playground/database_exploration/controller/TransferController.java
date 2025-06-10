package com.spring_playground.database_exploration.controller;

import com.spring_playground.database_exploration.entity.Transaction;
import com.spring_playground.database_exploration.exception.InsufficientBalanceException;
import com.spring_playground.database_exploration.service.TransferService;

import lombok.RequiredArgsConstructor;

import com.spring_playground.database_exploration.model.TransferRequest;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    Logger logger = LoggerFactory.getLogger(TransferController.class);

    @PostMapping("/regular")
    public ResponseEntity<?> regularTransfer(@RequestBody TransferRequest request) {
        try {
            Transaction transaction = transferService.regularTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error during transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed: " + e.getMessage());
        }
    }

    @PostMapping("/unsafe")
    public ResponseEntity<?> unsafeTransfer(@RequestBody TransferRequest request) {
        try {
            Transaction transaction = transferService.unsafeTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for unsafe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error during unsafe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed: " + e.getMessage());
        }
    }

    @PostMapping("/deadlock-safe")
    public ResponseEntity<?> deadlockSafeTransfer(@RequestBody TransferRequest request) {
        try {
            Transaction transaction = transferService.deadlockSafeTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for deadlock-safe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error during deadlock-safe transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed: " + e.getMessage());
        }
    }

    @PostMapping("/optimistic")
    public ResponseEntity<?> optimisticLockTransfer(@RequestBody TransferRequest request) {
        try {
            Transaction transaction = transferService.optimisticLockTransfer(
                    request.getSourceAccountNumber(),
                    request.getDestinationAccountNumber(),
                    request.getAmount());
            return ResponseEntity.ok(transaction);
        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance for optimistic lock transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.badRequest().body("Transfer failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error during optimistic lock transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed: " + e.getMessage());
        }
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<BigDecimal> getDirtyBalance(@PathVariable String accountNumber) {
        BigDecimal balance = transferService.getDirtyBalance(accountNumber);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/demonstrate-partial-failure")
    public ResponseEntity<?> demonstratePartialFailure(
            @RequestBody TransferRequest request,
            @RequestParam(defaultValue = "false") boolean simulateError) {
        try {
            Transaction transaction = transferService.demonstratePartialFailure(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount(),
                simulateError);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            logger.error("Error during transfer from {} to {}: {}", 
                         request.getSourceAccountNumber(), 
                         request.getDestinationAccountNumber(), 
                         e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed: " + e.getMessage());
        }
    }
}