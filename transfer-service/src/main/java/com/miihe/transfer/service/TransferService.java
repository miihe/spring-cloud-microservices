package com.miihe.transfer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miihe.transfer.controller.dto.TransferResponseDTO;
import com.miihe.transfer.entity.Transfer;
import com.miihe.transfer.exception.TransferServiceException;
import com.miihe.transfer.repository.TransferRepository;
import com.miihe.transfer.rest.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TransferService {

    private static final String TOPIC_EXCHANGE_TRANSFER = "js.transfer.notify.exchange";

    private static final String ROUTING_KEY_TRANSFER = "js.key.transfer";

    private TransferRepository transferRepository;

    private final AccountServiceClient accountServiceClient;

    private final BillServiceClient billServiceClient;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public TransferService(TransferRepository transferRepository, AccountServiceClient accountServiceClient,
                           BillServiceClient billServiceClient, RabbitTemplate rabbitTemplate) {
        this.transferRepository = transferRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    public TransferResponseDTO transfer(Long senderAccountId, Long payeeAccountId, Long senderBillId, Long payeeBillId,
                                        BigDecimal amount) {
        if ((senderAccountId == null & senderBillId == null) && (payeeAccountId == null & payeeBillId == null)) {
            throw new TransferServiceException("Parameters of sender (payee) are not set (accountId and billId).");
        }

        if (senderBillId != null & payeeBillId != null) {
            BillResponseDTO senderBillResponseDTO = billServiceClient.getBillById(senderBillId);
            BillResponseDTO payeeBillResponseDTO = billServiceClient.getBillById(payeeBillId);
            BillRequestDTO senderBillRequestDTO = createBillRequestSubtract(amount, senderBillResponseDTO);
            BillRequestDTO payeeBillRequestDTO = createBillRequestAdd(amount, payeeBillResponseDTO);
            billServiceClient.update(senderBillId, senderBillRequestDTO);
            billServiceClient.update(payeeBillId, payeeBillRequestDTO);
            AccountResponseDTO senderAccountResponseDTO = accountServiceClient.getAccountById(senderBillResponseDTO.getAccountId());
            AccountResponseDTO payeeAccountResponseDTO = accountServiceClient.getAccountById(payeeBillResponseDTO.getAccountId());
            transferRepository.save(new Transfer(senderAccountResponseDTO.getName(), senderBillId, amount,
                    payeeAccountResponseDTO.getName(), payeeBillId, OffsetDateTime.now()));
            return createResponse(amount, senderAccountResponseDTO, payeeAccountResponseDTO);
        }
        BillResponseDTO senderDefaultBill = getDefaultBill(senderAccountId);
        BillResponseDTO payeeDefaultBill = getDefaultBill(payeeAccountId);
        BillRequestDTO senderBillRequestDTO = createBillRequestSubtract(amount, senderDefaultBill);
        BillRequestDTO payeeBillRequestDTO = createBillRequestAdd(amount, payeeDefaultBill);
        billServiceClient.update(senderDefaultBill.getBillId(), senderBillRequestDTO);
        billServiceClient.update(payeeDefaultBill.getBillId(), payeeBillRequestDTO);
        AccountResponseDTO senderAccount = accountServiceClient.getAccountById(senderAccountId);
        AccountResponseDTO payeeAccount = accountServiceClient.getAccountById(payeeAccountId);
        transferRepository.save(new Transfer(senderAccount.getName(), senderBillId, amount,
                payeeAccount.getName(), payeeBillId, OffsetDateTime.now()));
        TransferResponseDTO transferResponseDTO = new TransferResponseDTO(amount, senderAccount.getName(),
                payeeAccount.getName(), senderAccount.getEmail(), payeeAccount.getEmail());
        return createResponse(amount, senderAccount, payeeAccount);
    }

    private TransferResponseDTO createResponse(BigDecimal amount, AccountResponseDTO senderAccountResponseDTO,
                                               AccountResponseDTO payeeAccountResponseDTO) {
        TransferResponseDTO transferResponseDTO = new TransferResponseDTO(amount, senderAccountResponseDTO.getName(),
                payeeAccountResponseDTO.getName(), senderAccountResponseDTO.getEmail(), payeeAccountResponseDTO.getEmail());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rabbitTemplate.
                    convertAndSend(TOPIC_EXCHANGE_TRANSFER, ROUTING_KEY_TRANSFER,
                            objectMapper.writeValueAsString(transferResponseDTO));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TransferServiceException("Can't send message to RabbitMQ");
        }
        return transferResponseDTO;
    }

    private BillRequestDTO createBillRequestSubtract (BigDecimal amount, BillResponseDTO billResponseDTO) {
        BillRequestDTO billRequestDTO = new BillRequestDTO();
        billRequestDTO.setAccountId(billResponseDTO.getAccountId());
        billRequestDTO.setCreationDate(billResponseDTO.getCreationDate());
        billRequestDTO.setIsDefault(billResponseDTO.getIsDefault());
        billRequestDTO.setOverdraftEnabled(billResponseDTO.getOverdraftEnabled());
        billRequestDTO.setAmount(billResponseDTO.getAmount().subtract(amount));
        return billRequestDTO;
    }

    private BillRequestDTO createBillRequestAdd (BigDecimal amount, BillResponseDTO billResponseDTO) {
        BillRequestDTO billRequestDTO = new BillRequestDTO();
        billRequestDTO.setAccountId(billResponseDTO.getAccountId());
        billRequestDTO.setCreationDate(billResponseDTO.getCreationDate());
        billRequestDTO.setIsDefault(billResponseDTO.getIsDefault());
        billRequestDTO.setOverdraftEnabled(billResponseDTO.getOverdraftEnabled());
        billRequestDTO.setAmount(billResponseDTO.getAmount().add(amount));
        return billRequestDTO;
    }

    private BillResponseDTO getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId).
                stream().
                filter(BillResponseDTO::getIsDefault).
                findAny().orElseThrow(() ->
                new TransferServiceException("Unable to find default bill for account: " + accountId));
    }
}
