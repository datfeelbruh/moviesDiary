package sobad.code.moviesdiary.repositories;

import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.QMovie;
import sobad.code.moviesdiary.entities.QReview;
import sobad.code.moviesdiary.entities.QUser;
import sobad.code.moviesdiary.repositories.filters.GenreFilter;
import sobad.code.moviesdiary.repositories.filters.UserIdFilter;

import static sobad.code.moviesdiary.entities.QMovie.movie;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MovieCustomRepositoryImpl {
    private final EntityManager entityManager;
    public PageImpl<Movie> searchBy(String text, PageRequest pageRequest, String... fields) {
        SearchResult<Movie> result = getSearchResult(text, pageRequest, fields);
        long total = result.total().hitCount();
        List<Movie> res = result.hits();
        return new PageImpl<>(res, pageRequest, total);
    }

    private SearchResult<Movie> getSearchResult(String text, PageRequest pageRequest, String... fields) {
        SearchSession searchSession = Search.session(entityManager);

        return searchSession
                .search(Movie.class)
                .where(f -> f.match().fields(fields).matching(text))
                .fetch((int) pageRequest.getOffset(), pageRequest.getPageSize());
    }

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
        JPAQuery<Movie> query = new JPAQuery<>(entityManager)
                .select(movie)
                .from(movie)
                .where(movie.genres.any().name.toLowerCase().contains(filter.getGenreName().toLowerCase()));

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Movie> movies = query.fetch();

        return new PageImpl<>(movies, pageable, total);
    }

    public PageImpl<Movie> findByUserIdFilter(UserIdFilter filter, Pageable pageable) {
        QUser user = QUser.user;
        QReview review = QReview.review;

        JPAQuery<Movie> query = new JPAQuery<>(entityManager)
                .select(movie)
                .from(movie)
                .join(review).on(movie.id.eq(review.movie.id))
                .join(user).on(user.id.eq(review.user.id))
                .where(review.user.id.eq(filter.getUserId()))
                .orderBy(review.id.desc());

        long total = query.fetchCount();
        query.limit(pageable.getPageSize());
        query.offset(pageable.getOffset());
        List<Movie> movies = query.fetch();

        return new PageImpl<>(movies, pageable, total);
    }
}
