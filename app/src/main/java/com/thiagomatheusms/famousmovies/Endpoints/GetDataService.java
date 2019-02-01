package com.thiagomatheusms.famousmovies.Endpoints;

import com.thiagomatheusms.famousmovies.Model.Page;
import com.thiagomatheusms.famousmovies.Model.PageReview;
import com.thiagomatheusms.famousmovies.Model.PageVideo;
import com.thiagomatheusms.famousmovies.Model.Review;
import com.thiagomatheusms.famousmovies.Model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {

    public static final String API_KEY = "18343d38f6be3a0a88ca9c1898b272e7";

    @GET("/3/movie/popular?api_key=" + API_KEY + "")
    Call<Page> getPopular(@Query("page") int page);

    @GET("/3/movie/top_rated?api_key=" + API_KEY + "")
    Call<Page> getTopRated(@Query("page") int page);

    @GET("/3/movie/{id}/reviews?api_key="+API_KEY+"")
    Call<PageReview> getReviews(@Path("id") int idMovie);

    @GET("/3/movie/{id}/videos?api_key="+API_KEY+"")
    Call<PageVideo> getVideos(@Path("id") int idMovie);

}
