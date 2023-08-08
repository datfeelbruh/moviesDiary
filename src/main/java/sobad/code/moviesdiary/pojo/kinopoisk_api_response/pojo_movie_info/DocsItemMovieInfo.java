package sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info;

import java.util.List;
import java.util.Optional;

public class DocsItemMovieInfo {
    private long id;
    private int year;
    private List<GenresItem> genres;
    private Optional<Rating> rating;
    private String name;
    private Poster poster;
    private Optional<String> description;
    public long getId() {
        return id;
    }
    public int getYear() {
        return year;
    }

    public List<GenresItem> getGenres() {
        return genres;
    }

    public Optional<Rating> getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }
    public Optional<String> getDescription() {
        return description;
    }

    public Poster getPoster() {
        return poster;
    }
}
