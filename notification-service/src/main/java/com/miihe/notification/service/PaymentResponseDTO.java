package com.miihe.notification.service;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentResponseDTO {

    private BigDecimal amount;

    private String mail;
}
