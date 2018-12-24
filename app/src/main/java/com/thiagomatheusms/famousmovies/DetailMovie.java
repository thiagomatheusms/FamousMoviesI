package com.thiagomatheusms.famousmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thiagomatheusms.famousmovies.Adapter.MoviesAdapter;

public class DetailMovie extends AppCompatActivity {

    private TextView mTitleMovie, mOriginalTitleMovie, mSynopsisMovie, mDateReleaseMovie;
    private ImageView mPosterPathMovie;
    private RatingBar mVoteAverageMovie;

    private static final String BASE_URL_IMG_2 = "http://image.tmdb.org/t/p/w185/";

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

        Intent intentStartThisActivity = getIntent();

        if (intentStartThisActivity != null) {
            if (intentStartThisActivity.hasExtra("TITLE") && intentStartThisActivity.hasExtra("POSTER_PATH")
                    && intentStartThisActivity.hasExtra("ORIGINAL_TITLE") && intentStartThisActivity.hasExtra("SYNOPSIS")
                    && intentStartThisActivity.hasExtra("DATE_RELEASE") && intentStartThisActivity.hasExtra("VOTE_AVERAGE")) {

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

    }

    public float setRatingVote(float voteAverage){
        float average = ((voteAverage * 5)/10);
        return average;
    }
}
