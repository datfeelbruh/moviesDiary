package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.dto.review.ReviewDtoRequest;
import sobad.code.movies_diary.dto.review.ReviewDtoResponse;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.service.ReviewService;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с ревью", description = "API Ревью пользователей")
public class ReviewController {
    private final ReviewService reviewService;
    public static final String REVIEW_CONTROLLER_PATH = "/api/reviews";

    @Operation(summary = "Создать ревью для фильма", description =
            """
            RequestBody может содержать пустые поля review и rating, тогда фильм просто привяжется к пользователю
            без оценки и ревью.
            \s
            Таким образом пользователь добавит себе его в профиль с возможность оценить после просмотра. 
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Ревью пользователя успешно добавлено к фильму",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewDtoResponse.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Ревью уже создано либо фильм с данным ID не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class))
                    }
            )
    })
    @PostMapping(value = REVIEW_CONTROLLER_PATH)
    public ResponseEntity<?> createReview(@RequestBody ReviewDtoRequest reviewDtoRequest) {
        return new ResponseEntity<>(reviewService.createReview(reviewDtoRequest), CREATED);
    }
    @Operation(summary = "Поиск ревью в базе данных приложения", description =
            """
            Поиск ревью возможен с данными параметрами:
            \s
            1) userId - возвращает все ревью пользователя с данным ID.
            \s
            2) movieId - возвращает все ревью на фильм с данным ID.
            \s
            3) поиск без параметров - вернет все ревью из базы данных приложения.
            \s
            Комбинирование параметров не допускается.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ревью пользователей на фильмы",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ReviewDtoResponse.class)))
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Ошибка, ревью на фильм с данным ID от пользователя с ID не найдено.",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class))
                    }
            ),
    })
    @GetMapping(value = REVIEW_CONTROLLER_PATH)
    public ResponseEntity<?> getAllReviews() {
        return new ResponseEntity<>(reviewService.getAllReviews(), OK);
    }

    @GetMapping(value = REVIEW_CONTROLLER_PATH, params = "userId")
    public ResponseEntity<?> getReviewByUserId(@RequestParam(required = false) Long userId) {
        return new ResponseEntity<>(reviewService.getReviewByUserId(userId), OK);
    }

    @GetMapping(value = REVIEW_CONTROLLER_PATH, params = "movieId")
    public ResponseEntity<?> getReviewByMovieId(@RequestParam(required = false) Long movieId) {
        return new ResponseEntity<>(reviewService.getReviewByMovieId(movieId), OK);
    }
    @Operation(summary = "Обновить ревью для фильма", description =
            """
            Эндпоинт обновляет ревью на фильм у пользователя.
            """)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ревью пользователя успешно обновлено у фильма",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReviewDtoResponse.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Ревью либо фильм с данным ID не найден",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppError.class))
                    }
            )
    })
    @PutMapping(value = REVIEW_CONTROLLER_PATH + "/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewDtoRequest reviewDtoRequest) {
        return new ResponseEntity<>(reviewService.updateReview(id, reviewDtoRequest), OK);
    }
    @Operation(summary = "Удалить ревью для фильма")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ревью пользователя успешно удалено"
            )
    })
    @DeleteMapping(value = REVIEW_CONTROLLER_PATH + "/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok("Ревью удалено");
    }
}
