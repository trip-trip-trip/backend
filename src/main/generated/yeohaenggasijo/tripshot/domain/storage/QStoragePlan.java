package yeohaenggasijo.tripshot.domain.storage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStoragePlan is a Querydsl query type for StoragePlan
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoragePlan extends EntityPathBase<StoragePlan> {

    private static final long serialVersionUID = -519424513L;

    public static final QStoragePlan storagePlan = new QStoragePlan("storagePlan");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> quotaMb = createNumber("quotaMb", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStoragePlan(String variable) {
        super(StoragePlan.class, forVariable(variable));
    }

    public QStoragePlan(Path<? extends StoragePlan> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStoragePlan(PathMetadata metadata) {
        super(StoragePlan.class, metadata);
    }

}

