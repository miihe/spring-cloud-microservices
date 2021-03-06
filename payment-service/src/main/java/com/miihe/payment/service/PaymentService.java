package com.miihe.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miihe.payment.controller.dto.PaymentResponseDTO;
import com.miihe.payment.entity.Payment;
import com.miihe.payment.exception.PaymentServiceException;
import com.miihe.payment.repository.PaymentRepository;
import com.miihe.payment.rest.*;
import feign.FeignException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

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

    @Transactional
    public PaymentResponseDTO payment(Long accountId, Long billId, BigDecimal amount) {
        if (accountId == null & billId == null) {
            throw new PaymentServiceException("Payment operation is was canceled. Account ID and bill ID is null.");
        }

        if (billId != null) {
            try{
                billServiceClient.getBillById(billId);
            } catch (FeignException e) {
                throw new PaymentServiceException("Payment operation is was canceled. " +
                        "Unable to find bill with id: " + billId);
            }
            BillResponseDTO billResponseDTO = billServiceClient.getBillById(billId);
            BillRequestDTO billRequestDTO = createBillRequest(amount, billResponseDTO);

            billServiceClient.updateAmountOfBill(billId, billRequestDTO.getAmount());

            AccountResponseDTO accountResponseDTO = accountServiceClient.getAccountById(billResponseDTO.getAccountId());
            paymentRepository.save(new Payment(amount, billId, OffsetDateTime.now(), accountResponseDTO.getEmail()));

            return createResponse(amount, accountResponseDTO);
        }
        try{
            accountServiceClient.getAccountById(accountId);
        } catch (FeignException e) {
            throw new PaymentServiceException("Payment operation is was canceled. " +
                    "Unable to find account with id: " + accountId);
        }
        BillResponseDTO defaultBill = getDefaultBill(accountId);
        BillRequestDTO billRequestDTO = createBillRequest(amount, defaultBill);
        billServiceClient.updateAmountOfBill(defaultBill.getBillId(), billRequestDTO.getAmount());
        AccountResponseDTO account = accountServiceClient.getAccountById(accountId);
        paymentRepository.save(new Payment(amount, defaultBill.getBillId(), OffsetDateTime.now(), account.getEmail()));
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
                new PaymentServiceException("Payment operation is was canceled." +
                        "Unable to find default bill for account: " + accountId));
    }

    public List<Payment> getPaymentsByBillId(Long billId) {
        if (paymentRepository.findAllPaymentsByBillId(billId).isEmpty()) {
            throw new PaymentServiceException("Unable to find any payment from bill with id: " + billId);
        }
        return paymentRepository.findAllPaymentsByBillId(billId);
    }
}
