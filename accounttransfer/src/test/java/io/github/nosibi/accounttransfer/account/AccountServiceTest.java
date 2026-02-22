package io.github.nosibi.accounttransfer.account;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

/**
 * 계좌 관리
 * 테스트 케이스
 * 1. 계좌 생성 후 조회한 계좌가 입력 데이터와 일치하는지 검증
 * 2. 계좌 해지 후 조회한 계좌가 해지 상태인지 검증
 * 3. 해지된 계좌를 해지 및 정지, 비밀번호 변경시 예외 발생하는지 검증
 * 4. 계좌 상태 변경 검증(해지, 정지, 활성화)
 * 5. 계좌 입금 및 인출 후 잔고 검증
 */
@SpringBootTest
@Slf4j
class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    String accountNumber = "123-456-78901";
    String password = "qwe!@#123";

    @BeforeEach
    void openAccount(){
        accountService.openAccount(accountNumber, password);
    }

    @AfterEach
    void deleteAccounts(){
        accountService.deleteAllAccount();
    }

    @Test
    void openAccountTest() {

        Account findAccount = accountService.findAccount(accountNumber);
        assertThat(findAccount.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(findAccount.getPassword()).isEqualTo(password);
    }

    @Test
    void closeAccountTest(){
        accountService.closeAccount(accountNumber, password);
        Account findAccount = accountService.findAccount(accountNumber);
        assertThat(findAccount.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    void closedAccountTest(){
        String newPassword = "asd!@#456";
        accountService.closeAccount(accountNumber, password);
        assertThatThrownBy(()->accountService.closeAccount(accountNumber, password)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->accountService.freezeAccount(accountNumber, password)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(()->accountService.changePassword(accountNumber,password, newPassword)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void statusTest(){
        accountService.closeAccount(accountNumber, password);
        assertThat(accountService.findAccount(accountNumber).getStatus()).isEqualTo(Status.CLOSED);
        accountService.ActivateAccount(accountNumber, password);
        assertThat(accountService.findAccount(accountNumber).getStatus()).isEqualTo(Status.ACTIVE);
        accountService.freezeAccount(accountNumber, password);
        assertThat(accountService.findAccount(accountNumber).getStatus()).isEqualTo(Status.FREEZE);
        accountService.ActivateAccount(accountNumber, password);
        assertThat(accountService.findAccount(accountNumber).getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    void balanceCheckTest(){
        accountService.depositIntoAccount(accountNumber,password,10000L);
        assertThat(accountService.checkAccountBalance(accountNumber,password)).isEqualTo(10000L);
        accountService.withdrawFromAccount(accountNumber,password,10000L);
        assertThat(accountService.checkAccountBalance(accountNumber,password)).isEqualTo(0L);
    }

}