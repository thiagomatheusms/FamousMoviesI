package com.thiagomatheusms.famousmovies.Model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Page {

    @SerializedName("page")
    private int title;

    @SerializedName("results")
    private List<Movie> movies;

    public Page(int title) {

        this.title = title;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

}