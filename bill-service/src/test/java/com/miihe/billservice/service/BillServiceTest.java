package com.miihe.billservice.service;

import com.miihe.bill.entity.Bill;
import com.miihe.bill.exception.AccountIdNotFoundException;
import com.miihe.bill.exception.BillNotFoundException;
import com.miihe.bill.repository.BillRepository;
import com.miihe.bill.rest.AccountResponseDTO;
import com.miihe.bill.rest.AccountServiceClient;
import com.miihe.bill.service.BillService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class BillServiceTest {

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private BillRepository billRepository;

    @InjectMocks
    private BillService billService;

    @Test(expected = BillNotFoundException.class)
    public void billServiceTest_getBillById() {
        billService.getBillById(12L);
    }

    @Test
    public void billServiceTest_createBill() {
        Bill bill = createBill();
        billService.createBill(bill.getAccountId(), bill.getAmount(), bill.getIsDefault(), bill.getOverdraftEnabled());
        Assertions.assertThat(bill.getBillId()).isEqualTo(1L);
        Assertions.assertThat(bill.getAccountId()).isEqualTo(1L);
        Assertions.assertThat(bill.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    public void billServiceTest_updateBill() {
        Bill bill = createBill();
        Mockito.when(billRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(bill));
        billService.updateBill(bill.getBillId(), false, false);
        Assertions.assertThat(bill.getIsDefault()).isEqualTo(false);
    }

    private Bill createBill() {
        Bill bill = new Bill();
        bill.setBillId(1L);
        bill.setAccountId(1L);
        bill.setAmount(BigDecimal.valueOf(1000));
        bill.setIsDefault(true);
        bill.setCreationDate(OffsetDateTime.now());
        bill.setOverdraftEnabled(true);
        Mockito.when(billRepository.save(ArgumentMatchers.any()))
                .thenReturn(bill);
        return bill;
    }
}
