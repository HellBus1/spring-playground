package com.spring_playground.database_exploration.controller;

import com.spring_playground.database_exploration.entity.Transaction;
import com.spring_playground.database_exploration.service.TransferService;
import com.spring_playground.database_exploration.model.TransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {
    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest request) {
        Transaction transaction = transferService.transfer(
            request.getSourceAccountNumber(),
            request.getDestinationAccountNumber(),
            request.getAmount()
        );
        return ResponseEntity.ok(transaction);
    }
}