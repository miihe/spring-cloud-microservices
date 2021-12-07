package utils;

import com.miihe.transfer.rest.AccountResponseDTO;
import com.miihe.transfer.rest.BillResponseDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;

public class TransferTestUtil {

    public static AccountResponseDTO createSenderAccountResponseDTO() {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setAccountId(1L);
        accountResponseDTO.setName("Miihe");
        accountResponseDTO.setEmail("TransferTestSender@mail.ru");
        accountResponseDTO.setPhone("+123123");
        accountResponseDTO.setBills(Arrays.asList(1L));
        accountResponseDTO.setCreationDate(OffsetDateTime.now());
        return accountResponseDTO;
    }

    public static BillResponseDTO createSenderBillResponseDTO() {
        BillResponseDTO billResponseDTO = new BillResponseDTO();
        billResponseDTO.setAccountId(1L);
        billResponseDTO.setAmount(BigDecimal.valueOf(1000));
        billResponseDTO.setBillId(1L);
        billResponseDTO.setCreationDate(OffsetDateTime.now());
        billResponseDTO.setIsDefault(true);
        billResponseDTO.setOverdraftEnabled(true);
        return billResponseDTO;
    }

    public static AccountResponseDTO createPayeeAccountResponseDTO() {
        AccountResponseDTO accountResponseDTO = new AccountResponseDTO();
        accountResponseDTO.setAccountId(2L);
        accountResponseDTO.setName("Miihe");
        accountResponseDTO.setEmail("TransferTestPayee@mail.ru");
        accountResponseDTO.setPhone("+123123");
        accountResponseDTO.setBills(Arrays.asList(2L));
        accountResponseDTO.setCreationDate(OffsetDateTime.now());
        return accountResponseDTO;
    }

    public static BillResponseDTO createPayeeBillResponseDTO() {
        BillResponseDTO billResponseDTO = new BillResponseDTO();
        billResponseDTO.setAccountId(2L);
        billResponseDTO.setAmount(BigDecimal.valueOf(2000));
        billResponseDTO.setBillId(2L);
        billResponseDTO.setCreationDate(OffsetDateTime.now());
        billResponseDTO.setIsDefault(true);
        billResponseDTO.setOverdraftEnabled(true);
        return billResponseDTO;
    }
}
