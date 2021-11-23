package com.miihe.deposit.controller;

import com.miihe.deposit.controller.dto.DepositRequestDTO;
import com.miihe.deposit.controller.dto.DepositResponseDTO;
import com.miihe.deposit.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
