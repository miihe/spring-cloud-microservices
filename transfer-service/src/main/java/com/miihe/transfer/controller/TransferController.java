package com.miihe.transfer.controller;

import com.miihe.transfer.controller.dto.TransferRequestDTO;
import com.miihe.transfer.controller.dto.TransferResponseDTO;
import com.miihe.transfer.entity.Transfer;
import com.miihe.transfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransferController {

    private final TransferService transferService;

    @Autowired
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfers")
    public TransferResponseDTO transfer(@RequestBody TransferRequestDTO transferRequestDTO) {
        return transferService.transfer(transferRequestDTO.getSenderAccountId(), transferRequestDTO.getPayeeAccountId(),
                transferRequestDTO.getSenderBillId(), transferRequestDTO.getPayeeBillId(), transferRequestDTO.getAmount());
    }

    @GetMapping("/transfers/{transferId}")
    public TransferResponseDTO getPaymentsByBillId(@PathVariable Long transferId) {
        Transfer transfer = transferService.getTransferById(transferId);
        return new TransferResponseDTO(transfer.getAmount(), transfer.getSenderName(), transfer.getPayeeName());
    }
}
