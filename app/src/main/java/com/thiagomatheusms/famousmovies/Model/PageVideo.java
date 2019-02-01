package com.thiagomatheusms.famousmovies.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageVideo {

    @SerializedName("results")
    private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
