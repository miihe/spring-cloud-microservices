package com.miihe.account.service;

import com.miihe.account.entity.Account;
import com.miihe.account.exception.AccountNotFoundException;
import com.miihe.account.repository.AccountRepository;
import com.miihe.account.rest.BillRequestDTO;
import com.miihe.account.rest.BillServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    private final BillServiceClient billServiceClient;

    @Autowired
    public AccountService(AccountRepository accountRepository, BillServiceClient billServiceClient) {
        this.accountRepository = accountRepository;
        this.billServiceClient = billServiceClient;
    }

    public Account getAccountById(Long accountId) throws AccountNotFoundException {
        return accountRepository.findById(accountId).
                orElseThrow(() -> new AccountNotFoundException("Unable to find account with id: " + accountId));
    }

    public Long createAccount(String name, String email, String phone) {
        List<Long> bills = new ArrayList<>();
        Account account = new Account(name, email, phone, bills, OffsetDateTime.now());
        BillRequestDTO billRequestDTO = new BillRequestDTO(accountRepository.save(account).getAccountId(), new BigDecimal(0),
                true, true);
        bills.add(billServiceClient.createDefaultBillFromAccountService(billRequestDTO));
        return accountRepository.save(account).getAccountId();
    }

    public Account updateAccount(Long accountId, String name, String email, String phone) {
        Account account = getAccountById(accountId);
        account.setName(name);
        account.setEmail(email);
        account.setPhone(phone);
        return accountRepository.save(account);
    }

    public Long deleteAccount(Long accountId) {
        Account accountById = getAccountById(accountId);
        List<Long> bills = accountById.getBills();
        for (Long forDelete : bills) {
            billServiceClient.deleteBillWithAccount(forDelete);
        }
        accountRepository.deleteById(accountId);
        return accountId;
    }

    public void addBillToAccountBillList(Long accountId, Long billId) {
        Account account = getAccountById(accountId);
        account.getBills().add(billId);
        accountRepository.save(account);
    }

    public void deleteBillFromAccountBillList(Long accountId, Long billId) {
        Account account = getAccountById(accountId);
        account.getBills().remove(billId);
        accountRepository.save(account);
    }
}
