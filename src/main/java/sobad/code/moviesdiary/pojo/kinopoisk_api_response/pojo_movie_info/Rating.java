package sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    private Double imdb;
    private Double kp;

    public Double getImdb() {
        return imdb;
    }

    public Double getKp() {
        return kp;
    }
}

