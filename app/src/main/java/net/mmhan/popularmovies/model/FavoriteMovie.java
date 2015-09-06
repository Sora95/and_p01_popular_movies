package net.mmhan.popularmovies.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mmhan on 1/9/15.
 */
public class FavoriteMovie extends RealmObject{
    @PrimaryKey
    private int id;

    private String posterPath;
    private String backdropPath;
    private String title;
    private String overview;
    private float voteAverage;
    private String releaseDate;
    private Date favoritedAt;

    public FavoriteMovie(){
    }
    public FavoriteMovie(int id, String posterPath, String backdropPath, String title, String overview, Float voteAverage, String releaseDate) {
        this.id = id;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.favoritedAt = new Date();
    }

    public int getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Date getFavoritedAt() {
        return favoritedAt;
    }

    public void setFavoritedAt(Date favoritedAt) {
        this.favoritedAt = favoritedAt;
    }
}
