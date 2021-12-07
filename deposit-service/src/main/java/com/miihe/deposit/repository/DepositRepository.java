package com.miihe.deposit.repository;

import com.miihe.deposit.entity.Deposit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DepositRepository extends CrudRepository<Deposit, Long> {

    List<Deposit> findDepositsByEmail(String email);

    List<Deposit> findAllDepositsByBillId(Long billId);
}
