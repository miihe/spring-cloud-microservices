package com.miihe.notification.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDTO {

    private BigDecimal amount;

    private String senderName;

    private String payeeName;

    private String senderEmail;

    private String payeeEmail;
}
