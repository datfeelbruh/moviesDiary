package sobad.code.movies_diary.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieInfoDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieShortInfoDto;
import sobad.code.movies_diary.service.ExternalApiService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с API кинопоиска")
public class ExternalApiController {
    public static final String EXTERNAL_API_CONTROLLER_MOVIES_PATH = "/api/externalApi/movies";
    public static final String EXTERNAL_API_CONTROLLER_MOVIE_PATH  = "/api/externalApi/movie";
    private final ExternalApiService externalApiService;

    @Operation(summary = "Поиск фильмов по названию.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Найденные фильмы", content =
            @Content(schema =
            @Schema(implementation = KinopoiskMovieShortInfoDto.class))
            )
    })
    @GetMapping(EXTERNAL_API_CONTROLLER_MOVIES_PATH)
    public ResponseEntity<?> findMovieList(@RequestParam String movieName) {
        try {
            List<KinopoiskMovieShortInfoDto> foundMovies = externalApiService.findMovieByName(movieName);
            return new ResponseEntity<>(foundMovies, HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    String.format(
                            "Фильмы с данным названием '%s' не были найдены",
                            movieName)),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Поиск всей нужной информации по id фильма на кинопоиске.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Найденный фильм", content =
            @Content(schema =
            @Schema(implementation = KinopoiskMovieInfoDto.class))
            )
    })
    @GetMapping(EXTERNAL_API_CONTROLLER_MOVIE_PATH)
    public ResponseEntity<?> findMovieInfo(@RequestParam Long movieKpId) {
        try {
            KinopoiskMovieInfoDto movieDtoResponse = externalApiService.findMovieInfoById(movieKpId);
            return new ResponseEntity<>(movieDtoResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    String.format(
                            "Фильм с данным id '%s' не найден",
                            movieKpId)),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
