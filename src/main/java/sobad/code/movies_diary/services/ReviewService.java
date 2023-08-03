package sobad.code.movies_diary.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sobad.code.movies_diary.dtos.review.ReviewDto;
import sobad.code.movies_diary.dtos.review.ReviewDtoRequest;
import sobad.code.movies_diary.dtos.pages.ReviewPages;
import sobad.code.movies_diary.dtos.review.ReviewDtoUpdateRequest;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.entities.Review;
import sobad.code.movies_diary.entities.User;
import sobad.code.movies_diary.exceptions.entiryExceptions.CustomAccessDeniedException;
import sobad.code.movies_diary.exceptions.entiryExceptions.EntityAlreadyExistException;
import sobad.code.movies_diary.exceptions.entiryExceptions.EntityNotFoundException;
import sobad.code.movies_diary.jwt.JwtTokenUtils;
import sobad.code.movies_diary.mappers.entitySerializers.ReviewSerializer;
import sobad.code.movies_diary.repositories.MovieRepository;
import sobad.code.movies_diary.repositories.ReviewRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final JwtTokenUtils jwtTokenUtils;
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
                .review(reviewDtoRequest.getReview())
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

        return ReviewPages.builder()
                .reviews(reviews)
                .total(reviewPage.getTotalElements())
                .limit(limit)
                .page(page)
                .pages(reviewPage.getTotalPages())
                .build();
    }

    @Transactional
    public ReviewDto getReviewByUserIdAndMovieId(Long userId, Long movieId) {
        Review review = reviewRepository.findAllByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(
                                "Ревью на фильм с id '%s' от пользователя с айди '%s' не найдено", userId, movieId)
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

        return ReviewPages.builder()
                .reviews(reviews)
                .total(reviewPage.getTotalElements())
                .limit(limit)
                .page(page)
                .pages(reviewPage.getTotalPages())
                .build();
    }

    @Transactional
    public ReviewPages getReviewByUserId(Long userId, Integer page, Integer limit) {
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Review> reviewPage = reviewRepository.findAllByUserId(userId, pageRequest);

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(reviewSerializer)
                .toList();

        return ReviewPages.builder()
                .reviews(reviews)
                .total(reviewPage.getTotalElements())
                .limit(limit)
                .page(page)
                .pages(reviewPage.getTotalPages())
                .build();
    }

    @Transactional
    public ReviewDto updateReview(Long reviewId, ReviewDtoUpdateRequest reviewDtoRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException(
                    String.format("Ревью с данным id '%s' не найдено", reviewId)));

        review.setId(reviewId);
        review.setReview(reviewDtoRequest.getReview());
        review.setRating(reviewDtoRequest.getRating());

        reviewRepository.save(review);

        return reviewSerializer.apply(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
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
    }

    public Double getAverageReviewRatingById(Long id) {
        Integer count = reviewRepository.countByMovieId(id);
        Integer countReviewToCalcAverage = 5;
        if (count > 0 && count % countReviewToCalcAverage == 0) {
            return getAverage(id);
        }
        return null;
    }


    public Double getAverage(Long id) {
        Double avg = reviewRepository.getAvgRatingByMovieId(id);
        return Math.round(avg * 10.0) / 10.0;
    }

    public List<ReviewDto> getRandomReviewsByMovieId(Long movieId) {
        return reviewRepository.findRandomReviewByMovieId(movieId).stream()
                .map(reviewSerializer)
                .toList();
    }
}
