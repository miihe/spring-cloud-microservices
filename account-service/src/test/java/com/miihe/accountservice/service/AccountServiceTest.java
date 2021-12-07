package com.miihe.accountservice.service;

import com.miihe.account.entity.Account;
import com.miihe.account.exception.AccountNotFoundException;
import com.miihe.account.repository.AccountRepository;
import com.miihe.account.rest.BillServiceClient;
import com.miihe.account.service.AccountService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {

    @Mock
    private BillServiceClient billServiceClient;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test(expected = AccountNotFoundException.class)
    public void accountServiceTest_getAccountById() {
        accountService.getAccountById(32L);
    }

    @Test
    public void accountServiceTest_createAccount() {
        Account account = createAccount();
        Long createdAccount = accountService.createAccount(account.getName(), account.getEmail(),
                account.getPhone());
        Assertions.assertThat(createdAccount).isEqualTo(1L);
    }

    @Test
    public void accountServiceTest_updateAccount() {
        Account account = createAccount();
        Mockito.when(accountRepository.findById(account.getAccountId()))
                .thenReturn(Optional.of(account));
        accountService.updateAccount(1L, "m", "as", "as");
        Assertions.assertThat(account.getName()).isEqualTo("m");
    }

    private Account createAccount() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setName("miihe");
        account.setEmail("email@mail.com");
        account.setPhone("+1234");
        account.setBills(Arrays.asList(1L, 2L, 3L));
        account.setCreationDate(OffsetDateTime.now());
        Mockito.when(accountRepository.save(ArgumentMatchers.any(Account.class)))
                .thenReturn(account);
        return account;
    }
}
