package com.ufukdev.simplebanking.repository;

import com.ufukdev.simplebanking.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    BankAccount findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);
}
