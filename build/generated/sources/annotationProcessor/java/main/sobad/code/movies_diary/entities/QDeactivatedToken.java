package sobad.code.movies_diary.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeactivatedToken is a Querydsl query type for DeactivatedToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeactivatedToken extends EntityPathBase<DeactivatedToken> {

    private static final long serialVersionUID = -2097027701L;

    public static final QDeactivatedToken deactivatedToken = new QDeactivatedToken("deactivatedToken");

    public final QBaseModel _super = new QBaseModel(this);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.util.Date> timestamp = createDateTime("timestamp", java.util.Date.class);

    public final StringPath token = createString("token");

    public QDeactivatedToken(String variable) {
        super(DeactivatedToken.class, forVariable(variable));
    }

    public QDeactivatedToken(Path<? extends DeactivatedToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeactivatedToken(PathMetadata metadata) {
        super(DeactivatedToken.class, metadata);
    }

}

