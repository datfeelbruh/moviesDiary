package sobad.code.movies_diary.mappers;

import org.springframework.stereotype.Component;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieInfoDto;
import sobad.code.movies_diary.dto.externalApiDtos.KinopoiskMovieShortInfoDto;
import sobad.code.movies_diary.entities.Movie;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.DocsItemMovieInfo;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.GenresItem;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieList.DocsItemMoviesList;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KinopoiskMapper {
    public KinopoiskMovieShortInfoDto mapToMovieShortInfo(DocsItemMoviesList docsItemMoviesList) {
        return KinopoiskMovieShortInfoDto.builder()
                .id(docsItemMoviesList.getId())
                .name(docsItemMoviesList.getName())
                .year(docsItemMoviesList.getYear())
                .build();
    }

    public KinopoiskMovieInfoDto mapToMovieInfo(DocsItemMovieInfo docsItemMovieInfo) {
        return KinopoiskMovieInfoDto.builder()
                .kpId(docsItemMovieInfo.getId())
                .movieName(docsItemMovieInfo.getName())
                .releaseYear(docsItemMovieInfo.getYear())
                .kpRating((Double) docsItemMovieInfo.getRating().getKp())
                .imdbRating((Double) docsItemMovieInfo.getRating().getImdb())
                .posterUrl(docsItemMovieInfo.getPoster().getUrl())
                .genres(new HashSet<>(docsItemMovieInfo.getGenres()))
                .build();
    }

    public KinopoiskMovieInfoDto mapToMovieInfoFromMovieInDb(Movie movie) {
        return KinopoiskMovieInfoDto.builder()
                .kpId(movie.getKpId())
                .movieName(movie.getMovieName())
                .releaseYear(movie.getReleaseYear())
                .kpRating(movie.getKpRating())
                .imdbRating(movie.getImdbRating())
                .posterUrl(movie.getPosterUrl())
                .genres(
                        movie.getGenres().stream()
                                .map(e -> new GenresItem(e.getName()))
                                .collect(Collectors.toSet())
                )
                .build();
    }
}
