package yeohaenggasijo.tripshot.domain.reel;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QShortReelItem is a Querydsl query type for ShortReelItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QShortReelItem extends EntityPathBase<ShortReelItem> {

    private static final long serialVersionUID = 18689383L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QShortReelItem shortReelItem = new QShortReelItem("shortReelItem");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> holdMs = createNumber("holdMs", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final yeohaenggasijo.tripshot.domain.media.QMediaAsset media;

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final QShortReel reel;

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.ReelTransition> transition = createEnum("transition", yeohaenggasijo.tripshot.domain.common.ReelTransition.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QShortReelItem(String variable) {
        this(ShortReelItem.class, forVariable(variable), INITS);
    }

    public QShortReelItem(Path<? extends ShortReelItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QShortReelItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QShortReelItem(PathMetadata metadata, PathInits inits) {
        this(ShortReelItem.class, metadata, inits);
    }

    public QShortReelItem(Class<? extends ShortReelItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.media = inits.isInitialized("media") ? new yeohaenggasijo.tripshot.domain.media.QMediaAsset(forProperty("media"), inits.get("media")) : null;
        this.reel = inits.isInitialized("reel") ? new QShortReel(forProperty("reel"), inits.get("reel")) : null;
    }

}

