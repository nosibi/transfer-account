package io.github.nosibi.accounttransfer.log;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTransferLog is a Querydsl query type for TransferLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransferLog extends EntityPathBase<TransferLog> {

    private static final long serialVersionUID = 750661836L;

    public static final QTransferLog transferLog = new QTransferLog("transferLog");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final StringPath fromAccountNumber = createString("fromAccountNumber");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<TransferStatus> status = createEnum("status", TransferStatus.class);

    public final StringPath toAccountNumber = createString("toAccountNumber");

    public final DateTimePath<java.time.LocalDateTime> transferredAt = createDateTime("transferredAt", java.time.LocalDateTime.class);

    public QTransferLog(String variable) {
        super(TransferLog.class, forVariable(variable));
    }

    public QTransferLog(Path<? extends TransferLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTransferLog(PathMetadata metadata) {
        super(TransferLog.class, metadata);
    }

}

