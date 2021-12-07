package com.miihe.transfer.repository;

import com.miihe.transfer.entity.Transfer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransferRepository extends CrudRepository<Transfer, Long> {
}
