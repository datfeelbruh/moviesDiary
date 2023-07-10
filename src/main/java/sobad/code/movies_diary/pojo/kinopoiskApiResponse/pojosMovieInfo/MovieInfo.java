package sobad.code.movies_diary.pojo.kinopoiskApiResponse.pojosMovieInfo;

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
