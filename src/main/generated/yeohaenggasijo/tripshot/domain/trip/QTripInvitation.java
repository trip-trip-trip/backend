package yeohaenggasijo.tripshot.domain.trip;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTripInvitation is a Querydsl query type for TripInvitation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTripInvitation extends EntityPathBase<TripInvitation> {

    private static final long serialVersionUID = 1424590709L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTripInvitation tripInvitation = new QTripInvitation("tripInvitation");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final yeohaenggasijo.tripshot.domain.user.QUser invitee;

    public final yeohaenggasijo.tripshot.domain.user.QUser inviter;

    public final DateTimePath<java.time.LocalDateTime> respondedAt = createDateTime("respondedAt", java.time.LocalDateTime.class);

    public final EnumPath<yeohaenggasijo.tripshot.domain.common.InvitationStatus> status = createEnum("status", yeohaenggasijo.tripshot.domain.common.InvitationStatus.class);

    public final QTrip trip;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTripInvitation(String variable) {
        this(TripInvitation.class, forVariable(variable), INITS);
    }

    public QTripInvitation(Path<? extends TripInvitation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTripInvitation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTripInvitation(PathMetadata metadata, PathInits inits) {
        this(TripInvitation.class, metadata, inits);
    }

    public QTripInvitation(Class<? extends TripInvitation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.invitee = inits.isInitialized("invitee") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("invitee")) : null;
        this.inviter = inits.isInitialized("inviter") ? new yeohaenggasijo.tripshot.domain.user.QUser(forProperty("inviter")) : null;
        this.trip = inits.isInitialized("trip") ? new QTrip(forProperty("trip"), inits.get("trip")) : null;
    }

}

