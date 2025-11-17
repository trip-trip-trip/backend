package yeohaenggasijo.tripshot.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFriendship is a Querydsl query type for Friendship
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFriendship extends EntityPathBase<Friendship> {

    private static final long serialVersionUID = -332187701L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFriendship friendship = new QFriendship("friendship");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    public final QUser addressee;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QUser requester;

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.FriendshipStatus> status = createEnum("status", yeohaenggasijo.tripshot.domain.common.FriendshipStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QFriendship(String variable) {
        this(Friendship.class, forVariable(variable), INITS);
    }

    public QFriendship(Path<? extends Friendship> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFriendship(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFriendship(PathMetadata metadata, PathInits inits) {
        this(Friendship.class, metadata, inits);
    }

    public QFriendship(Class<? extends Friendship> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.addressee = inits.isInitialized("addressee") ? new QUser(forProperty("addressee")) : null;
        this.requester = inits.isInitialized("requester") ? new QUser(forProperty("requester")) : null;
    }

}

