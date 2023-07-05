package sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo;

import java.util.List;

public class DocsItemMovieInfo {
	private long id;
	private int year;
	private List<GenresItem> genres;
	private Rating rating;
	private String name;
	private Poster poster;
	public long getId() {
		return id;
	}
	public int getYear(){
		return year;
	}

	public List<GenresItem> getGenres(){
		return genres;
	}

	public Rating getRating(){
		return rating;
	}

	public String getName(){
		return name;
	}

	public Poster getPoster(){
		return poster;
	}
}