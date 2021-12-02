package com.miihe.account.controller;

import com.miihe.account.controller.dto.AccountRequestDTO;
import com.miihe.account.controller.dto.AccountResponseDTO;
import com.miihe.account.controller.dto.BillsToAddOrDeleteDTO;
import com.miihe.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

    public final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public AccountResponseDTO getAccount(@PathVariable Long accountId) {
        return new AccountResponseDTO(accountService.getAccountById(accountId));
    }

    @PostMapping
    public Long createAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        return accountService.createAccount(accountRequestDTO.getName(), accountRequestDTO.getEmail(),
                accountRequestDTO.getPhone());
    }

    @PutMapping("/{accountId}")
    public AccountResponseDTO updateAccount(@PathVariable Long accountId, @RequestBody AccountRequestDTO accountRequestDTO) {
        return new AccountResponseDTO(accountService.updateAccount(accountId, accountRequestDTO.getName(), accountRequestDTO.getEmail(),
                accountRequestDTO.getPhone()));
    }

    @DeleteMapping("/{accountId}")
    public Long deleteAccount(@PathVariable Long accountId) {
        return accountService.deleteAccount(accountId);
    }

    @PutMapping("/addBill/{accountId}")
    public void addBillToAccountBillList(@PathVariable Long accountId, @RequestBody BillsToAddOrDeleteDTO billsToAddOrDeleteDTO) {
        accountService.addBillToAccountBillList(accountId, billsToAddOrDeleteDTO.getBillId());
    }

    @DeleteMapping("/deleteBill/{accountId}")
    public void deleteBillFromAccountBillList(@PathVariable Long accountId, @RequestBody BillsToAddOrDeleteDTO billsToAddOrDeleteDTO) {
        accountService.deleteBillFromAccountBillList(accountId, billsToAddOrDeleteDTO.getBillId());
    }
}
