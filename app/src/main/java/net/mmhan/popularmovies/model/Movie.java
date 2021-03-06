package net.mmhan.popularmovies.model;

import java.io.Serializable;

/**
 * Created by mmhan on 6/9/15.
 */
public class Movie implements Serializable {

    private int id;

    private String poster_path;
    private String backdrop_path;
    public String title;
    public String overview;
    public Float vote_average;
    public String release_date;

    final String BACKDROP_SIZE = "w780";
    final String POSTER_SIZE = "w342";
    final String BASE_URL = "http://image.tmdb.org/t/p/";

    public Movie(FavoriteMovie movie) {
        this.id = movie.getId();
        this.poster_path = movie.getPosterPath();
        this.backdrop_path = movie.getBackdropPath();
        this.title = movie.getTitle();
        this.overview = movie.getOverview();
        this.vote_average = movie.getVoteAverage();
        this.release_date = movie.getReleaseDate();
    }

    public int getId() {
        return id;
    }

    public FavoriteMovie getRealmObject() {
        return new FavoriteMovie(id, poster_path, backdrop_path, title, overview, vote_average, release_date);
    }

    public String getPosterUrl() {
        return String.format("%s%s/%s", BASE_URL, POSTER_SIZE, poster_path);
    }

    public String getBackdropUrl() {
        return String.format("%s%s/%s", BASE_URL, BACKDROP_SIZE, backdrop_path);
    }
}
