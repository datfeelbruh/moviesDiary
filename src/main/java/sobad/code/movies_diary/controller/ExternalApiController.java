package sobad.code.movies_diary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sobad.code.movies_diary.AppError;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieNameDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieInfoDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieIdDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieShortInfoDto;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieList.DocsItemMoviesList;
import sobad.code.movies_diary.service.ExternalApiService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/externalApi")
public class ExternalApiController {
    private final ExternalApiService externalApiService;

    @PostMapping("/searchMoviesList")
    public ResponseEntity<?> findMovieList(@RequestBody KinopoiskMovieNameDto kinopoiskMovieDtoRequest) {
        try {
            List<KinopoiskMovieShortInfoDto> foundMovies = externalApiService.findMovieByName(kinopoiskMovieDtoRequest);
            return new ResponseEntity<>(foundMovies, HttpStatus.OK);
        } catch (Exception e) {
            return  new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    String.format(
                            "Фильмы с данным названием '%s' не были найдены",
                            kinopoiskMovieDtoRequest.getName())),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/searchMovieInfo")
    public ResponseEntity<?> findMovieInfo(@RequestBody KinopoiskMovieIdDto movieIdDto) {
        try {
            KinopoiskMovieInfoDto movieDtoResponse = externalApiService.findMovieInfoById(movieIdDto.getId());
            return new ResponseEntity<>(movieDtoResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(),
                    String.format(
                            "Фильм с данным id '%s' не найден",
                            movieIdDto.getId())),
                    HttpStatus.BAD_REQUEST);
        }
    }
}