package sobad.code.movies_diary.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import sobad.code.movies_diary.dtos.review.ReviewDtoRequest;
import sobad.code.movies_diary.dtos.review.ReviewDtoResponse;
import sobad.code.movies_diary.exceptions.AppError;
import sobad.code.movies_diary.services.ReviewService;

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
    public ResponseEntity<?> createReview(@RequestBody ReviewDtoRequest reviewDtoRequest, HttpServletRequest request) {
        return new ResponseEntity<>(reviewService.createReview(reviewDtoRequest, request), CREATED);
    }
    @Operation(summary = "Поиск ревью в базе данных приложения", description =
            """
            Поиск ревью возможен с данными параметрами:
            \s
            1) userId - возвращает все ревью пользователя с данным ID.
            \s
            2) movieId - возвращает все ревью на фильм с данным ID.
            \s
            3) userId + movieId - вернет ревью на фильм с данным ID от пользователя с данным ID.
            \s
            Метод возвращает определенное количество результатов сформированных в страницы на основе параметров.
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
    public ResponseEntity<?> getReviews(
            @RequestParam(required = false) @Parameter(description = "ID пользователя",
                    example = "1") Long userId,
            @RequestParam(required = false) @Parameter(description = "ID фильма",
            example = "1") Long movieId,
            @RequestParam(required = false, value = "page", defaultValue = "1")
            @Parameter(description = "Страница выборки.") Integer page,
            @RequestParam(required = false, value = "limit", defaultValue = "10")
            @Parameter(description = "Количество элементов на странице.") Integer limit) {

        if (userId != null && movieId != null) {
            return new ResponseEntity<>(reviewService.getReviewByUserIdAndMovieId(userId, movieId), OK);
        } else if (movieId != null) {
            return new ResponseEntity<>(reviewService.getReviewByMovieId(movieId, page, limit), OK);
        } else if (userId != null) {
            return new ResponseEntity<>(reviewService.getReviewByUserId(userId, page, limit), OK);
        }
        return new ResponseEntity<>(reviewService.getAllReviews(page, limit), OK);
    }

    @Operation(summary = "Обновить ревью для фильма", description =
            """
            Данный метод обновляет ревью на фильм у пользователя.
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
            description = "Ревью или фильм с данным ID не найден",
            content = {
                @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AppError.class))
            }
            )
    })
    @PutMapping(value = REVIEW_CONTROLLER_PATH + "/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable @Parameter(description = "ID ревью", example = "1") Long reviewId,
            @RequestBody ReviewDtoRequest reviewDtoRequest) {
        return new ResponseEntity<>(reviewService.updateReview(reviewId, reviewDtoRequest), OK);
    }
    @Operation(summary = "Удалить ревью для фильма")
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Ревью пользователя успешно удалено"
            ),
        @ApiResponse(
                responseCode = "422",
                description = "Исключение при запросе.",
                content = {
                    @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AppError.class))
                    }
            )
    })
    @DeleteMapping(value = REVIEW_CONTROLLER_PATH + "/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable(value = "reviewId") @Parameter(description = "ID ревью",
            example = "1") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Ревью удалено");
    }
}
