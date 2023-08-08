package sobad.code.moviesdiary.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.moviesdiary.entities.Genre;
import sobad.code.moviesdiary.repositories.GenreRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Genre findGenre(String genreName) {
        Optional<Genre> genreInDB = genreRepository.findByName(genreName);
        if (genreInDB.isPresent()) {
            return genreInDB.get();
        }
        Genre newGenre = Genre.builder().name(genreName).build();
        return genreRepository.save(newGenre);
    }
}
