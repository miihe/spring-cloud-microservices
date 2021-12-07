package com.miihe.depositservice.utils;

import com.miihe.deposit.rest.AccountResponseDTO;
import com.miihe.deposit.rest.BillResponseDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;

public class DepositTestUtil {

    public static AccountResponseDTO createAccountResponseDTO() {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setAccountId(1L);
        accountResponseDTO.setName("Miihe");
        accountResponseDTO.setEmail("DepositTest@mail.ru");
        accountResponseDTO.setPhone("+123123");
        accountResponseDTO.setBills(Arrays.asList(1L, 2L, 3L));
        accountResponseDTO.setCreationDate(OffsetDateTime.now());
        return accountResponseDTO;
    }

    public static BillResponseDTO createBillResponseDTO() {
        BillResponseDTO billResponseDTO = new BillResponseDTO();
        billResponseDTO.setAccountId(1L);
        billResponseDTO.setAmount(BigDecimal.valueOf(1000));
        billResponseDTO.setBillId(1L);
        billResponseDTO.setCreationDate(OffsetDateTime.now());
        billResponseDTO.setIsDefault(true);
        billResponseDTO.setOverdraftEnabled(true);
        return billResponseDTO;
    }
}
