package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miihe.payment.PaymentApplication;
import com.miihe.payment.controller.dto.PaymentResponseDTO;
import com.miihe.payment.entity.Payment;
import com.miihe.payment.repository.PaymentRepository;
import com.miihe.payment.rest.AccountResponseDTO;
import com.miihe.payment.rest.AccountServiceClient;
import com.miihe.payment.rest.BillResponseDTO;
import com.miihe.payment.rest.BillServiceClient;
import config.SpringH2DatabaseConfig;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import utils.PaymentTestUtil;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PaymentApplication.class, SpringH2DatabaseConfig.class})
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private AccountServiceClient accountServiceClient;

    @MockBean
    private BillServiceClient billServiceClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private static final String REQUEST = "{\n" +
            "    \"billId\": 1,\n" +
            "    \"amount\": 2000\n" +
            "}";

    @Test
    public void createPayment() throws Exception {
        AccountResponseDTO accountResponseDTO = PaymentTestUtil.createAccountResponseDTO();
        BillResponseDTO billResponseDTO = PaymentTestUtil.createBillResponseDTO();
        Mockito.when(billServiceClient.getBillById(ArgumentMatchers.anyLong()))
                .thenReturn(billResponseDTO);
        Mockito.when(accountServiceClient.getAccountById(ArgumentMatchers.anyLong()))
                .thenReturn(accountResponseDTO);
        MvcResult mvcResult = mockMvc.perform(post("/payments").content(REQUEST)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<Payment> payments = paymentRepository.findPaymentsByEmail("PaymentTest@mail.ru");
        ObjectMapper objectMapper = new ObjectMapper();
        PaymentResponseDTO paymentResponseDTO = objectMapper.readValue(body, PaymentResponseDTO.class);
        Assertions.assertThat(paymentResponseDTO.getMail()).isEqualTo(payments.get(0).getEmail());
        Assertions.assertThat(paymentResponseDTO.getAmount()).isEqualByComparingTo((payments.get(0).getAmount()));
    }
}
