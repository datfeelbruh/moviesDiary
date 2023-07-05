package sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class GenresItem{
	private String name;

	public String getName(){
		return name;
	}
}
