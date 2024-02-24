package com.ufukdev.simplebanking.services;

import com.ufukdev.simplebanking.constant.ErrorMessage;
import com.ufukdev.simplebanking.dto.request.CreateBankAccountRequest;
import com.ufukdev.simplebanking.dto.response.CreateBankAccountResponse;
import com.ufukdev.simplebanking.enums.Status;
import com.ufukdev.simplebanking.exceptions.BankAccountExistsException;
import com.ufukdev.simplebanking.model.BankAccount;
import com.ufukdev.simplebanking.repository.BankAccountRepository;
import org.springframework.stereotype.Service;


@Service
public class BankAccountService{
    private final BankAccountRepository bankAccountRepository;
    public BankAccountService(BankAccountRepository bankAccountRepository){
        this.bankAccountRepository = bankAccountRepository;
    }

    public CreateBankAccountResponse createBankAccount(CreateBankAccountRequest createBankAccountRequest) {
        if (this.bankAccountRepository.existsByAccountNumber(createBankAccountRequest.getAccountNumber())) {
            throw new BankAccountExistsException(
                    ErrorMessage.BANK_ACCOUNT_ALREADY_EXISTS,
                    createBankAccountRequest.getAccountNumber()
            );
        }

        BankAccount bankAccount = BankAccount.builder()
                .accountNumber(createBankAccountRequest.getAccountNumber())
                .owner(createBankAccountRequest.getOwner())
                .balance(createBankAccountRequest.getBalance())
                .build();
        this.bankAccountRepository.save(bankAccount);
        CreateBankAccountResponse createBankAccountResponse = CreateBankAccountResponse.builder()
                .approvalCode(bankAccount.getId())
                .build();
        createBankAccountResponse.setStatus(Status.OK.getStatus());
        return createBankAccountResponse;
    }
}
