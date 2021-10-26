package com.miihe.account.repository;

import com.miihe.account.entity.Account;
import org.springframework.data.repository.CrudRepository;


public interface AccountRepository extends CrudRepository<Account, Long> {
}
