package com.thiagomatheusms.famousmovies.Database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.thiagomatheusms.famousmovies.Model.Movie;

public class DetailMovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public DetailMovieViewModel(AppDataBase database, int movieID) {
        movie = database.movieDao().getMovieId(movieID);
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
