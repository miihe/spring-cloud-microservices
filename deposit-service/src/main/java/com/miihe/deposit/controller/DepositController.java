package com.miihe.deposit.controller;

import com.miihe.deposit.controller.dto.DepositRequestDTO;
import com.miihe.deposit.controller.dto.DepositResponseDTO;
import com.miihe.deposit.entity.Deposit;
import com.miihe.deposit.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class DepositController {

    private final DepositService depositService;

    @Autowired
    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/deposits")
    public DepositResponseDTO depositResponseDTO(@RequestBody DepositRequestDTO depositRequestDTO) {
        return depositService.deposit(depositRequestDTO.getAccountId(),
                depositRequestDTO.getBillId(), depositRequestDTO.getAmount());
    }

    @GetMapping("/deposits/{billId}")
    public List<Deposit> getDepositByBillId(@PathVariable Long billId) {
        return depositService.getDepositByBillId(billId);
    }
}
