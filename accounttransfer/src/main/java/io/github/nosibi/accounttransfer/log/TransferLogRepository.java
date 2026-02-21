package io.github.nosibi.accounttransfer.log;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * JPA로 기본 메서드 생성
 * Querydsl을 사용하여 검색 조건에 부합하는 로그를 찾는 동적 쿼리 구현
 */
@Repository
public class TransferLogRepository{

    private final EntityManager em;
    private final JPAQueryFactory query;

    public TransferLogRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(TransferLog transferLog){
        em.persist(transferLog);
    }


    public List<TransferLog> findAll(TransferLogSearchCond cond){
        String fromAccountNumber = cond.getFromAccountNumber();
        String toAccountNumber = cond.getToAccountNumber();
        Long amount = cond.getAmount();
        TransferStatus status = cond.getStatus();
        LocalDateTime fromDate = cond.getFromDate();
        LocalDateTime toDate = cond.getToDate();

        QTransferLog transferLog = QTransferLog.transferLog;

        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(fromAccountNumber)){
            builder.and(transferLog.fromAccountNumber.eq(fromAccountNumber));
        }

        if(StringUtils.hasText(toAccountNumber)){
            builder.and(transferLog.toAccountNumber.eq(toAccountNumber));
        }

        if(amount != null){
            builder.and(transferLog.amount.loe(amount));
        }

        if(status != null){
            builder.and(transferLog.status.eq(status));
        }

        if(fromDate != null && toDate != null){
            builder.and(transferLog.transferredAt.between(fromDate, toDate));
        } else if (fromDate != null) {
            builder.and(transferLog.transferredAt.after(fromDate));
        } else if (toDate != null) {
            builder.and(transferLog.transferredAt.before(toDate));
        }

        return query.select(transferLog).from(transferLog).where(builder).fetch();
    }
}