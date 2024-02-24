package com.ufukdev.simplebanking.repository;

import com.ufukdev.simplebanking.entity.BankAccountTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountTransactionRepository extends JpaRepository<BankAccountTransactionEntity, UUID> {
    List<BankAccountTransactionEntity> findByBankAccountId(UUID id);
}
