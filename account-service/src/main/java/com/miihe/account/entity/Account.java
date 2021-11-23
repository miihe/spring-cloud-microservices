package com.miihe.account.entity;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long accountId;

    private String name;

    private String email;

    private String phone;

    @ElementCollection
    private List<Long> bills;

    private OffsetDateTime creationDate;


    public Account(String name, String email, String phone, List<Long> bills, OffsetDateTime creationDate) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bills = bills;
        this.creationDate = creationDate;
    }
}
