package com.miihe.deposit.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class DepositRequestDTO {

    private Long accountId;

    private Long billId;

    private BigDecimal amount;
}
