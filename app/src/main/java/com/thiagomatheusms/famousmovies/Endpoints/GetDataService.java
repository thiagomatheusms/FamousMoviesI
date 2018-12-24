package com.thiagomatheusms.famousmovies.Endpoints;

import com.thiagomatheusms.famousmovies.Model.Page;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {

    public static final String API_KEY = "";

    @GET("/3/movie/popular?api_key="+API_KEY+"")
    Call<Page> getPopular(@Query("page") int page);

    @GET("/3/movie/top_rated?api_key="+API_KEY+"")
    Call<Page> getTopRated(@Query("page") int page);
}
