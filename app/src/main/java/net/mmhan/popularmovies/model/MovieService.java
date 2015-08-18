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

    String END_POINT = "http://api.themoviedb.org/3/";
    String EP_POPULAR = "/discover/movie?sort_by=popularity.desc";
    String EP_HIGHEST_RATED = "/discover/movie?sort_by=vote_average.desc&vote_count.gte=1000";


    @GET(EP_HIGHEST_RATED)
    void topRated(@Query("page") int page, Callback<MoviesResult> callback);

    @GET(EP_POPULAR)
    void popular(@Query("page") int page, Callback<MoviesResult> callback);



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
