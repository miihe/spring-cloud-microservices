package com.miihe.transfer.controller.dto;

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

    public TransferResponseDTO(BigDecimal amount, String senderName, String payeeName) {
        this.amount = amount;
        this.senderName = senderName;
        this.payeeName = payeeName;
    }
}
