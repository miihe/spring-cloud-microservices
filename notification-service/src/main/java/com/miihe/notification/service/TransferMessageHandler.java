package com.miihe.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miihe.notification.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class TransferMessageHandler {

    @Autowired
    private final JavaMailSender javaMailSender;

    public TransferMessageHandler(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_TRANSFER)
    public void receive(Message message) throws JsonProcessingException {
        System.out.println(message);
        byte[] body = message.getBody();
        String jsonBody = new String(body);
        ObjectMapper objectMapper = new ObjectMapper();
        TransferResponseDTO transferResponseDTO = objectMapper.readValue(jsonBody, TransferResponseDTO.class);
        System.out.println(transferResponseDTO);

        SimpleMailMessage mailMessageToSender = new SimpleMailMessage();
        mailMessageToSender.setTo(transferResponseDTO.getSenderEmail());
        mailMessageToSender.setFrom("lori@cat.xyz");
        mailMessageToSender.setSubject("Transfer");
        mailMessageToSender.setText("You make transfer to " + transferResponseDTO.getPayeeName() +
                ". Sum: " + transferResponseDTO.getAmount());

        SimpleMailMessage mailMessageToPayee = new SimpleMailMessage();
        mailMessageToPayee.setTo(transferResponseDTO.getPayeeEmail());
        mailMessageToPayee.setFrom("lori@cat.xyz");
        mailMessageToPayee.setSubject("Transfer");
        mailMessageToPayee.setText("You have been transferred funds in the sum of : " + transferResponseDTO.getAmount() +
                " - from the addressee: " + transferResponseDTO.getSenderEmail());

        try {
            javaMailSender.send(mailMessageToSender);
            javaMailSender.send(mailMessageToPayee);
        } catch (Exception exception) {
            System.out.println(exception);
        }

    }
}
