package com.miihe.transfer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long transferId;

    private String senderName;

    private Long senderBillId;

    private BigDecimal amount;

    private String payeeName;

    private Long payeeBillId;

    private OffsetDateTime creationDate;

    public Transfer(String senderName, Long senderBillId, BigDecimal amount,
                    String payeeName, Long payeeBillId, OffsetDateTime creationDate) {
        this.senderName = senderName;
        this.senderBillId = senderBillId;
        this.amount = amount;
        this.payeeName = payeeName;
        this.payeeBillId = payeeBillId;
        this.creationDate = creationDate;
    }
}
