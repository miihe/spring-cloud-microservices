package com.miihe.transfer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miihe.transfer.controller.dto.TransferResponseDTO;
import com.miihe.transfer.entity.Transfer;
import com.miihe.transfer.exception.TransferServiceException;
import com.miihe.transfer.repository.TransferRepository;
import com.miihe.transfer.rest.*;
import feign.FeignException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public TransferResponseDTO transfer(Long senderAccountId, Long payeeAccountId, Long senderBillId, Long payeeBillId,
                                        BigDecimal amount) {
        if (senderAccountId == null & senderBillId == null) {
            throw new TransferServiceException("Transfer operation is was canceled." +
                    "Parameters of sender are not set (accountId and billId).");
        }

        if(payeeAccountId == null & payeeBillId == null) {
            throw new TransferServiceException("Transfer operation is was canceled." +
                    "Parameters of payee are not set (accountId and billId).");
        }

        if (senderBillId != null & payeeBillId != null) {
            try{
                billServiceClient.getBillById(senderBillId);
                billServiceClient.getBillById(payeeBillId);
            } catch (FeignException e) {
                throw new TransferServiceException("Transfer operation is was canceled." +
                        "Unable to find bill of sender or/and payee.");
            }
            BillResponseDTO senderBillResponseDTO = billServiceClient.getBillById(senderBillId);
            BillResponseDTO payeeBillResponseDTO = billServiceClient.getBillById(payeeBillId);
            BillRequestDTO senderBillRequestDTO = createBillRequestSubtract(amount, senderBillResponseDTO);
            BillRequestDTO payeeBillRequestDTO = createBillRequestAdd(amount, payeeBillResponseDTO);
            billServiceClient.updateAmountOfBill(senderBillId, senderBillRequestDTO.getAmount());
            billServiceClient.updateAmountOfBill(payeeBillId, payeeBillRequestDTO.getAmount());
            AccountResponseDTO senderAccountResponseDTO = accountServiceClient.getAccountById(senderBillResponseDTO.getAccountId());
            AccountResponseDTO payeeAccountResponseDTO = accountServiceClient.getAccountById(payeeBillResponseDTO.getAccountId());
            transferRepository.save(new Transfer(senderAccountResponseDTO.getName(), senderBillId, amount,
                    payeeAccountResponseDTO.getName(), payeeBillId, OffsetDateTime.now()));
            return createResponse(amount, senderAccountResponseDTO, payeeAccountResponseDTO);
        }
        return partOfTransferByAccountId(senderAccountId, payeeAccountId, amount);
    }

    private TransferResponseDTO partOfTransferByAccountId (Long senderAccountId, Long payeeAccountId, BigDecimal amount) {
        try{
            accountServiceClient.getAccountById(senderAccountId);
            accountServiceClient.getAccountById(payeeAccountId);
        } catch (FeignException e) {
            throw new TransferServiceException("Transfer operation is was canceled." +
                    "Unable to find account of sender or/and payee.");
        }
        BillResponseDTO senderDefaultBill = getDefaultBill(senderAccountId);
        BillResponseDTO payeeDefaultBill = getDefaultBill(payeeAccountId);
        BillRequestDTO senderBillRequestDTO = createBillRequestSubtract(amount, senderDefaultBill);
        BillRequestDTO payeeBillRequestDTO = createBillRequestAdd(amount, payeeDefaultBill);
        billServiceClient.updateAmountOfBill(senderDefaultBill.getBillId(), senderBillRequestDTO.getAmount());
        billServiceClient.updateAmountOfBill(payeeDefaultBill.getBillId(), payeeBillRequestDTO.getAmount());
        AccountResponseDTO senderAccount = accountServiceClient.getAccountById(senderAccountId);
        AccountResponseDTO payeeAccount = accountServiceClient.getAccountById(payeeAccountId);
        transferRepository.save(new Transfer(senderAccount.getName(), getDefaultBill(senderAccountId).getBillId(), amount,
                payeeAccount.getName(), getDefaultBill(payeeAccountId).getBillId(), OffsetDateTime.now()));
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
                new TransferServiceException("Transfer operation is was canceled." +
                        "Unable to find default bill for account: " + accountId));
    }

    public Transfer getTransferById(Long transferId) {

        return transferRepository.findById(transferId)
                .orElseThrow(() -> new TransferServiceException("Unable to find default bill for account: " + transferId));
    }
}
