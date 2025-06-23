package com.spring_playground.database_exploration.controller;

import com.spring_playground.database_exploration.entity.Account;
import com.spring_playground.database_exploration.model.CreateAccountRequest;
import com.spring_playground.database_exploration.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request.getAccountNumber(), request.getCustomerName(), request.getInitialBalance());
        return ResponseEntity.ok(account);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Account>> createBatchAccounts(@RequestBody List<CreateAccountRequest> requests) {
        List<Account> accounts = accountService.createBatchAccounts(requests);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        Account account = accountService.getAccount(accountNumber);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/{accountNumber}/balance")
    public ResponseEntity<Account> updateBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {
        Account account = accountService.updateBalance(accountNumber, amount);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
}
