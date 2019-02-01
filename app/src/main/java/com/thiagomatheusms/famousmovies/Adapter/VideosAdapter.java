package com.thiagomatheusms.famousmovies.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Video;
import com.thiagomatheusms.famousmovies.R;

import java.util.List;
import java.util.zip.Inflater;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {

    private List<Video> mVideos;
    private VideosAdapterOnClickHandler videosAdapterOnClickHandler;

    public VideosAdapter(VideosAdapterOnClickHandler videosAdapterOnClickHandler, List<Video> mVideos) {
        this.mVideos = mVideos;
        this.videosAdapterOnClickHandler = videosAdapterOnClickHandler;
    }

    @NonNull
    @Override
    public VideosAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idLayoutItemVideoList = R.layout.item_video_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(idLayoutItemVideoList, parent, false);
        VideosAdapterViewHolder videosAdapterViewHolder = new VideosAdapterViewHolder(view);

        return videosAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapterViewHolder holder, int position) {
        String mName = mVideos.get(position).getName();
        holder.mNameVideo.setText(mName);
    }

    @Override
    public int getItemCount() {
        if (mVideos == null) {
            return 0;
        }
        return mVideos.size();
    }

    public interface VideosAdapterOnClickHandler {
        void onClickHandler(Video video);
    }

    //setList
    public void setVideosList(List<Video> videos, int currentItems) {
        mVideos = videos;
        notifyDataSetChanged();
    }


    public class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mNameVideo;

        public VideosAdapterViewHolder(View itemView) {
            super(itemView);

            mNameVideo = (TextView) itemView.findViewById(R.id.tv_video_name);
            mNameVideo.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Video video = mVideos.get(position);
            videosAdapterOnClickHandler.onClickHandler(video);
        }
    }
}
