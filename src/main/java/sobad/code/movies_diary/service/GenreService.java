package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.entities.Genre;
import sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo.GenresItem;
import sobad.code.movies_diary.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Set<Genre> getGenres(List<GenresItem> genresItems) {
        return genresItems.stream()
                .map(this::createGenre)
                .collect(Collectors.toSet());
    }

    private Genre createGenre(GenresItem genresItem) {
        Optional<Genre> genreInDB = genreRepository.findByName(genresItem.getName());
        if (genreInDB.isPresent()) {
            return genreInDB.get();
        }

        Genre newGenre = Genre.builder().name(genresItem.getName()).build();
        return genreRepository.save(newGenre);
    }
}
