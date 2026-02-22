package io.github.nosibi.accounttransfer.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 구현체가 하나이고 교체 가능성이 없다고 판단되므로 추상화 인터페이스 도입은 하지 않음
 * 기능 : 계좌 조회, 계좌 개설, 계좌 정지, 입금, 출금, 잔액 조회, 계좌 비밀번호 변경, 계좌 정지, 계좌 활성화
 * 트랜잭션 적용으로 변경감지로 인한 자동 DB
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public Account findAccount(String accountNumber){
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()->new IllegalStateException("존재하지 않는 계좌입니다."));
    }

    public void openAccount(String accountNumber, String password) {
        Account account = new Account(accountNumber, password);
        accountRepository.save(account);
    }

    public void closeAccount(String accountNumber, String password){
        Account findAccount = findAccount(accountNumber, password);
        findAccount.delete();
    }

    public void depositIntoAccount(String accountNumber, String password, Long amount) {
        findAccount(accountNumber, password).deposit(amount); //변경감지로 자동으로 DB에 반영
    }

    public void withdrawFromAccount(String accountNumber, String password, Long amount) {
        findAccount(accountNumber, password).withdraw(amount); //변경감지로 자동으로 DB에 반영
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

    public void ActivateAccount(String accountNumber, String password){
        Account findAccount = findAccount(accountNumber, password);
        findAccount.activate();
    }

    //테스트용 메서드(각 테스트 종료시 저장된 계좌 전체 삭제)
    public void deleteAllAccount(){
        accountRepository.deleteAll();
    }

    private Account findAccount(String accountNumber, String password){
        Account findAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalStateException("계좌가 존재하지 않습니다."));
        findAccount.validatePassword(password);
        return findAccount;
    }
}
