package com.miihe.account.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "bill-service")
public interface BillServiceClient {

    @RequestMapping(value = "/bills/{billId}", method = RequestMethod.GET)
    BillResponseDTO getBill(@PathVariable Long billId);

    @RequestMapping(value = "/bills/defaultCreate/", method = RequestMethod.POST)
    Long createDefaultBillFromAccountService(@RequestBody BillRequestDTO billRequestDTO);

    @RequestMapping(value = "/bills/deleteWithAccount/{billId}", method = RequestMethod.DELETE)
    void deleteBillWithAccount(@PathVariable Long billId);
}
