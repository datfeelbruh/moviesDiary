package sobad.code.movies_diary.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovieRating is a Querydsl query type for MovieRating
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovieRating extends EntityPathBase<MovieRating> {

    private static final long serialVersionUID = -1391962229L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMovieRating movieRating = new QMovieRating("movieRating");

    public final QBaseModel _super = new QBaseModel(this);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final QMovie movie;

    public final NumberPath<Double> rating = createNumber("rating", Double.class);

    public final QUser user;

    public QMovieRating(String variable) {
        this(MovieRating.class, forVariable(variable), INITS);
    }

    public QMovieRating(Path<? extends MovieRating> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMovieRating(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMovieRating(PathMetadata metadata, PathInits inits) {
        this(MovieRating.class, metadata, inits);
    }

    public QMovieRating(Class<? extends MovieRating> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.movie = inits.isInitialized("movie") ? new QMovie(forProperty("movie")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

