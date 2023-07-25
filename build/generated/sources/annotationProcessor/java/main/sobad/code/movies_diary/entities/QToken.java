package sobad.code.movies_diary.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QToken is a Querydsl query type for Token
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QToken extends EntityPathBase<Token> {

    private static final long serialVersionUID = 1610295063L;

    public static final QToken token = new QToken("token");

    public final QBaseModel _super = new QBaseModel(this);

    public final StringPath accessToken = createString("accessToken");

    public final StringPath accessTokenExpiry = createString("accessTokenExpiry");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public QToken(String variable) {
        super(Token.class, forVariable(variable));
    }

    public QToken(Path<? extends Token> path) {
        super(path.getType(), path.getMetadata());
    }

    public QToken(PathMetadata metadata) {
        super(Token.class, metadata);
    }

}

