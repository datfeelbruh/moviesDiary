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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieInfoDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieShortInfoDto;
import sobad.code.movies_diary.service.ExternalApiService;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequiredArgsConstructor
@Tag(name = "Взаимодействие с API кинопоиска")
public class ExternalApiController {
    public static final String EXTERNAL_API_CONTROLLER_PATH = "/api/externalApi/movies";
    private final ExternalApiService externalApiService;

    @Operation(summary = "Поиск фильмов по названию.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Найденные фильмы", content =
            @Content(schema =
            @Schema(implementation = KinopoiskMovieShortInfoDto.class))
            )
    })
    @RequestMapping(value = EXTERNAL_API_CONTROLLER_PATH, method = GET, params = "name")
    public ResponseEntity<?> findMovieList(@RequestParam String name) {
        try {
            List<KinopoiskMovieShortInfoDto> foundMovies = externalApiService.findMovieByName(name);
            return new ResponseEntity<>(foundMovies, HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    String.format(
                            "Фильмы с данным названием '%s' не были найдены",
                            name)),
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
    @RequestMapping(value = EXTERNAL_API_CONTROLLER_PATH, method = GET, params = "kpId")
    public ResponseEntity<?> findMovieInfo(@RequestParam Long kpId) {
        try {
            KinopoiskMovieInfoDto movieDtoResponse = externalApiService.findMovieInfoById(kpId);
            return new ResponseEntity<>(movieDtoResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    String.format(
                            "Фильм с данным id '%s' не найден",
                            kpId)),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
