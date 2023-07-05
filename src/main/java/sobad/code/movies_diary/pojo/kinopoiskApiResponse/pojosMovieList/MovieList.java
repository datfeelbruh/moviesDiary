package sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieList;

import java.util.List;

public class MovieList {
	private int total;
	private int pages;
	private List<DocsItemMoviesList> docs;
	private int limit;
	private int page;

	public int getTotal(){
		return total;
	}

	public int getPages(){
		return pages;
	}

	public List<DocsItemMoviesList> getDocs(){
		return docs;
	}

	public int getLimit(){
		return limit;
	}

	public int getPage(){
		return page;
	}
}