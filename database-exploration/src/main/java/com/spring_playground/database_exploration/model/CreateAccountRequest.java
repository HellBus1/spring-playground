package com.spring_playground.database_exploration.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    private String accountNumber;
    private String customerName;
    private BigDecimal initialBalance;
}