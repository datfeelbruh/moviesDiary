package sobad.code.movies_diary.repositories.dsl;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.QReview;
import sobad.code.movies_diary.entities.QUser;
import sobad.code.movies_diary.repositories.dsl.filters.GenreFilter;
import sobad.code.movies_diary.repositories.dsl.filters.TitleFilter;
import sobad.code.movies_diary.repositories.dsl.filters.UserIdFilter;

import static sobad.code.movies_diary.entities.QMovie.movie;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MovieCustomRepositoryImpl {
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    public PageImpl<Movie> findByGenreNameFilter(GenreFilter filter, Pageable pageable) {
        BlazeJPAQuery<Movie> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(movie)
                .from(movie)
                .where(movie.genres.any().name.toLowerCase().contains(filter.getGenreName().toLowerCase()));

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Movie> movies = query.fetch();

        return new PageImpl<>(movies, pageable, total);
    }

    public PageImpl<Movie> findByTitleFilter(TitleFilter filter, Pageable pageable) {
        BlazeJPAQuery<Movie> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(movie)
                .from(movie)
                .where(movie.title.toLowerCase().contains(filter.getMovieName().toLowerCase()));

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Movie> movies = query.fetch();

        return new PageImpl<>(movies, pageable, total);
    }

    public PageImpl<Movie> findByUserIdFilter(UserIdFilter filter, Pageable pageable) {
        QUser user = QUser.user;
        QReview review = QReview.review1;

        BlazeJPAQuery<Movie> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(movie)
                .from(movie)
                .join(review).on(movie.id.eq(review.movie.id))
                .join(user).on(user.id.eq(review.user.id))
                .where(review.user.id.eq(filter.getUserId()));

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Movie> movies = query.fetch();

        return new PageImpl<>(movies, pageable, total);
    }
}
