package com.miihe.payment.entity;

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
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long paymentId;

    private BigDecimal amount;

    private Long billId;

    private OffsetDateTime creationDate;

    private String email;

    public Payment(BigDecimal amount, Long billId,
                   OffsetDateTime creationDate, String email) {
        this.amount = amount;
        this.billId = billId;
        this.creationDate = creationDate;
        this.email = email;
    }
}
