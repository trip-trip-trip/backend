package yeohaenggasijo.tripshot.domain.scrapbook;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QScrapbook is a Querydsl query type for Scrapbook
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScrapbook extends EntityPathBase<Scrapbook> {

    private static final long serialVersionUID = 1069924404L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScrapbook scrapbook = new QScrapbook("scrapbook");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    public final yeohaenggasijo.tripshot.domain.media.QMediaAsset coverMedia;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final yeohaenggasijo.tripshot.domain.user.QUser creator;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.ScrapbookRenderStatus> renderStatus = createEnum("renderStatus", yeohaenggasijo.tripshot.domain.common.ScrapbookRenderStatus.class);

    public final QScrapbookTemplate template;

    public final StringPath title = createString("title");

    public final yeohaenggasijo.tripshot.domain.trip.QTrip trip;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.ScrapbookVisibility> visibility = createEnum("visibility", yeohaenggasijo.tripshot.domain.common.ScrapbookVisibility.class);

    public QScrapbook(String variable) {
        this(Scrapbook.class, forVariable(variable), INITS);
    }

    public QScrapbook(Path<? extends Scrapbook> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QScrapbook(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QScrapbook(PathMetadata metadata, PathInits inits) {
        this(Scrapbook.class, metadata, inits);
    }

    public QScrapbook(Class<? extends Scrapbook> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coverMedia = inits.isInitialized("coverMedia") ? new yeohaenggasijo.tripshot.domain.media.QMediaAsset(forProperty("coverMedia"), inits.get("coverMedia")) : null;
        this.creator = inits.isInitialized("creator") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("creator")) : null;
        this.template = inits.isInitialized("template") ? new QScrapbookTemplate(forProperty("template")) : null;
        this.trip = inits.isInitialized("trip") ? new yeohaenggasijo.tripshot.domain.trip.QTrip(forProperty("trip"), inits.get("trip")) : null;
    }

}

