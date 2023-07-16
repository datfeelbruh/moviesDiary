package sobad.code.movies_diary.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.dto.review.ReviewDtoRequest;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.service.ReviewService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private static final String REVIEW_CONTROLLER_PATH = "/api/reviews";

    @PostMapping(value = REVIEW_CONTROLLER_PATH)
    public ReviewDtoResponse createReview(@RequestBody ReviewDtoRequest reviewDtoRequest) {
        return reviewService.createReview(reviewDtoRequest);
    }

    @GetMapping(value = REVIEW_CONTROLLER_PATH, params = "userId")
    public List<ReviewDtoResponse> getReviewByUserId(@RequestParam Long userId) {
        return reviewService.getReviewByUserId(userId);
    }

    @GetMapping(value = REVIEW_CONTROLLER_PATH, params = "movieId")
    public List<ReviewDtoResponse> getReviewByMovieId(@RequestParam Long movieId) {
        return reviewService.getReviewByMovieId(movieId);
    }

    @PutMapping(value = REVIEW_CONTROLLER_PATH)
    public ReviewDtoResponse updateReview(@PathVariable Long id, @RequestBody ReviewDtoRequest reviewDtoRequest) {
        return reviewService.updateReview(id, reviewDtoRequest);
    }
}
