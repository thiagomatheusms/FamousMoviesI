package com.thiagomatheusms.famousmovies.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.thiagomatheusms.famousmovies.Model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    private static final Object LOCK =  new Object();
    private static final String DATABASE_NAME = "famousMovies";
    private static AppDataBase sInstance;

    public static AppDataBase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDataBase.class,
                        AppDataBase.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return sInstance;
    }

    public abstract MovieDao movieDao();
}
