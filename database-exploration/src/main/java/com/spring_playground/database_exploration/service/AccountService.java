package com.spring_playground.database_exploration.service;

import com.spring_playground.database_exploration.entity.Account;
import com.spring_playground.database_exploration.model.CreateAccountRequest;
import com.spring_playground.database_exploration.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(String accountNumber, String customerName, BigDecimal initialBalance) {
        // Validate account number doesn't exist
        accountRepository.findByAccountNumber(accountNumber)
            .ifPresent(account -> {
                throw new RuntimeException("Account number already exists: " + accountNumber);
            });

        Account account = Account.builder()
            .accountNumber(accountNumber)
            .customerName(customerName)
            .balance(initialBalance)
            .build();

        return accountRepository.save(account);
    }

    @Transactional
    public List<Account> createBatchAccounts(List<CreateAccountRequest> requests) {
        return requests.stream()
            .map(request -> createAccount(request.getAccountNumber(), request.getCustomerName(), request.getInitialBalance()))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    @Transactional
    public Account updateBalance(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public boolean hasInsufficientBalance(String accountNumber, BigDecimal amount) {
        Account account = getAccount(accountNumber);
        return account.getBalance().compareTo(amount) < 0;
    }
}
