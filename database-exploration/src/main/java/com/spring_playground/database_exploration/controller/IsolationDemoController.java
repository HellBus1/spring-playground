package com.spring_playground.database_exploration.controller;

import com.spring_playground.database_exploration.entity.Account;
import com.spring_playground.database_exploration.service.IsolationDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/isolation")
@RequiredArgsConstructor
public class IsolationDemoController {
    private final IsolationDemoService isolationDemoService;

    @GetMapping("/dirty-read/{accountNumber}")
    public ResponseEntity<BigDecimal> getDirtyRead(@PathVariable String accountNumber) {
        return ResponseEntity.ok(isolationDemoService.getDirtyRead(accountNumber));
    }

    @GetMapping("/non-repeatable-read/{accountNumber}")
    public ResponseEntity<Boolean> checkNonRepeatableRead(@PathVariable String accountNumber) {
        return ResponseEntity.ok(isolationDemoService.demonstrateNonRepeatableRead(accountNumber));
    }

    @GetMapping("/phantom-read")
    public ResponseEntity<List<Account>> checkPhantomRead(
            @RequestParam(defaultValue = "1000") BigDecimal threshold) {
        return ResponseEntity.ok(isolationDemoService.demonstratePhantomRead(threshold));
    }

    @PostMapping("/serializable/{accountNumber}")
    public ResponseEntity<Void> testSerializable(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {
        isolationDemoService.demonstrateSerializable(accountNumber, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/modify-balance/{accountNumber}")
    public ResponseEntity<Void> modifyBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount) {
        isolationDemoService.modifyBalance(accountNumber, amount);
        return ResponseEntity.ok().build();
    }
}
