package com.thiagomatheusms.famousmovies;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.thiagomatheusms.famousmovies.Adapter.MoviesAdapter;
import com.thiagomatheusms.famousmovies.Adapter.VideosAdapter;
import com.thiagomatheusms.famousmovies.Endpoints.GetDataService;
import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Page;
import com.thiagomatheusms.famousmovies.Model.PageReview;
import com.thiagomatheusms.famousmovies.Model.PageVideo;
import com.thiagomatheusms.famousmovies.Model.Review;
import com.thiagomatheusms.famousmovies.Model.Video;
import com.thiagomatheusms.famousmovies.Utilities.RetrofitClientInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailMovie extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Review>>, VideosAdapter.VideosAdapterOnClickHandler {

    /* Views */
    private TextView mTitleMovie, mOriginalTitleMovie, mSynopsisMovie, mDateReleaseMovie, mReview, mReviewAuthor, mViewAll;
    private RecyclerView mRecyclerViewVideos;
    private ImageView mPosterPathMovie;
    private RatingBar mVoteAverageMovie;
    private Button mFavorite;

    /* Others */
    private GetDataService service;
    private int idMovie;
    private int totalViews;
    private VideosAdapter videosAdapter;
    private String urlVideo;

    /* Constant for Loader*/
    private static final int LOADER_ID = 200;
    private static final int LOADER_VIDEO_ID = 300;

    //constant for Bundle's SavedInstance
    public static final String ID_MOVIE_KEY = "idMovieKey";

    /* Constant for URL Images*/
    private static final String BASE_URL_IMG_2 = "http://image.tmdb.org/t/p/w185/";

    /* LoaderCallBack for Video's Loader */
    private LoaderCallbacks<List<Video>> videoResultLoaderListener;

    /* List of videos */
    private List<Video> mVideosList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        mTitleMovie = (TextView) findViewById(R.id.tv_title_movie);
        mOriginalTitleMovie = (TextView) findViewById(R.id.tv_originalTitle_movie);
        mSynopsisMovie = (TextView) findViewById(R.id.tv_synopsis_movie);
        mDateReleaseMovie = (TextView) findViewById(R.id.tv_releaseDate_movie);
        mPosterPathMovie = (ImageView) findViewById(R.id.iv_miniature_movie);
        mVoteAverageMovie = (RatingBar) findViewById(R.id.ratingbar_vote_movie);
        mFavorite = (Button) findViewById(R.id.btn_favorite);
        mReview = (TextView) findViewById(R.id.tv_review);
        mReviewAuthor = (TextView) findViewById(R.id.tv_review_author);
        mViewAll = (TextView) findViewById(R.id.tv_view_all);
        mRecyclerViewVideos = (RecyclerView) findViewById(R.id.rv_videos);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerViewVideos.setLayoutManager(layoutManager);
        mRecyclerViewVideos.setHasFixedSize(true);

        videosAdapter = new VideosAdapter(this, mVideosList);
        mRecyclerViewVideos.setAdapter(videosAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ID_MOVIE_KEY)) {
                idMovie = savedInstanceState.getInt(ID_MOVIE_KEY);
            }
        }

        Intent intentStartThisActivity = getIntent();

        if (intentStartThisActivity != null) {
            if (intentStartThisActivity.hasExtra("ID") &&
                    intentStartThisActivity.hasExtra("TITLE") &&
                    intentStartThisActivity.hasExtra("POSTER_PATH") &&
                    intentStartThisActivity.hasExtra("ORIGINAL_TITLE") &&
                    intentStartThisActivity.hasExtra("SYNOPSIS") &&
                    intentStartThisActivity.hasExtra("DATE_RELEASE") &&
                    intentStartThisActivity.hasExtra("VOTE_AVERAGE")) {

                idMovie = intentStartThisActivity.getIntExtra("ID", 0);
                String mTitle = intentStartThisActivity.getStringExtra("TITLE");
                String mPosterPath = intentStartThisActivity.getStringExtra("POSTER_PATH");
                String mOriginalTitle = intentStartThisActivity.getStringExtra("ORIGINAL_TITLE");
                String mSynopsis = intentStartThisActivity.getStringExtra("SYNOPSIS");
                String mDateRelease = intentStartThisActivity.getStringExtra("DATE_RELEASE");
                float mVoteAverage = intentStartThisActivity.getFloatExtra("VOTE_AVERAGE", 0);

                Glide.with(this).load(BASE_URL_IMG_2 + mPosterPath).into(mPosterPathMovie);

                mTitleMovie.setText(mTitle);
                mOriginalTitleMovie.setText(mOriginalTitle);
                mSynopsisMovie.setText(mSynopsis);
                mDateReleaseMovie.setText(mDateRelease);
                mVoteAverageMovie.setRating(setRatingVote(mVoteAverage));
            }
        }

        mFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTintStar();
            }

        });

