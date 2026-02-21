package io.github.nosibi.accounttransfer.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 구현체가 하나이고 교체 가능성이 없다고 판단되므로 추상화 인터페이스 도입은 하지 않음
 * 기능 : 계좌 개설, 계좌 삭제, 잔액 조회, 계좌 비밀번호 변경, 계좌 정지
 * 트랜잭션 적용으로 변경감지로 인한 저장 가능
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public void openAccount(String accountNumber, String password) {
        Account account = new Account(accountNumber, password);
        accountRepository.save(account);
    }

    public void closeAccount(String accountNumber, String password){
        Account findAccount = findAccount(accountNumber, password);
        findAccount.delete();
    }

    @Transactional(readOnly = true)
    public Long checkAccountBalance(String accountNumber, String password){
        Account findAccount = findAccount(accountNumber, password);
        findAccount.validateActive();
        return findAccount.getBalance();
    }

    public void changePassword(String accountNumber, String oldPassword, String newPassword){
        Account findAccount = findAccount(accountNumber, oldPassword);
        findAccount.changePassword(oldPassword, newPassword);
    }

    public void freezeAccount(String accountNumber, String password){
        Account findAccount = findAccount(accountNumber, password);
        findAccount.freeze();
    }

    private Account findAccount(String accountNumber, String password){
        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalStateException("계좌가 존재하지 않습니다."));
        findAccount.validatePassword(password);
        return findAccount;
    }
}
