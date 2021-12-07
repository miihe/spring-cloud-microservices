package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miihe.transfer.TransferApplication;
import com.miihe.transfer.controller.dto.TransferResponseDTO;
import com.miihe.transfer.entity.Transfer;
import com.miihe.transfer.repository.TransferRepository;
import com.miihe.transfer.rest.AccountResponseDTO;
import com.miihe.transfer.rest.AccountServiceClient;
import com.miihe.transfer.rest.BillResponseDTO;
import com.miihe.transfer.rest.BillServiceClient;
import config.SpringH2DatabaseConfig;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import utils.TransferTestUtil;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TransferApplication.class, SpringH2DatabaseConfig.class})
public class TransferControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TransferRepository transferRepository;

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

    private static String REQUEST = "{\n" +
            "    \"senderBillId\": 1,\n" +
            "    \"payeeBillId\": 2,\n" +
            "    \"amount\": 500\n" +
            "}";

    @Test
    public void createTransfer() throws Exception {
        AccountResponseDTO senderAccountResponseDTO = TransferTestUtil.createSenderAccountResponseDTO();
        BillResponseDTO senderBillResponseDTO = TransferTestUtil.createSenderBillResponseDTO();
        AccountResponseDTO payeeAccountResponseDTO = TransferTestUtil.createPayeeAccountResponseDTO();
        BillResponseDTO payeeBillResponseDTO = TransferTestUtil.createPayeeBillResponseDTO();
        Mockito.when(accountServiceClient.getAccountById(1L))
                .thenReturn(senderAccountResponseDTO);
        Mockito.when(accountServiceClient.getAccountById(2L))
                .thenReturn(payeeAccountResponseDTO);
        Mockito.when(billServiceClient.getBillById(1L))
                .thenReturn(senderBillResponseDTO);
        Mockito.when(billServiceClient.getBillById(2L))
                .thenReturn(payeeBillResponseDTO);

        MvcResult mvcResult = mockMvc.perform(post("/transfers").content(REQUEST)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        String body = mvcResult.getResponse().getContentAsString();
        List<Transfer> transfers = (List<Transfer>) transferRepository.findAll();
        ObjectMapper objectMapper = new ObjectMapper();
        TransferResponseDTO transferResponseDTO = objectMapper.readValue(body, TransferResponseDTO.class);
        Assertions.assertThat(transferResponseDTO.getSenderName()).isEqualTo(transfers.get(0).getSenderName());
        Assertions.assertThat(transferResponseDTO.getPayeeName()).isEqualTo(transfers.get(0).getPayeeName());
        Assertions.assertThat(transferResponseDTO.getAmount()).isEqualByComparingTo(transfers.get(0).getAmount());
    }
}
