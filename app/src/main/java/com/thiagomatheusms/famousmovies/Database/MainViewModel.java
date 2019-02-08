package com.thiagomatheusms.famousmovies.Database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.thiagomatheusms.famousmovies.Model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Movie>> favoriteMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDataBase database = AppDataBase.getInstance(this.getApplication());
        Log.d("teste", "Actively retrieving the tasks from the DataBase");

        favoriteMovies = database.movieDao().getFavoriteMovies();
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        return favoriteMovies;
    }
}
