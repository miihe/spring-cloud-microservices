package com.miihe.bill.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @RequestMapping(value = "/accounts/{accountId}", method = RequestMethod.GET)
    AccountResponseDTO getAccount(@PathVariable Long accountId);

    @RequestMapping(value = "/accounts/addBill/{accountId}", method = RequestMethod.PUT)
    void addBillToAccountBillList(@PathVariable Long accountId, @RequestBody BillsToAddOrDeleteDTO billsToAddOrDeleteDTO);

    @RequestMapping(value = "/accounts/deleteBill/{accountId}", method = RequestMethod.DELETE)
    void deleteBillFromAccountBillList(@PathVariable Long accountId, @RequestBody BillsToAddOrDeleteDTO billsToAddOrDeleteDTO);
}
