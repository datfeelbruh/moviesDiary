package sobad.code.movies_diary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sobad.code.movies_diary.dto.GenreDto;
import sobad.code.movies_diary.entities.Genre;
import sobad.code.movies_diary.repositories.GenreRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
//    public Genre createGenre(GenreDto genreDto) {
//        Genre genre = Genre.builder()
//                .name(genreDto.getName())
//                .build();
//
//        return genreRepository.save(genre);
//    }

    public Genre findGenre(String genreName) {
        Optional<Genre> genreInDB = genreRepository.findByName(genreName);
        if (genreInDB.isPresent()) {
            return genreInDB.get();
        }
        Genre newGenre = Genre.builder().name(genreName).build();
        return genreRepository.save(newGenre);
    }
}
