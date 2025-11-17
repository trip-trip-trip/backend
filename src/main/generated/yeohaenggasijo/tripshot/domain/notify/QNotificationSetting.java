package yeohaenggasijo.tripshot.domain.notify;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNotificationSetting is a Querydsl query type for NotificationSetting
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNotificationSetting extends EntityPathBase<NotificationSetting> {

    private static final long serialVersionUID = 1028188178L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QNotificationSetting notificationSetting = new QNotificationSetting("notificationSetting");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final BooleanPath momentEnabled = createBoolean("momentEnabled");

    public final NumberPath<Integer> timesPerDay = createNumber("timesPerDay", Integer.class);

    public final StringPath timezone = createString("timezone");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final yeohaenggasijo.tripshot.domain.user.QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QNotificationSetting(String variable) {
        this(NotificationSetting.class, forVariable(variable), INITS);
    }

    public QNotificationSetting(Path<? extends NotificationSetting> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QNotificationSetting(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QNotificationSetting(PathMetadata metadata, PathInits inits) {
        this(NotificationSetting.class, metadata, inits);
    }

    public QNotificationSetting(Class<? extends NotificationSetting> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("user")) : null;
    }

}

