package yeohaenggasijo.tripshot.domain.notify;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationJob is a Querydsl query type for NotificationJob
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationJob extends EntityPathBase<NotificationJob> {

    private static final long serialVersionUID = 2144511615L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationJob notificationJob = new QNotificationJob("notificationJob");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath errorMessage = createString("errorMessage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> scheduledAt = createDateTime("scheduledAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> sentAt = createDateTime("sentAt", java.time.LocalDateTime.class);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.SlotCode> slotCode = createEnum("slotCode", yeohaenggasijo.tripshot.domain.common.SlotCode.class);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.NotificationStatus> status = createEnum("status", yeohaenggasijo.tripshot.domain.common.NotificationStatus.class);

    public final yeohaenggasijo.tripshot.domain.trip.QTrip trip;

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.NotificationType> type = createEnum("type", yeohaenggasijo.tripshot.domain.common.NotificationType.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final yeohaenggasijo.tripshot.domain.user.QUser user;

    public final NumberPath<Integer> weight = createNumber("weight", Integer.class);

    public QNotificationJob(String variable) {
        this(NotificationJob.class, forVariable(variable), INITS);
    }

    public QNotificationJob(Path<? extends NotificationJob> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationJob(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationJob(PathMetadata metadata, PathInits inits) {
        this(NotificationJob.class, metadata, inits);
    }

    public QNotificationJob(Class<? extends NotificationJob> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.trip = inits.isInitialized("trip") ? new yeohaenggasijo.tripshot.domain.trip.QTrip(forProperty("trip"), inits.get("trip")) : null;
        this.user = inits.isInitialized("user") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("user")) : null;
    }

}

