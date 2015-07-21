package net.mmhan.popularmovies.model;

import java.util.List;

/**
 * Created by mmhan on 21/7/15.
 */
public class MoviesResult {
    String page;
    List<Movie> results;
    int totalPages;
    int totalResults;

    public List<Movie> getMovies(){
        return results;

    }

    public class Movie {
        public String poster_path;
        public String title;
        public String overview;
        public Float vote_average;
        public String release_date;
    }
}
