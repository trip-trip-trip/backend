package yeohaenggasijo.tripshot.domain.trip;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTrip is a Querydsl query type for Trip
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTrip extends EntityPathBase<Trip> {

    private static final long serialVersionUID = 2107010748L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTrip trip = new QTrip("trip");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    public final yeohaenggasijo.tripshot.domain.media.QMediaAsset coverMedia;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath inviteCode = createString("inviteCode");

    public final yeohaenggasijo.tripshot.domain.user.QUser owner;

    public final yeohaenggasijo.tripshot.domain.place.QPlace place;

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.TripStatus> status = createEnum("status", yeohaenggasijo.tripshot.domain.common.TripStatus.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.TripVisibility> visibility = createEnum("visibility", yeohaenggasijo.tripshot.domain.common.TripVisibility.class);

    public QTrip(String variable) {
        this(Trip.class, forVariable(variable), INITS);
    }

    public QTrip(Path<? extends Trip> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTrip(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTrip(PathMetadata metadata, PathInits inits) {
        this(Trip.class, metadata, inits);
    }

    public QTrip(Class<? extends Trip> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coverMedia = inits.isInitialized("coverMedia") ? new yeohaenggasijo.tripshot.domain.media.QMediaAsset(forProperty("coverMedia"), inits.get("coverMedia")) : null;
        this.owner = inits.isInitialized("owner") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("owner")) : null;
        this.place = inits.isInitialized("place") ? new yeohaenggasijo.tripshot.domain.place.QPlace(forProperty("place"), inits.get("place")) : null;
    }

}

