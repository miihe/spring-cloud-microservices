package com.miihe.account.service;

import com.miihe.account.entity.Account;
import com.miihe.account.exception.AccountNotFoundException;
import com.miihe.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId).
                orElseThrow(() -> new AccountNotFoundException("Unable to find account with id: " + accountId));
    }

    public Long createAccount(String name, String email, String phone, List<Long> bills) {
        Account account = new Account(name, email, phone, bills, OffsetDateTime.now());
        return accountRepository.save(account).getAccountId();
    }

    public Account updateAccount(Long accountId, String name, String email,
                                 String phone, List<Long> bills) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setBills(bills);
        account.setEmail(email);
        account.setPhone(phone);
        account.setName(name);
        return accountRepository.save(account);
    }

    public Account deleteAccount(Long accountId) {
        Account deletedAccount = getAccountById(accountId);
        accountRepository.deleteById(accountId);
        return deletedAccount;

    }
}
