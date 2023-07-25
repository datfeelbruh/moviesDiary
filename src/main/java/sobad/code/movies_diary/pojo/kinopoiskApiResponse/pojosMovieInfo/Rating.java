package sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo;

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

