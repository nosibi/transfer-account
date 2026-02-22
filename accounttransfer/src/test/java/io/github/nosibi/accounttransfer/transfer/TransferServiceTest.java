package io.github.nosibi.accounttransfer.transfer;

import io.github.nosibi.accounttransfer.account.AccountService;
import io.github.nosibi.accounttransfer.log.TransferLog;
import io.github.nosibi.accounttransfer.log.TransferLogService;
import io.github.nosibi.accounttransfer.log.TransferStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * 계좌이체
 * 테스트 케이스
 * 1. 계좌이체 후 잔고가 송금액만큼 증가/감소했는지 검증(보낸쪽은 잔고가 0원, 받은쪽은 잔고가 10000원)
 * 2. 계좌이체 후 이체 성공 로그 확인
 * 3. 계좌이체 간 예외 발생 시 잔고가 변동하지 않고 실패 로그는 저장되는지 확인
 */
@SpringBootTest
@Slf4j
class TransferServiceTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private TransferLogService transferLogService;

    String fromAccountNumber = "123-456-78901";
    String fromAccountPassword = "qwe!@#123";
    String successToAccountNumber = "111-111-11111";
    String failToAccountNumber = "000-000-00000"; //예외 발생하는 조건
    String toAccountPassword = "asd!@#456";


    @BeforeEach
    void openAccountForTransfer(){
        accountService.openAccount(fromAccountNumber, fromAccountPassword);
        accountService.depositIntoAccount(fromAccountNumber,fromAccountPassword,10000L);
        accountService.openAccount(successToAccountNumber, toAccountPassword);
    }

    @AfterEach
    void deleteAccounts(){
        accountService.deleteAllAccount();
        transferLogService.deleteAllLogs();
    }

    @Test
    void transferTest(){
        transferService.transfer(fromAccountNumber,fromAccountPassword, successToAccountNumber, 10000L);
        assertThat(accountService.checkAccountBalance(fromAccountNumber,fromAccountPassword)).isEqualTo(0L);
        assertThat(accountService.checkAccountBalance(successToAccountNumber,toAccountPassword)).isEqualTo(10000L);
    }

    @Test
    void successLogTest(){
        transferService.transfer(fromAccountNumber,fromAccountPassword, successToAccountNumber, 10000L);
        List<TransferLog> transferLogs = transferLogService.transferLogList(
                fromAccountNumber,
                successToAccountNumber,
                null,
                null,
                null,
                null);
        assertThat(transferLogs.get(0).getStatus()).isEqualTo(TransferStatus.SUCCESS);
    }

    @Test
    void failLogTest(){
        assertThatThrownBy(
                ()->transferService.transfer(fromAccountNumber,fromAccountPassword, failToAccountNumber, 10000L))
                .isInstanceOf(RuntimeException.class);

        assertThat(accountService.checkAccountBalance(fromAccountNumber,fromAccountPassword)).isEqualTo(10000L);
        assertThat(accountService.checkAccountBalance(failToAccountNumber,toAccountPassword)).isEqualTo(0L);
        assertThat(transferLogService
                .transferLogList(fromAccountNumber, failToAccountNumber,null,null,null,null).get(0).getStatus())
                .isEqualTo(TransferStatus.FAIL);
    }
}