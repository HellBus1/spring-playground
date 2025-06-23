package com.spring_playground.database_exploration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring_playground.database_exploration.entity.TransactionStatusHistory;

import java.util.List;

@Repository
public interface TransactionStatusHistoryRepository extends JpaRepository<TransactionStatusHistory, Long> {
    List<TransactionStatusHistory> findByTransactionIdOrderByCreatedAtDesc(Long transactionId);
}
