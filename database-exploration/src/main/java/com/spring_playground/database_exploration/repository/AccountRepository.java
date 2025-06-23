package com.spring_playground.database_exploration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spring_playground.database_exploration.entity.Account;

import jakarta.persistence.LockModeType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithPessimisticLock(@Param("accountNumber") String accountNumber);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithOptimisticLock(@Param("accountNumber") String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.balance > :threshold")
    List<Account> findByBalanceGreaterThan(@Param("threshold") BigDecimal threshold);

    @Query(value = "SELECT * FROM account WHERE account_number = :accountNumber FOR UPDATE", nativeQuery = true)
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    @Query(value = "SELECT * FROM account WHERE account_number = :accountNumber", 
           nativeQuery = true)
    Optional<Account> findByAccountNumberUnsafe(@Param("accountNumber") String accountNumber);

    @Modifying
    @Query(value = "UPDATE account SET balance = :newBalance WHERE account_number = :accountNumber", 
           nativeQuery = true)
    int updateBalanceUnsafe(@Param("accountNumber") String accountNumber, 
                           @Param("newBalance") BigDecimal newBalance);
}