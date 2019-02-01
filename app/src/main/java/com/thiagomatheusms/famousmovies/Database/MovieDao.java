package com.thiagomatheusms.famousmovies.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.thiagomatheusms.famousmovies.Model.Movie;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie")
    List<Movie> getFavoriteMovies();

    @Insert
    void insertFavoriteMovie(Movie movie);

    @Delete
    void deleteFavoriteMovie(Movie movie);
}
