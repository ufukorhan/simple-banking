package com.ufukdev.simplebanking.enums;

public enum TransactionType {
    BILL_PAYMENT("BillPaymentTransaction"),
    DEPOSIT("DepositTransaction"),

    WITHDRAWAL("WithdrawalTransaction");


    private final String type;

    TransactionType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }
}
