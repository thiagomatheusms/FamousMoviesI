package com.thiagomatheusms.famousmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Movie implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("poster_path")
    private String poster_path;

    @SerializedName("original_title")
    private String original_title;

    @SerializedName("overview")
    private String synopsis;

    @SerializedName("vote_average")
    private float vote_average;

    @SerializedName("release_date")
    private String date_release;

    public Movie(String title) {
        this.title = title;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster_path = in.readString();
        original_title = in.readString();
        synopsis = in.readString();
        vote_average = in.readFloat();
        date_release = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getDate_release() {
        return date_release;
    }

    public void setDate_release(String date_release) {
        this.date_release = date_release;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(original_title);
        parcel.writeString(synopsis);
        parcel.writeFloat(vote_average);
        parcel.writeString(date_release);
    }
}
