package com.ufukdev.simplebanking.controller;


import com.ufukdev.simplebanking.dto.request.CreateBankAccountRequest;
import com.ufukdev.simplebanking.dto.response.CreateBankAccountResponse;
import com.ufukdev.simplebanking.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/account/v1")
public class AccountController {
    private final BankAccountService bankAccountService;

    public AccountController(BankAccountService bankAccountService){
        this.bankAccountService = bankAccountService;
    }

    @PostMapping(value = "/createBankAccount")
    public ResponseEntity<CreateBankAccountResponse> createBankAccount(@RequestBody CreateBankAccountRequest createBankAccountRequest) {
        return ResponseEntity.ok(this.bankAccountService.createBankAccount(createBankAccountRequest));
    }
}
