package com.miihe.payment.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PaymentRequestDTO {

    private Long accountId;

    private Long billId;

    private BigDecimal amount;
}
