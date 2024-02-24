package com.ufukdev.simplebanking.services;

import com.ufukdev.simplebanking.dto.model.DepositTransaction;
import com.ufukdev.simplebanking.dto.request.CreateBankAccountRequest;
import com.ufukdev.simplebanking.dto.request.CreateTransactionRequest;
import com.ufukdev.simplebanking.dto.response.CreateBankAccountResponse;
import com.ufukdev.simplebanking.dto.response.CreateTransactionResponse;
import com.ufukdev.simplebanking.entity.BankAccountEntity;
import com.ufukdev.simplebanking.entity.BankAccountTransactionEntity;
import com.ufukdev.simplebanking.entity.TransactionEntity;
import com.ufukdev.simplebanking.enums.Status;
import com.ufukdev.simplebanking.enums.TransactionType;
import com.ufukdev.simplebanking.exceptions.BankAccountExistsException;
import com.ufukdev.simplebanking.repository.BankAccountRepository;
import com.ufukdev.simplebanking.repository.BankAccountTransactionRepository;
import com.ufukdev.simplebanking.repository.TransactionRepository;
import com.ufukdev.simplebanking.util.ErrorMessage;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class BankAccountService{
    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final BankAccountTransactionRepository bankAccountTransactionRepository;

    public BankAccountService(
            BankAccountRepository bankAccountRepository,
            TransactionRepository transactionRepository,
            BankAccountTransactionRepository bankAccountTransactionRepository
    ){
        this.bankAccountRepository = bankAccountRepository;
        this.transactionRepository = transactionRepository;
        this.bankAccountTransactionRepository = bankAccountTransactionRepository;
    }

    public CreateBankAccountResponse createBankAccount(CreateBankAccountRequest createBankAccountRequest) {
        if (this.bankAccountRepository.existsByAccountNumber(createBankAccountRequest.getAccountNumber())) {
            throw new BankAccountExistsException(
                    ErrorMessage.BANK_ACCOUNT_ALREADY_EXISTS,
                    createBankAccountRequest.getAccountNumber()
            );
        }

        BankAccountEntity bankAccountEntity = BankAccountEntity.builder()
                .accountNumber(createBankAccountRequest.getAccountNumber())
                .owner(createBankAccountRequest.getOwner())
                .balance(createBankAccountRequest.getBalance())
                .build();
        this.bankAccountRepository.save(bankAccountEntity);
        CreateBankAccountResponse createBankAccountResponse = CreateBankAccountResponse.builder()
                .approvalCode(bankAccountEntity.getId())
                .build();
        createBankAccountResponse.setStatus(Status.OK.getStatus());
        return createBankAccountResponse;
    }

    public CreateTransactionResponse depositMoney(String accountNumber, CreateTransactionRequest createTransactionRequest) {
        BankAccountEntity bankAccountEntity = this.bankAccountRepository.findByAccountNumber(accountNumber);
        this.checkBankAccountExists(bankAccountEntity, accountNumber);
        bankAccountEntity.setBalance(bankAccountEntity.getBalance().add(createTransactionRequest.getAmount()));
        this.bankAccountRepository.save(bankAccountEntity);

        DepositTransaction depositTransaction = DepositTransaction.builder()
                .amount(createTransactionRequest.getAmount())
                .build();

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .id(depositTransaction.getApprovalCode())
                .amount(depositTransaction.getAmount())
                .type(TransactionType.DEPOSIT.getType())
                .build();

        this.transactionRepository.save(transactionEntity);

        BankAccountTransactionEntity bankAccountTransactionEntity = BankAccountTransactionEntity.builder()
                .bankAccountId(bankAccountEntity.getId())
                .transactionId(transactionEntity.getId())
                .build();

        this.bankAccountTransactionRepository.save(bankAccountTransactionEntity);

        return CreateTransactionResponse.builder()
                .approvalCode(transactionEntity.getId())
                .status(Status.OK.getStatus())
                .build();
    }

    private void checkBankAccountExists(BankAccountEntity bankAccountEntity, String accountNumber) {
        if (Objects.isNull(bankAccountEntity)) {
            throw new BankAccountExistsException(ErrorMessage.BANK_ACCOUNT_NOT_FOUND, accountNumber);
        }
    }
}
