package io.github.nosibi.accounttransfer.log;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferLogService {
    private final TransferLogRepository transferLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSuccessLog(String fromAccountNumber, String toAccountNumber, Long amount){
        TransferLog transferLog = new TransferLog(fromAccountNumber, toAccountNumber, amount, TransferStatus.SUCCESS);
        transferLogRepository.save(transferLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailLog(String fromAccountNumber, String toAccountNumber, Long amount){
        TransferLog transferLog = new TransferLog(fromAccountNumber, toAccountNumber, amount, TransferStatus.FAIL);
        transferLogRepository.save(transferLog);
    }

    //테스트용 메서드(각 테스트 종료시 저장된 로그 전체 삭제)
    public void deleteAllLogs(){
        transferLogRepository.deleteAll();
    }

    public List<TransferLog> transferLogList(
            String fromAccountNumber,
            String toAccountNumber,
            Long amount,
            TransferStatus status,
            LocalDateTime fromDate,
            LocalDateTime toDate){
        TransferLogSearchCond cond =
                new TransferLogSearchCond(fromAccountNumber, toAccountNumber, amount, status, fromDate, toDate);
        return transferLogRepository.findAll(cond);
    }
}
