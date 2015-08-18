package net.mmhan.popularmovies.model;

import java.io.Serializable;
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

    public class Movie implements Serializable {

        String poster_path;
        String backdrop_path;
        public String title;
        public String overview;
        public Float vote_average;
        public String release_date;

        final String BACKDROP_SIZE = "w780";
        final String POSTER_SIZE = "w342";
        final String BASE_URL = "http://image.tmdb.org/t/p/";

        public String getPosterUrl(){
            return String.format("%s%s/%s", BASE_URL, POSTER_SIZE, poster_path);
        }

        public String getBackdropUrl(){
            return String.format("%s%s/%s", BASE_URL, BACKDROP_SIZE, backdrop_path);
        }
    }
}
