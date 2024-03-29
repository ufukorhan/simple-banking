package com.ufukdev.simplebanking.controller;


import com.ufukdev.simplebanking.dto.request.CreateBankAccountRequest;
import com.ufukdev.simplebanking.dto.request.CreateTransactionRequest;
import com.ufukdev.simplebanking.dto.response.CreateBankAccountResponse;
import com.ufukdev.simplebanking.dto.response.CreateTransactionResponse;
import com.ufukdev.simplebanking.dto.response.GetBankAccountDetailResponse;
import com.ufukdev.simplebanking.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/account/v1", produces = "application/json", consumes = "application/json")
public class AccountController {
    private final BankAccountService bankAccountService;

    public AccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping(value = "/createBankAccount")
    public ResponseEntity<CreateBankAccountResponse> createBankAccount(@RequestBody CreateBankAccountRequest createBankAccountRequest) {
        return ResponseEntity.ok(this.bankAccountService.createBankAccount(createBankAccountRequest));
    }

    @PostMapping(value = "/credit/{accountNumber}")
    public ResponseEntity<CreateTransactionResponse> depositMoney(@PathVariable("accountNumber") String accountNumber, @RequestBody CreateTransactionRequest createTransactionRequest) {
        return ResponseEntity.ok(this.bankAccountService.depositMoney(accountNumber, createTransactionRequest));
    }

    @PostMapping(value = "/debit/{accountNumber}")
    public ResponseEntity<CreateTransactionResponse> withdrawMoney(@PathVariable("accountNumber") String accountNumber, @RequestBody CreateTransactionRequest createTransactionRequest){
        return ResponseEntity.ok(this.bankAccountService.withdrawMoney(accountNumber, createTransactionRequest));
    }

    @GetMapping(value = "/{accountNumber}")
    public ResponseEntity<GetBankAccountDetailResponse> getBankAccountDetail(@PathVariable("accountNumber") String accountNumber){
        return ResponseEntity.ok(this.bankAccountService.getBankAccountDetails(accountNumber));
    }
}
