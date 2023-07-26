package sobad.code.movies_diary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import sobad.code.movies_diary.dtos.movie.MovieDto;
import sobad.code.movies_diary.dtos.movie.MoviePages;
import sobad.code.movies_diary.mappers.externalApiSerializer.ExternalAPISerializer;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.MovieInfo;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
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

        if (responseEntity.getBody().getDocs().isEmpty()) {
            return MoviePages.builder()
                    .movies(new ArrayList<>())
                    .page(responseEntity.getBody().getPage())
                    .pages(responseEntity.getBody().getPages())
                    .total((long) responseEntity.getBody().getTotal())
                    .limit(responseEntity.getBody().getLimit())
                    .build();
        }

        List<MovieDto> movies = foundMovie.stream()
                .map(externalAPISerializer).toList();

        return MoviePages.builder()
                .movies(movies)
                .page(responseEntity.getBody().getPage())
                .pages(responseEntity.getBody().getPages())
                .total((long) responseEntity.getBody().getTotal())
                .limit(responseEntity.getBody().getLimit())
                .build();
    }
}
