package sobad.code.moviesdiary.pojo.kinopoisk_api_response.pojo_movie_info;

import java.util.List;

public class MovieInfo {
    private int total;
    private int pages;
    private List<DocsItemMovieInfo> docs;
    private int limit;
    private int page;

    public int getTotal() {
        return total;
    }

    public int getPages() {
        return pages;
    }

    public List<DocsItemMovieInfo> getDocs() {
        return docs;
    }

    public int getLimit() {
        return limit;
    }

    public int getPage() {
        return page;
    }
}
