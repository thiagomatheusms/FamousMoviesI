package com.thiagomatheusms.famousmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Debug;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thiagomatheusms.famousmovies.Adapter.MoviesAdapter;
import com.thiagomatheusms.famousmovies.Adapter.ReviewsAdapter;
import com.thiagomatheusms.famousmovies.Database.AppDataBase;
import com.thiagomatheusms.famousmovies.Database.AppExecutors;
import com.thiagomatheusms.famousmovies.Database.MainViewModel;
import com.thiagomatheusms.famousmovies.Endpoints.GetDataService;
import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Page;
import com.thiagomatheusms.famousmovies.Model.PageReview;
import com.thiagomatheusms.famousmovies.Model.Review;
import com.thiagomatheusms.famousmovies.Utilities.EndlessScroll;
import com.thiagomatheusms.famousmovies.Utilities.InternetChecking;
import com.thiagomatheusms.famousmovies.Utilities.RetrofitClientInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Review>> {

    /* Views */
    private RecyclerView mRecyclerViewReviews;
    private ProgressBar mLoadingProgressBar;
    private TextView mErrorMessage;

    /* Adapter */
    private ReviewsAdapter reviewsAdapter;

    /* Others */
    private List<Review> mReviewsList = new ArrayList<>();
    private Movie movieRequest;
    private GetDataService service;
    private EndlessScroll scroolListener;

    /* Constant for Loader */
    private static final int REVIEW_LOADER_ID = 400;

    /* Endless Scroll */
    private int mCurrentPage = 0;
    private int mTotalPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator_review);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message_review);
        mRecyclerViewReviews = (RecyclerView) findViewById(R.id.rv_reviews);
        mRecyclerViewReviews.setHasFixedSize(true);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("MOVIE_KEY")) {
            movieRequest = intent.getParcelableExtra("MOVIE_KEY");
        }

        scroolListener = new EndlessScroll((GridLayoutManager) mRecyclerViewReviews.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, (LoaderCallbacks<Object>) getApplicationContext());

            }
        };

        reviewsAdapter = new ReviewsAdapter(mReviewsList);
        mRecyclerViewReviews.setAdapter(reviewsAdapter);

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);

    }

    private void showDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerViewReviews.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerViewReviews.setVisibility(View.INVISIBLE);
    }



    /* LOADER */

    @Override
    public Loader<List<Review>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<List<Review>>(this) {

            List<Review> reviews;

            @Override
            protected void onStartLoading() {
                if (reviews != null || mCurrentPage > mTotalPage) {
                    return;
                } else {
                    mLoadingProgressBar.setVisibility(View.VISIBLE);
                    mErrorMessage.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Review> loadInBackground() {
                mCurrentPage++;

                service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

                retrofit2.Call<PageReview> call;

                List<Review> mReviewsReturn = null;

                call = service.getReviews(movieRequest.getId());

                try {
                    PageReview pageReview = call.execute().body();
                    mTotalPage = pageReview.getTotalPages();
                    mReviewsReturn = pageReview.getReviews();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return mReviewsReturn;
            }

            @Override
            public void deliverResult(List<Review> data) {
                reviews = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
        mLoadingProgressBar.setVisibility(View.GONE);

        if (data != null) {
            mReviewsList.addAll(data);
            showDataView();
            reviewsAdapter.setReviewsList(mReviewsList);

        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Review>> loader) {

    }
}