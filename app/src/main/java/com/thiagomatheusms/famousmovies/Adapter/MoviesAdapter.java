package com.thiagomatheusms.famousmovies.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thiagomatheusms.famousmovies.MainActivity;
import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Page;
import com.thiagomatheusms.famousmovies.R;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private static final String BASE_URL_IMG = "http://image.tmdb.org/t/p/w185/";
    private List<Movie> mMoviesList;
    private MoviesAdapterOnClickHandler moviesAdapterOnClickHandler;

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler, List<Movie> mList) {
        this.moviesAdapterOnClickHandler = clickHandler;
        this.mMoviesList = mList;
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int idLayoutItemMovieList = R.layout.item_movie_list;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(idLayoutItemMovieList, parent, false);
        MoviesAdapterViewHolder moviesAdapterViewHolder = new MoviesAdapterViewHolder(view);

        return moviesAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder holder, int position) {
        String urlMoviePoster = mMoviesList.get(position).getPoster_path();

//        Glide.with(holder.itemView).load(BASE_URL_IMG + urlMoviePoster).into(holder.mImageMovie);
        Picasso.get().load(BASE_URL_IMG + urlMoviePoster)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .fit()
                .into(holder.mImageMovie);
    }

    @Override
    public int getItemCount() {
        if (mMoviesList == null) {
            return 0;
        }
        return mMoviesList.size();
    }

    //Interface listener
    public interface MoviesAdapterOnClickHandler {
        void onClickHandler(Movie movieClicked);
    }

    //ViewHolder
    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mImageMovie;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);

            mImageMovie = (ImageView) itemView.findViewById(R.id.iv_movie_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie movie = mMoviesList.get(position);
            moviesAdapterOnClickHandler.onClickHandler(movie);
        }
    }

    //setList
    public void setMoviesList(List<Movie> movies, int currentItems) {
        mMoviesList = movies;
        notifyDataSetChanged();
    }
}
