package com.miihe.payment.repository;

import com.miihe.payment.entity.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findPaymentsByEmail(String email);

    List<Payment> findAllPaymentsByBillId(Long billId);
}
