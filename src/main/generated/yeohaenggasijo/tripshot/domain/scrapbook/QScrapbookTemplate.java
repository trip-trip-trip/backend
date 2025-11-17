package yeohaenggasijo.tripshot.domain.scrapbook;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QScrapbookTemplate is a Querydsl query type for ScrapbookTemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QScrapbookTemplate extends EntityPathBase<ScrapbookTemplate> {

    private static final long serialVersionUID = -788431666L;

    public static final QScrapbookTemplate scrapbookTemplate = new QScrapbookTemplate("scrapbookTemplate");

    public final yeohaenggasijo.tripshot.domain.base.QBaseEntity _super = new yeohaenggasijo.tripshot.domain.base.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath layoutJson = createString("layoutJson");

    public final NumberPath<Integer> maxMediaCount = createNumber("maxMediaCount", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath previewUrl = createString("previewUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QScrapbookTemplate(String variable) {
        super(ScrapbookTemplate.class, forVariable(variable));
    }

    public QScrapbookTemplate(Path<? extends ScrapbookTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScrapbookTemplate(PathMetadata metadata) {
        super(ScrapbookTemplate.class, metadata);
    }

}

