package net.mmhan.popularmovies.model;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by mmhan on 20/7/15.
 */
public interface MovieService {

    public static String END_POINT = "http://api.themoviedb.org/3/";

    @GET("/movie/top_rated")
    void topRated(Callback<MoviesResult> callback);

    @GET("/movie/top_rated")
    void topRated(@Query("page") int page, Callback<MoviesResult> callback);

    @GET("/movie/popular")
    void popular(Callback<MoviesResult> callback);
    @GET("/movie/popular")
    void popular(@Query("page") int page, Callback<MoviesResult> callback);

    public static class Implementation {
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
