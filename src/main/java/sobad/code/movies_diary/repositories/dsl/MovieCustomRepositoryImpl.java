package sobad.code.movies_diary.repositories.dsl;

import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.QReview;
import sobad.code.movies_diary.entities.QUser;
import sobad.code.movies_diary.repositories.dsl.filters.MovieGenreFilter;
import sobad.code.movies_diary.repositories.dsl.filters.MovieNameFilter;
import sobad.code.movies_diary.repositories.dsl.filters.MovieUserIdFilter;

import static sobad.code.movies_diary.entities.QMovie.movie;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieCustomRepositoryImpl implements MovieCustomRepository {
    private final EntityManager entityManager;
    @Override
    public List<Movie> findByFilter(MovieGenreFilter filter) {
        return new JPAQuery<Movie>(entityManager)
                .select(movie)
                .from(movie)
                .where(movie.genres.any().name.toLowerCase().contains(filter.getGenreName().toLowerCase()))
                .fetch();
    }

    public List<Movie> findByMovieNameFilter(MovieNameFilter filter) {
        return new JPAQuery<Movie>(entityManager)
                .select(movie)
                .from(movie)
                .where(movie.movieName.toLowerCase().contains(filter.getMovieName().toLowerCase()))
                .fetch();
    }

    public List<Movie> findByMovieUserIdFilter(MovieUserIdFilter filter) {
        QUser user = QUser.user;
        QReview review = QReview.review1;
        return new JPAQuery<Movie>(entityManager)
                .from(movie)
                .join(review).on(movie.id.eq(review.movie.id))
                .join(user).on(user.id.eq(review.user.id))
                .where(review.user.id.eq(filter.getUserId()))
                .fetch();
    }
}
