package com.miihe.bill.controller;

import com.miihe.bill.controller.dto.BillRequestDTO;
import com.miihe.bill.controller.dto.BillResponseDTO;
import com.miihe.bill.controller.dto.BillUpdateRequestDTO;
import com.miihe.bill.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class BillController {

    private final BillService billService;

    @Autowired
    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/{billId}")
    public BillResponseDTO getBill(@PathVariable Long billId) {
        return new BillResponseDTO(billService.getBillById(billId));
    }

    @PostMapping("/defaultCreate/")
    public Long createDefaultBillFromAccountService(@RequestBody BillRequestDTO billRequestDTO) {
        return billService.createDefaultBillFromAccountService(billRequestDTO.getAccountId(), billRequestDTO.getAmount(),
                billRequestDTO.getIsDefault(), billRequestDTO.getOverdraftEnabled());
    }

    @PostMapping
    public Long createBill(@RequestBody BillRequestDTO billRequestDTO) {
        return billService.createBill(billRequestDTO.getAccountId(), billRequestDTO.getAmount(),
                billRequestDTO.getIsDefault(), billRequestDTO.getOverdraftEnabled());
    }

    @PutMapping("/{billId}")
    public BillResponseDTO updateBill(@PathVariable Long billId, @RequestBody BillUpdateRequestDTO billUpdateRequestDTO) {
        return new BillResponseDTO(billService.updateBill(billId, billUpdateRequestDTO.getIsDefault(),
                billUpdateRequestDTO.getOverdraftEnabled()));
    }

    @DeleteMapping("/{billId}")
    public BillResponseDTO deleteBill(@PathVariable Long billId) {
        return new BillResponseDTO(billService.deleteBill(billId));
    }

    @DeleteMapping("/deleteWithAccount/{billId}")
    public BillResponseDTO deleteBillWithAccount(@PathVariable Long billId) {
        return new BillResponseDTO(billService.deleteBillWithAccount(billId));
    }

    @PatchMapping("/{billId}/{amount}")
    public void updateAmountOfBill(@PathVariable Long billId, @PathVariable BigDecimal amount) {
        billService.updateAmountOfBill(billId, amount);
    }

    @GetMapping("/account/{accountId}")
    public List<BillResponseDTO> getBillsByAccountId(@PathVariable Long accountId) {
        return billService.getBillsByAccountId(accountId).stream().
                map(BillResponseDTO::new).
                collect(Collectors.toList());

    }
}
