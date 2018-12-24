package com.thiagomatheusms.famousmovies;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thiagomatheusms.famousmovies.Adapter.MoviesAdapter;
import com.thiagomatheusms.famousmovies.Endpoints.GetDataService;
import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Page;
import com.thiagomatheusms.famousmovies.Utilities.EndlessScroll;
import com.thiagomatheusms.famousmovies.Utilities.InternetChecking;
import com.thiagomatheusms.famousmovies.Utilities.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    //Views
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerViewMovies;
    private TextView mErrorMessage;

    //RecyclerViewAdapter
    private MoviesAdapter mMoviesAdapter;

    //Retrofit Interface
    private GetDataService service;

    //Handler for send internet messages
    private Handler handler;

    //Others variables
    private String filter = "popular";
    private int mCurrentPage = 0;
    private EndlessScroll scroolListener;
    private int currentItems;
    private List<Movie> mMoviesList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.rv_movies);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this, mMoviesList);
        mRecyclerViewMovies.setAdapter(mMoviesAdapter);

        scroolListener = new EndlessScroll(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                InternetChecking internetChecking = new InternetChecking(handler);
                internetChecking.start();
            }
        };

        mRecyclerViewMovies.addOnScrollListener(scroolListener);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                internetAnswer(msg);
            }
        };

        sendHandlerMessage();

    }

    private void sendHandlerMessage(){
        InternetChecking internetChecking = new InternetChecking(handler);
        internetChecking.start();
    }

    private void internetAnswer(Message msg) {
        if (msg.obj.toString().equalsIgnoreCase("ok")) {
            getData();
        } else if (msg.obj.toString().equalsIgnoreCase("error")) {
            Toast.makeText(this, "No Internet Network!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);

        mCurrentPage++;

        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<Page> call;

        if (filter.equalsIgnoreCase("topRated")) {
            call = service.getTopRated(mCurrentPage);
        } else {
            call = service.getPopular(mCurrentPage);
        }

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {
                Page page = response.body();
                if (page != null) {
                    generateData(page.getMovies());
                }else{
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                showErrorMessage();
            }
        });
    }

    private void generateData(List<Movie> movieList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        currentItems = mMoviesAdapter.getItemCount();

        if (movieList != null) {
            showDataView();
            mMoviesAdapter.setMoviesList(movieList,currentItems);

        } else {
            showErrorMessage();
        }
    }

    private void showDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    private void listClear(){
        mCurrentPage = 0;
        mMoviesList.clear();
        mMoviesAdapter.notifyDataSetChanged();
        scroolListener.resetState();
        sendHandlerMessage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();

        switch (idItem) {
            case R.id.action_filter_popular:
                listClear();
                filter = "popular";
                setTitle("Popular");
                break;

            case R.id.action_filter_topRated:
                listClear();
                filter = "topRated";
                setTitle("Top Rated");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickHandler(Movie movieClicked) {
        Intent intent = new Intent(getBaseContext(), DetailMovie.class);
        intent.putExtra("TITLE", movieClicked.getTitle());
        intent.putExtra("POSTER_PATH", movieClicked.getPoster_path());
        intent.putExtra("ORIGINAL_TITLE", movieClicked.getOriginal_title());
        intent.putExtra("SYNOPSIS", movieClicked.getSynopsis());
        intent.putExtra("DATE_RELEASE", movieClicked.getDate_release());
        intent.putExtra("VOTE_AVERAGE", movieClicked.getVote_average());
        startActivity(intent);
    }
}
