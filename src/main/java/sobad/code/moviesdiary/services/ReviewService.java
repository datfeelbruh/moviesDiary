package sobad.code.moviesdiary.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.moviesdiary.dtos.MessageDto;
import sobad.code.moviesdiary.dtos.review.ReviewDto;
import sobad.code.moviesdiary.dtos.review.ReviewDtoRequest;
import sobad.code.moviesdiary.dtos.pages.ReviewPages;
import sobad.code.moviesdiary.dtos.review.ReviewDtoUpdateRequest;
import sobad.code.moviesdiary.entities.Movie;
import sobad.code.moviesdiary.entities.Review;
import sobad.code.moviesdiary.entities.User;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.CustomAccessDeniedException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityAlreadyExistException;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityNotFoundException;
import sobad.code.moviesdiary.mappers.entity_serializers.ReviewSerializer;
import sobad.code.moviesdiary.repositories.MovieRepository;
import sobad.code.moviesdiary.repositories.ReviewRepository;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final MovieRepository movieRepository;
    private final ReviewSerializer reviewSerializer;

    @Transactional
    public ReviewDto createReview(ReviewDtoRequest reviewDtoRequest, HttpServletRequest request) {
        User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (reviewRepository.findAllByUserIdAndMovieId(user.getId(), reviewDtoRequest.getMovieId()).isPresent()) {
            throw new EntityAlreadyExistException("Ревью на этот фильм уже создано");
        }
        Movie movie = movieRepository.findById(reviewDtoRequest.getMovieId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Фильм с данным id '%s' не найден", reviewDtoRequest.getMovieId()))
                );

        Review review = Review.builder()
                .rating(reviewDtoRequest.getRating())
                .userReview(reviewDtoRequest.getReview())
                .movie(movie)
                .user(user)
                .build();

        reviewRepository.save(review);

        return reviewSerializer.apply(review);
    }

    @Transactional
    public ReviewPages getAllReviews(Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAll(pageRequest);

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(reviewSerializer)
                .toList();

        ReviewPages reviewPages = new ReviewPages(reviews);
        reviewPages.setTotal(reviewPage.getTotalElements());
        reviewPages.setLimit(limit);
        reviewPages.setPage(page);
        reviewPages.setPages(reviewPage.getTotalPages());

        return reviewPages;
    }

    @Transactional
    public ReviewDto getReviewByUserIdAndMovieId(Long userId, Long movieId) {
        Review review = reviewRepository.findAllByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(
                                "Ревью на фильм с id '%s' от пользователя с айди '%s' не найдено", movieId, userId)
                ));

        return reviewSerializer.apply(review);
    }

    @Transactional
    public ReviewPages getReviewByMovieId(Long movieId, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAllByMovieId(movieId, pageRequest);

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(reviewSerializer)
                .toList();

        ReviewPages reviewPages = new ReviewPages(reviews);
        reviewPages.setTotal(reviewPage.getTotalElements());
        reviewPages.setLimit(limit);
        reviewPages.setPage(page);
        reviewPages.setPages(reviewPage.getTotalPages());

        return reviewPages;
    }

    @Transactional
    public ReviewPages getReviewByUserId(Long userId, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAllByUserId(userId, pageRequest);

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(reviewSerializer)
                .toList();

        ReviewPages reviewPages = new ReviewPages(reviews);
        reviewPages.setTotal(reviewPage.getTotalElements());
        reviewPages.setLimit(limit);
        reviewPages.setPage(page);
        reviewPages.setPages(reviewPage.getTotalPages());

        return reviewPages;
    }

    @Transactional
    public ReviewDto updateReview(Long reviewId, ReviewDtoUpdateRequest reviewDtoRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Ревью с данным id '%s' не найдено", reviewId)));

        review.setId(reviewId);
        review.setUserReview(reviewDtoRequest.getReview());
        review.setRating(reviewDtoRequest.getRating());

        reviewRepository.save(review);

        return reviewSerializer.apply(review);
    }

    @Transactional
    public MessageDto deleteReview(Long reviewId) {
        User user = userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(
                                "Ревью с данным ID '%s' найдено", reviewId)
                ));
        if (!Objects.equals(review.getUser().getId(), user.getId())) {
            throw new CustomAccessDeniedException("Вы не можете удалить ревью другого пользователя.");
        }
        reviewRepository.deleteById(reviewId);
        return new MessageDto(200, "Ревью успешно удалено.", Date.from(Instant.now()).toString());
    }
    @Transactional
    public Double getAverageReviewRatingById(Long id) {
        Integer count = reviewRepository.countByMovieId(id);
        Integer countReviewToCalcAverage = 5;
        if (count > 0 && count % countReviewToCalcAverage == 0) {
            return getAverage(id);
        }
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.map(Movie::getKgRating).orElse(null);
    }
    @Transactional
    public Double getAverage(Long id) {
        Double avg = reviewRepository.getAvgRatingByMovieId(id);
        Movie movie = movieRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Фильм с данным ID не найден!"));
        movie.setId(id);
        movie.setKgRating(Math.round(avg * 10.0) / 10.0);
        movieRepository.save(movie);
        return Math.round(avg * 10.0) / 10.0;
    }

    public List<ReviewDto> getRandomReviewsByMovieId(Long movieId) {
        return reviewRepository.findRandomReviewByMovieId(movieId).stream()
                .map(reviewSerializer)
                .toList();
    }
}
