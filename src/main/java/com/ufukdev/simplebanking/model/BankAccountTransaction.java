package com.ufukdev.simplebanking.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "bank_account_transaction")
public class BankAccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "bank_account_id")
    private UUID bankAccountId;

    @Column(name = "transaction_id")
    private UUID transactionId;
}
