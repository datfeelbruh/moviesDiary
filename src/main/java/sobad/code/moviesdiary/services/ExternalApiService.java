package sobad.code.moviesdiary.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sobad.code.moviesdiary.dtos.movie.MovieDto;
import sobad.code.moviesdiary.dtos.pages.MoviePages;
import sobad.code.moviesdiary.exceptions.entiry_exceptions.EntityNotFoundException;
import sobad.code.moviesdiary.mappers.external_api_serializer.ExternalAPISerializer;
import sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info.DocsItemMovieInfo;
import sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info.MovieInfo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {
    @Value("${x_api_key}")
    private String apiKey;
    private final ExternalAPISerializer externalAPISerializer;

    public MoviePages findMovieByName(String name, Integer page, Integer limit) {
        String url = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.kinopoisk.dev")
                .path("v1.3/movie")
                .queryParam("selectFields", "id", "name", "year", "rating.kp",
                        "rating.imdb", "poster.url", "genres.name", "description")
                .queryParam("name", name)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .build()
                .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", apiKey);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<MovieInfo> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity, MovieInfo.class);

        List<DocsItemMovieInfo> foundMovie = responseEntity.getBody().getDocs();
        log.debug("ИЩУ НА КП В КИНОПОИСК СЕРВИС");
        if (responseEntity.getBody().getDocs().isEmpty()) {
            throw new EntityNotFoundException("Фильмы для данного названия на Кинопоиске не найдены!");
        }

        List<MovieDto> movies = foundMovie.stream()
                .map(externalAPISerializer).toList();

        MoviePages moviePages = new MoviePages(movies);
        moviePages.setPage(responseEntity.getBody().getPage());
        moviePages.setPages(responseEntity.getBody().getPages());
        moviePages.setTotal((long) responseEntity.getBody().getTotal());
        moviePages.setLimit(responseEntity.getBody().getLimit());

        return moviePages;
    }
}
