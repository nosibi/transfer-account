package io.github.nosibi.accounttransfer.log;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class TransferLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String fromAccountNumber;
    @Column
    private String toAccountNumber;
    @Column
    private Long amount;
    @Column
    @Enumerated(EnumType.STRING)
    private TransferStatus status;
    @Column
    private LocalDateTime transferredAt;

    public TransferLog(String fromAccountNumber, String toAccountNumber, Long amount, TransferStatus status) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.status = status;
        this.transferredAt = LocalDateTime.now();
    }
}