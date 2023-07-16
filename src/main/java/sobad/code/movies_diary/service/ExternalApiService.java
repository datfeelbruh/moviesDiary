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
import sobad.code.movies_diary.dto.movie.MovieDtoResponse;
import sobad.code.movies_diary.mappers.MovieMapper;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.MovieInfo;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalApiService {
    @Value("${x-api-key}")
    private String apiKey;
    private final MovieMapper movieMapper;

    public List<MovieDtoResponse> findMovieByName(String name) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.kinopoisk.dev")
                .path("v1.3/movie")
                .queryParam("selectFields", "id", "name", "year", "rating.kp",
                        "rating.imdb", "poster.url", "genres.name", "description")
                .queryParam("name", name)
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
            throw new RuntimeException(String.format("Фильм с данным названием '%s' не найден", name));
        }

        return foundMovie.stream()
                .map(movieMapper::mapFromKinopoiskToMovieInfo)
                .collect(Collectors.toList());
    }
}
