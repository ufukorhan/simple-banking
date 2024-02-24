package com.ufukdev.simplebanking.repository;

import com.ufukdev.simplebanking.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccountEntity, UUID> {

    BankAccountEntity findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
