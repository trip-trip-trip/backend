package yeohaenggasijo.tripshot.domain.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserStorage is a Querydsl query type for UserStorage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserStorage extends EntityPathBase<UserStorage> {

    private static final long serialVersionUID = 2115096043L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserStorage userStorage = new QUserStorage("userStorage");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final QStoragePlan plan;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> usage = createNumber("usage", Integer.class);

    public final yeohaenggasijo.tripshot.domain.user.QUser user;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserStorage(String variable) {
        this(UserStorage.class, forVariable(variable), INITS);
    }

    public QUserStorage(Path<? extends UserStorage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserStorage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserStorage(PathMetadata metadata, PathInits inits) {
        this(UserStorage.class, metadata, inits);
    }

    public QUserStorage(Class<? extends UserStorage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.plan = inits.isInitialized("plan") ? new QStoragePlan(forProperty("plan")) : null;
        this.user = inits.isInitialized("user") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("user")) : null;
    }

}

