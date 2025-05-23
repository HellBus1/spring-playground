package com.spring_playground.database_exploration.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.spring_playground.database_exploration.enumeration.TransactionStatus;
import com.spring_playground.database_exploration.enumeration.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "transaction_reference", unique = true, nullable = false)
    private String transactionReference;
    
    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;
    
    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "transaction_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}