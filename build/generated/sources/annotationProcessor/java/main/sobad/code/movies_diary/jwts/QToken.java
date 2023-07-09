package sobad.code.movies_diary.jwts;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QToken is a Querydsl query type for Token
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QToken extends EntityPathBase<Token> {

    private static final long serialVersionUID = 1706190114L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QToken token1 = new QToken("token1");

    public final sobad.code.movies_diary.entities.QBaseModel _super = new sobad.code.movies_diary.entities.QBaseModel(this);

    public final BooleanPath expired = createBoolean("expired");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath revoked = createBoolean("revoked");

    public final StringPath token = createString("token");

    public final sobad.code.movies_diary.entities.QUser user;

    public QToken(String variable) {
        this(Token.class, forVariable(variable), INITS);
    }

    public QToken(Path<? extends Token> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QToken(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QToken(PathMetadata metadata, PathInits inits) {
        this(Token.class, metadata, inits);
    }

    public QToken(Class<? extends Token> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new sobad.code.movies_diary.entities.QUser(forProperty("user")) : null;
    }

}

