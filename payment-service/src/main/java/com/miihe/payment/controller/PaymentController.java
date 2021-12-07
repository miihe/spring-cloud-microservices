package com.miihe.payment.controller;

import com.miihe.payment.controller.dto.PaymentRequestDTO;
import com.miihe.payment.controller.dto.PaymentResponseDTO;
import com.miihe.payment.entity.Payment;
import com.miihe.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments")
    public PaymentResponseDTO paymentResponseDTO(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        return paymentService.payment(paymentRequestDTO.getAccountId(),
                paymentRequestDTO.getBillId(), paymentRequestDTO.getAmount());
    }

    @GetMapping("/payments/{billId}")
    public List<Payment> getPaymentsByBillId(@PathVariable Long billId) {
        return paymentService.getPaymentsByBillId(billId);
    }
}
