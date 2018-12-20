package com.thiagomatheusms.famousmovies.Endpoints;

import com.thiagomatheusms.famousmovies.Model.Page;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {

    @GET("/3/movie/popular?api_key=18343d38f6be3a0a88ca9c1898b272e7")
    Call<Page> getPopular(@Query("page") int page);

    @GET("/3/movie/top_rated?api_key=18343d38f6be3a0a88ca9c1898b272e7")
    Call<Page> getTopRated(@Query("page") int page);
}
