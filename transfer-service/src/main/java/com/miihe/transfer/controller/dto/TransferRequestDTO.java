package com.miihe.transfer.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class TransferRequestDTO {

    private Long senderAccountId;

    private Long payeeAccountId;

    private Long senderBillId;

    private Long payeeBillId;

    private BigDecimal amount;
}