//        mVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openVideo(urlVideo);
//            }
//        });

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> searchLoader = loaderManager.getLoader(LOADER_ID);
        if (searchLoader == null) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, null, this);
        }

        videoResultLoaderListener = new LoaderCallbacks<List<Video>>() {
            @NonNull
            @Override
            public Loader<List<Video>> onCreateLoader(int id, @Nullable Bundle args) {
                return new AsyncTaskLoader<List<Video>>(getBaseContext()) {

                    @Override
                    protected void onStartLoading() {
                        forceLoad();
                    }

                    @Nullable
                    @Override
                    public List<Video> loadInBackground() {
                        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

                        retrofit2.Call<PageVideo> call;

                        List<Video> retorno = null;

                        call = service.getVideos(idMovie);

                        try {
                            PageVideo pageVideo = call.execute().body();
                            retorno = pageVideo.getVideos();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return retorno;
                    }
                };
            }

            @Override
            public void onLoadFinished(@NonNull Loader<List<Video>> loader, List<Video> data) {
                if (data != null) {
                    mVideosList.addAll(data);
                    videosAdapter.setVideosList(data, 1);
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<List<Video>> loader) {

            }
        };


        getSupportLoaderManager().initLoader(LOADER_VIDEO_ID, null, videoResultLoaderListener);
    }

    /* Others */

    public float setRatingVote(float voteAverage) {
        float average = ((voteAverage * 5) / 10);
        return average;
    }


    public void setTintStar() {
        mFavorite.setCompoundDrawablesWithIntrinsicBounds(getBaseContext().getResources().
                getDrawable(R.drawable.ic_star_black_24dp), null, null, null);
        mFavorite.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    public void openVideo(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /* SaveInstanceState */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ID_MOVIE_KEY, idMovie);
    }

    /* LOADER REVIEWS */

    @NonNull
    @Override
    public Loader<List<Review>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<List<Review>>(this) {

            List<Review> mReviews;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (mReviews != null) {
                    return;
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Review> loadInBackground() {
                service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

                retrofit2.Call<PageReview> call;

                List<Review> retorno = null;

                call = service.getReviews(idMovie);

                try {
                    PageReview pageReview = call.execute().body();
                    totalViews = pageReview.getTotalResults();
                    retorno = pageReview.getReviews();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return retorno;
            }

            @Override
            public void deliverResult(@Nullable List<Review> data) {
                mReviews = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Review>> loader, List<Review> data) {
        if (data != null && data.size() > 0) {
            if (data.get(0).getContent().length() >= 77) {
                mReview.setText(data.get(0).getContent().substring(0, 76) + "...");
            } else {
                mReview.setText(data.get(0).getContent());
            }
            mReviewAuthor.setText(data.get(0).getAuthor());
            mViewAll.setText("View All (" + totalViews + ")");
        } else {
            mReview.setText("There aren't reviews for this movie yet.");
            mReviewAuthor.setVisibility(View.INVISIBLE);
            mViewAll.setVisibility(View.GONE);
        }

//        if(data.isEmpty()){
//            Toast.makeText(this, data.get(0).getAuthor(), Toast.LENGTH_LONG).show();
//            Toast.makeText(this, "VAZIO", Toast.LENGTH_LONG).show();
//        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Review>> loader) {

    }

    /* Click for videos adapter*/

    @Override
    public void onClickHandler(Video video) {
        openVideo("https://www.youtube.com/watch?v=" + video.getKey());
    }
}
