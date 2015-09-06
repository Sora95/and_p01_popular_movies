package net.mmhan.popularmovies.model;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by mmhan on 20/7/15.
 */
public interface MovieService {

    String END_POINT = "http://api.themoviedb.org/3/";
    String EP_POPULAR = "/discover/movie?sort_by=popularity.desc";
    String EP_UNPOPULAR = "/discover/movie?sort_by=popularity.asc";
    String EP_HIGHEST_RATED = "/discover/movie?sort_by=vote_average.desc&vote_count.gte=1000";
    String EP_LOWEST_RATED = "/discover/movie?sort_by=vote_average.asc&vote_count.gte=1000";
    String EP_TRAILERS = "/movie/{id}/videos";
    String EP_REVIEWS = "/movie/{id}/reviews";


    @GET(EP_HIGHEST_RATED)
    void highestRated(@Query("page") int page, Callback<MoviesResult> callback);
    @GET(EP_LOWEST_RATED)
    void lowestRated(@Query("page") int page, Callback<MoviesResult> callback);

    @GET(EP_POPULAR)
    void popular(@Query("page") int page, Callback<MoviesResult> callback);
    @GET(EP_UNPOPULAR)
    void unpopular(@Query("page") int page, Callback<MoviesResult> callback);

    @GET(EP_TRAILERS)
    void trailers(@Path("id") int id, Callback<TrailersResult> callback);

    @GET(EP_REVIEWS)
    void reviews(@Path("id") int id, Callback<ReviewsResult> callback);


    class Implementation {
        public static MovieService get(final String api_key) {
            return new RestAdapter.Builder()
                    .setEndpoint(END_POINT)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("api_key", api_key);
                        }
                    })
                    .build()
                    .create(MovieService.class);
        }
    }
}
