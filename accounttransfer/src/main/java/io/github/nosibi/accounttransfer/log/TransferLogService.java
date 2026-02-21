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
