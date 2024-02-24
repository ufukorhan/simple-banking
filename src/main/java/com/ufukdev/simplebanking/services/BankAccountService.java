package com.ufukdev.simplebanking.services;

import com.ufukdev.simplebanking.dto.model.BankAccount;
import com.ufukdev.simplebanking.dto.model.DepositTransaction;
import com.ufukdev.simplebanking.dto.model.Transaction;
import com.ufukdev.simplebanking.dto.model.WithdrawalTransaction;
import com.ufukdev.simplebanking.dto.request.CreateBankAccountRequest;
import com.ufukdev.simplebanking.dto.request.CreateTransactionRequest;
import com.ufukdev.simplebanking.dto.response.CreateBankAccountResponse;
import com.ufukdev.simplebanking.dto.response.CreateTransactionResponse;
import com.ufukdev.simplebanking.dto.response.GetBankAccountDetailResponse;
import com.ufukdev.simplebanking.entity.BankAccountEntity;
import com.ufukdev.simplebanking.entity.BankAccountTransactionEntity;
import com.ufukdev.simplebanking.entity.TransactionEntity;
import com.ufukdev.simplebanking.enums.Status;
import com.ufukdev.simplebanking.enums.TransactionType;
import com.ufukdev.simplebanking.exceptions.BankAccountExistsException;
import com.ufukdev.simplebanking.exceptions.NotEnoughMoneyException;
import com.ufukdev.simplebanking.repository.BankAccountRepository;
import com.ufukdev.simplebanking.repository.BankAccountTransactionRepository;
import com.ufukdev.simplebanking.repository.TransactionRepository;
import com.ufukdev.simplebanking.util.ErrorMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


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

    public CreateTransactionResponse withdrawMoney(String accountNumber, CreateTransactionRequest createTransactionRequest){
        BankAccountEntity bankAccountEntity = this.bankAccountRepository.findByAccountNumber(accountNumber);
        this.checkBankAccountExists(bankAccountEntity, accountNumber);
        this.checkBankAccountBalance(createTransactionRequest, bankAccountEntity);

        bankAccountEntity.setBalance(bankAccountEntity.getBalance().subtract(createTransactionRequest.getAmount()));
        this.bankAccountRepository.save(bankAccountEntity);

        WithdrawalTransaction withdrawalTransaction = WithdrawalTransaction.builder()
                .amount(createTransactionRequest.getAmount())
                .build();

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .id(withdrawalTransaction.getApprovalCode())
                .amount(withdrawalTransaction.getAmount())
                .type(TransactionType.WITHDRAWAL.getType())
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

    public GetBankAccountDetailResponse getBankAccountDetails(String accountNumber) {
        BankAccountEntity bankAccountEntity = this.bankAccountRepository.findByAccountNumber(accountNumber);
        this.checkBankAccountExists(bankAccountEntity, accountNumber);

        BankAccount bankAccount = BankAccount.builder()
                .id(bankAccountEntity.getId())
                .accountNumber(bankAccountEntity.getAccountNumber())
                .owner(bankAccountEntity.getOwner())
                .balance(bankAccountEntity.getBalance())
                .createdDate(bankAccountEntity.getCreatedDate())
                .build();


        GetBankAccountDetailResponse getBankAccountDetailResponse = GetBankAccountDetailResponse.builder()
                .accountNumber(bankAccount.getAccountNumber())
                .owner(bankAccount.getOwner())
                .balance(bankAccount.getBalance())
                .createdDate(bankAccount.getCreatedDate().toString())
                .build();


        List<UUID> transactionIdList = this.bankAccountTransactionRepository.findByBankAccountId(
                        bankAccount.getId())
                .stream()
                .map(BankAccountTransactionEntity::getTransactionId)
                .toList();

        if (transactionIdList.isEmpty()) {
            return getBankAccountDetailResponse;
        }

        List<TransactionEntity> transactionEntityList = this.transactionRepository.findAllById(transactionIdList);
        List<Transaction> transactions = transactionEntityList.stream()
                .map(transactionEntity -> Transaction.builder()
                        .approvalCode(transactionEntity.getId())
                        .date(transactionEntity.getDate().toString())
                        .amount(transactionEntity.getAmount())
                        .type(transactionEntity.getType())
                        .build()).collect(Collectors.toList());

        getBankAccountDetailResponse.setTransactions(transactions);
        return getBankAccountDetailResponse;
    }

    private void checkBankAccountExists(BankAccountEntity bankAccountEntity, String accountNumber) {
        if (Objects.isNull(bankAccountEntity)) {
            throw new BankAccountExistsException(ErrorMessage.BANK_ACCOUNT_NOT_FOUND, accountNumber);
        }
    }
    private void checkBankAccountBalance(CreateTransactionRequest createTransactionRequest, BankAccountEntity bankAccountEntity){
        if (bankAccountEntity.getBalance().compareTo(createTransactionRequest.getAmount()) < 0){
            throw new NotEnoughMoneyException(ErrorMessage.NOT_ENOUGH_MONEY, bankAccountEntity.getAccountNumber());
        }
    }
}
