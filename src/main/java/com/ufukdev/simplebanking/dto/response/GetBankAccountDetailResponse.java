package com.ufukdev.simplebanking.dto.response;

import com.ufukdev.simplebanking.dto.model.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Builder
public class GetBankAccountDetailResponse {
    private String accountNumber;
    private String owner;
    private BigDecimal balance;
    private String createdDate;
    private List<Transaction> transactions;
}
