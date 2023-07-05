package sobad.code.movies_diary.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 606167789L;

    public static final QUser user = new QUser("user");

    public final QBaseModel _super = new QBaseModel(this);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final SetPath<Movie, QMovie> movies = this.<Movie, QMovie>createSet("movies", Movie.class, QMovie.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final CollectionPath<Role, QRole> roles = this.<Role, QRole>createCollection("roles", Role.class, QRole.class, PathInits.DIRECT2);

    public final ListPath<MovieRating, QMovieRating> userMovieRatings = this.<MovieRating, QMovieRating>createList("userMovieRatings", MovieRating.class, QMovieRating.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

