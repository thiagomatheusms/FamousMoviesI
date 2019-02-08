package com.thiagomatheusms.famousmovies.Database;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Database;

public class DetailMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDataBase mDb;
    private final int mMovieID;

    public DetailMovieViewModelFactory(AppDataBase mDb, int mMovieID) {
        this.mDb = mDb;
        this.mMovieID = mMovieID;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new DetailMovieViewModel(mDb, mMovieID);
    }
}
