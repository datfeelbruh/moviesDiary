package sobad.code.movies_diary.entities;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMovie is a Querydsl query type for Movie
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMovie extends EntityPathBase<Movie> {

    private static final long serialVersionUID = 1603841102L;

    public static final QMovie movie = new QMovie("movie");

    public final StringPath description = createString("description");

    public final SetPath<Genre, QGenre> genres = this.<Genre, QGenre>createSet("genres", Genre.class, QGenre.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Double> imdbRating = createNumber("imdbRating", Double.class);

    public final NumberPath<Double> kpRating = createNumber("kpRating", Double.class);

    public final StringPath movieName = createString("movieName");

    public final StringPath posterUrl = createString("posterUrl");

    public final NumberPath<Integer> releaseYear = createNumber("releaseYear", Integer.class);

    public QMovie(String variable) {
        super(Movie.class, forVariable(variable));
    }

    public QMovie(Path<? extends Movie> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMovie(PathMetadata metadata) {
        super(Movie.class, metadata);
    }

}

