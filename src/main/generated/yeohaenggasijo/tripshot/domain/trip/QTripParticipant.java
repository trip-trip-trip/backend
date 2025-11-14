package yeohaenggasijo.tripshot.domain.trip;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTripParticipant is a Querydsl query type for TripParticipant
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTripParticipant extends EntityPathBase<TripParticipant> {

    private static final long serialVersionUID = -715793705L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTripParticipant tripParticipant = new QTripParticipant("tripParticipant");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.TripParticipantRole> role = createEnum("role", yeohaenggasijo.tripshot.domain.common.TripParticipantRole.class);

    public final QTrip trip;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final yeohaenggasijo.tripshot.domain.user.QUser user;

    public QTripParticipant(String variable) {
        this(TripParticipant.class, forVariable(variable), INITS);
    }

    public QTripParticipant(Path<? extends TripParticipant> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTripParticipant(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTripParticipant(PathMetadata metadata, PathInits inits) {
        this(TripParticipant.class, metadata, inits);
    }

    public QTripParticipant(Class<? extends TripParticipant> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.trip = inits.isInitialized("trip") ? new QTrip(forProperty("trip"), inits.get("trip")) : null;
        this.user = inits.isInitialized("user") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("user")) : null;
    }

}

