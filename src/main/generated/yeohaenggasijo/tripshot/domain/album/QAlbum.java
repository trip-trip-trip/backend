package yeohaenggasijo.tripshot.domain.album;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAlbum is a Querydsl query type for Album
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAlbum extends EntityPathBase<Album> {

    private static final long serialVersionUID = 1705623774L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlbum album = new QAlbum("album");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    public final yeohaenggasijo.tripshot.domain.media.QMediaAsset coverMedia;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isShared = createBoolean("isShared");

    public final yeohaenggasijo.tripshot.domain.user.QUser owner;

    public final StringPath title = createString("title");

    public final yeohaenggasijo.tripshot.domain.trip.QTrip trip;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QAlbum(String variable) {
        this(Album.class, forVariable(variable), INITS);
    }

    public QAlbum(Path<? extends Album> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAlbum(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAlbum(PathMetadata metadata, PathInits inits) {
        this(Album.class, metadata, inits);
    }

    public QAlbum(Class<? extends Album> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coverMedia = inits.isInitialized("coverMedia") ? new yeohaenggasijo.tripshot.domain.media.QMediaAsset(forProperty("coverMedia"), inits.get("coverMedia")) : null;
        this.owner = inits.isInitialized("owner") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("owner")) : null;
        this.trip = inits.isInitialized("trip") ? new yeohaenggasijo.tripshot.domain.trip.QTrip(forProperty("trip"), inits.get("trip")) : null;
    }

}

