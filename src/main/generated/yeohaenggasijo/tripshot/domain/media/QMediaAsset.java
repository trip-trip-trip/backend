package yeohaenggasijo.tripshot.domain.media;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMediaAsset is a Querydsl query type for MediaAsset
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMediaAsset extends EntityPathBase<MediaAsset> {

    private static final long serialVersionUID = -634009656L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMediaAsset mediaAsset = new QMediaAsset("mediaAsset");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.CaptureType> captureType = createEnum("captureType", yeohaenggasijo.tripshot.domain.common.CaptureType.class);

    public final StringPath comment = createString("comment");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> durationSec = createNumber("durationSec", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> expiration = createDateTime("expiration", java.time.LocalDateTime.class);

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isSharedInAlbum = createBoolean("isSharedInAlbum");

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.MediaKind> mediaKind = createEnum("mediaKind", yeohaenggasijo.tripshot.domain.common.MediaKind.class);

    public final DateTimePath<java.time.LocalDateTime> takenAt = createDateTime("takenAt", java.time.LocalDateTime.class);

    public final StringPath thumbnailUrl = createString("thumbnailUrl");

    public final yeohaenggasijo.tripshot.domain.trip.QTrip trip;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final yeohaenggasijo.tripshot.domain.user.QUser uploader;

    public final StringPath url = createString("url");

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public QMediaAsset(String variable) {
        this(MediaAsset.class, forVariable(variable), INITS);
    }

    public QMediaAsset(Path<? extends MediaAsset> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMediaAsset(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMediaAsset(PathMetadata metadata, PathInits inits) {
        this(MediaAsset.class, metadata, inits);
    }

    public QMediaAsset(Class<? extends MediaAsset> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.trip = inits.isInitialized("trip") ? new yeohaenggasijo.tripshot.domain.trip.QTrip(forProperty("trip"), inits.get("trip")) : null;
        this.uploader = inits.isInitialized("uploader") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("uploader")) : null;
    }

}

