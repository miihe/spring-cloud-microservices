package com.miihe.bill.service;

import com.miihe.bill.entity.Bill;
import com.miihe.bill.exception.AccountIdNotFoundException;
import com.miihe.bill.exception.BillNotFoundException;
import com.miihe.bill.repository.BillRepository;
import com.miihe.bill.rest.AccountServiceClient;
import com.miihe.bill.rest.BillsToAddOrDeleteDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;

    private final AccountServiceClient accountServiceClient;

    @Autowired
    public BillService(BillRepository billRepository, AccountServiceClient accountServiceClient) {
        this.billRepository = billRepository;
        this.accountServiceClient = accountServiceClient;
    }

    public Bill getBillById(Long billId) {
        return billRepository.findById(billId).orElseThrow
                (() -> new BillNotFoundException("Unable to find bill with id: " + billId));
    }

    public Long createDefaultBillFromAccountService(Long accountId, BigDecimal amount, Boolean isDefault, Boolean overdraftEnabled) {
        Bill bill = new Bill(accountId, amount, isDefault, OffsetDateTime.now(), overdraftEnabled);
        return billRepository.save(bill).getBillId();
    }

    @Transactional
    public Long createBill(Long accountId, BigDecimal amount, Boolean isDefault, Boolean overdraftEnabled) {
        try{
            accountServiceClient.getAccount(accountId);
        } catch (FeignException e) {
            throw new AccountIdNotFoundException("Account with id: " + accountId + " - is not found. Bill was not created.");
        }

        Bill bill = new Bill(accountId, amount, isDefault, OffsetDateTime.now(), overdraftEnabled);
        accountServiceClient.addBillToAccountBillList(accountId, new BillsToAddOrDeleteDTO(billRepository.save(bill).getBillId()));
        return bill.getBillId();
    }

    public Bill updateBill(Long billId, Boolean isDefault, Boolean overdraftEnabled) {
        Bill bill = getBillById(billId);
        bill.setIsDefault(isDefault);
        bill.setOverdraftEnabled(overdraftEnabled);
        return billRepository.save(bill);
    }

    @Transactional
    public Bill deleteBill(Long billId) {
        Bill deletedBill = getBillById(billId);
        accountServiceClient.deleteBillFromAccountBillList(deletedBill.getAccountId(), new BillsToAddOrDeleteDTO(billId));
        billRepository.deleteById(billId);
        return deletedBill;
    }

    public Bill deleteBillWithAccount(Long billId) {
        Bill deletedBill = getBillById(billId);
        billRepository.deleteById(billId);
        return deletedBill;
    }

    public void updateAmountOfBill(Long billId, BigDecimal amount) {
        Bill bill = getBillById(billId);
        bill.setAmount(amount);
        billRepository.save(bill);
    }

    public List<Bill> getBillsByAccountId(Long accountId) {
        return billRepository.getBillsByAccountId(accountId);
    }
}
