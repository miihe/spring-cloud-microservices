package com.miihe.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miihe.payment.controller.dto.PaymentResponseDTO;
import com.miihe.payment.entity.Payment;
import com.miihe.payment.exception.PaymentServiceException;
import com.miihe.payment.repository.PaymentRepository;
import com.miihe.payment.rest.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class PaymentService {

    private static final String TOPIC_EXCHANGE_PAYMENT = "js.payment.notify.exchange";

    private static final String ROUTING_KEY_PAYMENT = "js.key.payment";

    private PaymentRepository paymentRepository;

    private final AccountServiceClient accountServiceClient;

    private final BillServiceClient billServiceClient;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, AccountServiceClient accountServiceClient,
                          BillServiceClient billServiceClient, RabbitTemplate rabbitTemplate) {
        this.paymentRepository = paymentRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public PaymentResponseDTO payment(Long accountId, Long billId, BigDecimal amount) {
        if (accountId == null & billId == null) {
            throw new PaymentServiceException("Account ID and bill ID is null.");
        }

        if (billId != null) {
            BillResponseDTO billResponseDTO = billServiceClient.getBillById(billId);
            BillRequestDTO billRequestDTO = createBillRequest(amount, billResponseDTO);

            billServiceClient.update(billId, billRequestDTO);

            AccountResponseDTO accountResponseDTO = accountServiceClient.getAccountById(billResponseDTO.getAccountId());
            paymentRepository.save(new Payment(amount, billId, OffsetDateTime.now(), accountResponseDTO.getEmail()));

            return createResponse(amount, accountResponseDTO);
        }
        BillResponseDTO defaultBill = getDefaultBill(accountId);
        BillRequestDTO billRequestDTO = createBillRequest(amount, defaultBill);
        billServiceClient.update(defaultBill.getBillId(), billRequestDTO);
        AccountResponseDTO account = accountServiceClient.getAccountById(accountId);
        paymentRepository.save(new Payment(amount, defaultBill.getBillId(), OffsetDateTime.now(), account.getEmail()));
        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(amount, account.getEmail());
        return createResponse(amount, account);
    }

    private PaymentResponseDTO createResponse(BigDecimal amount, AccountResponseDTO accountResponseDTO) {
        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO(amount, accountResponseDTO.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.
                    convertAndSend(TOPIC_EXCHANGE_PAYMENT, ROUTING_KEY_PAYMENT,
                            objectMapper.writeValueAsString(paymentResponseDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new PaymentServiceException("Can't send message to RabbitMQ");
        }
        return paymentResponseDTO;
    }

    private BillRequestDTO createBillRequest(BigDecimal amount, BillResponseDTO billResponseDTO) {
        BillRequestDTO billRequestDTO = new BillRequestDTO();
        billRequestDTO.setAccountId(billResponseDTO.getAccountId());
        billRequestDTO.setCreationDate(billResponseDTO.getCreationDate());
        billRequestDTO.setIsDefault(billResponseDTO.getIsDefault());
        billRequestDTO.setOverdraftEnabled(billResponseDTO.getOverdraftEnabled());
        billRequestDTO.setAmount(billResponseDTO.getAmount().subtract(amount));
        return billRequestDTO;
    }

    private BillResponseDTO getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId).
                stream().
                filter(BillResponseDTO::getIsDefault).
                findAny().orElseThrow(() ->
                new PaymentServiceException("Unable to find default bill for account: " + accountId));
    }
}