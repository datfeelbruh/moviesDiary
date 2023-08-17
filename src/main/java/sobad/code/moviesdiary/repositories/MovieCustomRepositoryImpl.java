package sobad.code.moviesdiary.repositories;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.querydsl.BlazeJPAQuery;
import com.querydsl.core.types.OrderSpecifier;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.QReview;
import sobad.code.moviesdiary.entities.QUser;
import sobad.code.moviesdiary.repositories.filters.GenreFilter;
import sobad.code.moviesdiary.repositories.filters.TitleFilter;
import sobad.code.moviesdiary.repositories.filters.UserIdFilter;

import static sobad.code.moviesdiary.entities.QMovie.movie;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieCustomRepositoryImpl {
    private final EntityManager entityManager;
    private final CriteriaBuilderFactory criteriaBuilderFactory;

    public Map<Long, String> getTitlesWithId() {
        return entityManager.createQuery("""
                SELECT m.id as id, m.title as title
                FROM Movie m
                ORDER BY m.title LIMIT 20
                """, Tuple.class)
                .getResultList()
                .stream()
                .collect(
                        Collectors.toMap(
                                tuple -> ((Long) tuple.get("id")),
                                tuple -> ((String) tuple.get("title"))
                        )
                );
    }

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
        QReview review = QReview.review;
        OrderSpecifier<Long> order = review.id.desc();

        BlazeJPAQuery<Movie> query = new BlazeJPAQuery<>(entityManager, criteriaBuilderFactory)
                .select(movie)
                .from(movie)
                .join(review).on(movie.id.eq(review.movie.id))
                .join(user).on(user.id.eq(review.user.id))
                .where(review.user.id.eq(filter.getUserId()))
                .orderBy(order);

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Movie> movies = query.fetch();

        return new PageImpl<>(movies, pageable, total);
    }
}
