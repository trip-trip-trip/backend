package yeohaenggasijo.tripshot.domain.reel;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShortReel is a Querydsl query type for ShortReel
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShortReel extends EntityPathBase<ShortReel> {

    private static final long serialVersionUID = -677808076L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShortReel shortReel = new QShortReel("shortReel");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final yeohaenggasijo.tripshot.domain.user.QUser creator;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final yeohaenggasijo.tripshot.domain.media.QMediaAsset outputMedia;

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.ReelRenderStatus> renderStatus = createEnum("renderStatus", yeohaenggasijo.tripshot.domain.common.ReelRenderStatus.class);

    public final StringPath title = createString("title");

    public final yeohaenggasijo.tripshot.domain.trip.QTrip trip;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QShortReel(String variable) {
        this(ShortReel.class, forVariable(variable), INITS);
    }

    public QShortReel(Path<? extends ShortReel> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShortReel(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShortReel(PathMetadata metadata, PathInits inits) {
        this(ShortReel.class, metadata, inits);
    }

    public QShortReel(Class<? extends ShortReel> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.creator = inits.isInitialized("creator") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("creator")) : null;
        this.outputMedia = inits.isInitialized("outputMedia") ? new yeohaenggasijo.tripshot.domain.media.QMediaAsset(forProperty("outputMedia"), inits.get("outputMedia")) : null;
        this.trip = inits.isInitialized("trip") ? new yeohaenggasijo.tripshot.domain.trip.QTrip(forProperty("trip"), inits.get("trip")) : null;
    }

}

