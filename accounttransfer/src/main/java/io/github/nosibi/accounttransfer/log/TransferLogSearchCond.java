package io.github.nosibi.accounttransfer.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 동적쿼리를 적용해보기 위한 검색 조건 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TransferLogSearchCond {
    private String fromAccountNumber;
    private String toAccountNumber;
    private Long amount;
    private TransferStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
