package yeohaenggasijo.tripshot.domain.notify;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPushDevice is a Querydsl query type for PushDevice
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPushDevice extends EntityPathBase<PushDevice> {

    private static final long serialVersionUID = 1618374211L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPushDevice pushDevice = new QPushDevice("pushDevice");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath deviceToken = createString("deviceToken");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isActive = createBoolean("isActive");

    public final DateTimePath<java.time.LocalDateTime> lastSeenAt = createDateTime("lastSeenAt", java.time.LocalDateTime.class);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.DevicePlatform> platform = createEnum("platform", yeohaenggasijo.tripshot.domain.common.DevicePlatform.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final yeohaenggasijo.tripshot.domain.user.QUser user;

    public QPushDevice(String variable) {
        this(PushDevice.class, forVariable(variable), INITS);
    }

    public QPushDevice(Path<? extends PushDevice> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPushDevice(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPushDevice(PathMetadata metadata, PathInits inits) {
        this(PushDevice.class, metadata, inits);
    }

    public QPushDevice(Class<? extends PushDevice> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("user")) : null;
    }

}

