package io.github.nosibi.accounttransfer.transfer;

import io.github.nosibi.accounttransfer.account.Account;
import io.github.nosibi.accounttransfer.account.AccountRepository;
import io.github.nosibi.accounttransfer.log.TransferLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 계좌 이체, 이체 후 로그 기록 저장
 * 외부 트랜잭션과 내부 트랜잭션의 물리 트랜잭션 분리 구현
 * 계좌 이체와 로그 기록 트랜잭션을 물리적으로 분리
 * 계좌 이체 간 예외 발생 시 계좌 이체는 롤백되어 실패
 * 로그 기록은 예외 발생 유무에 상관없이 저장(상태만 다르게 저장)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final AccountRepository accountRepository;
    private final TransferLogService transferLogService;

    @Transactional
    public void transfer(String fromAccountNumber, String fromPassword, String toAccountNumber, Long amount){
        try{
            Account fromAccount = findAccount(fromAccountNumber);
            fromAccount.validatePassword(fromPassword);
            Account toAccount = findAccount(toAccountNumber);

            fromAccount.withdraw(amount);
            toAccount.deposit(amount);

            transferLogService.saveSuccessLog(fromAccountNumber, toAccountNumber, amount);
        }catch (RuntimeException e){
            transferLogService.saveFailLog(fromAccountNumber, toAccountNumber, amount);
            throw e; //예외를 전가해야 계좌이체 트랜잭션이 롤백됨
        }
    }

    private Account findAccount(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalStateException("계좌가 존재하지 않습니다."));
    }
}
