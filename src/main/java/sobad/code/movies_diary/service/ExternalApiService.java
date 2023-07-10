package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieInfoDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieShortInfoDto;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.mappers.KinopoiskMapper;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.MovieInfo;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieList.DocsItemMoviesList;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieList.MovieList;
import sobad.code.movies_diary.repositories.MovieRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalApiService {
    @Value("${x-api-key}")
    private String apiKey;
    private final KinopoiskMapper mapper;
    private final MovieRepository movieRepository;

    public List<KinopoiskMovieShortInfoDto> findMovieByName(String movieName) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.kinopoisk.dev")
                .path("v1.3/movie")
                .queryParam("selectFields", "id", "name", "year")
                .queryParam("name", movieName)
                .build()
                .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<MovieList> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity, MovieList.class);

        List<DocsItemMoviesList> foundMoviesList = responseEntity.getBody().getDocs();

        if (foundMoviesList.isEmpty()) {
            throw new RuntimeException(
                    String.format("Фильмы с данным названием '%s' не найдены", movieName)
            );
        }

        return foundMoviesList.stream()
                .map(mapper::mapToMovieShortInfo)
                .collect(Collectors.toList());
    }

    public KinopoiskMovieInfoDto findMovieInfoById(Long id) {
        Optional<Movie> movieInDb = movieRepository.findByKpId(id);
        if (movieInDb.isPresent()) {
            return mapper.mapToMovieInfoFromMovieInDb(movieInDb.get());
        }
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.kinopoisk.dev")
                .path("v1.3/movie")
                .queryParam("selectFields", "id", "name", "year", "rating.kp",
                        "rating.imdb", "poster.url", "genres.name")
                .queryParam("id", id)
                .build()
                .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<MovieInfo> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity, MovieInfo.class);

        List<DocsItemMovieInfo> foundMovie = responseEntity.getBody().getDocs();

        if (responseEntity.getBody().getDocs().isEmpty()) {
            throw new RuntimeException(String.format("Фильм с данным id '%s' не найден", id));
        }

        return mapper.mapToMovieInfo(foundMovie.get(0));
    }
}
