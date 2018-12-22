package com.thiagomatheusms.famousmovies;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thiagomatheusms.famousmovies.Adapter.MoviesAdapter;
import com.thiagomatheusms.famousmovies.Endpoints.GetDataService;
import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Page;
import com.thiagomatheusms.famousmovies.Utilities.InternetChecking;
import com.thiagomatheusms.famousmovies.Utilities.RetrofitClientInstance;

import java.io.IOException;
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

    private Handler handler;

    //Endless
    int previousTotal = 0;
    boolean loading = true;
    int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    int i = 1;
    List<Movie> teste;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerViewMovies.setAdapter(mMoviesAdapter);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                internetAnswer(msg);
            }
        };

        InternetChecking internetChecking = new InternetChecking(handler);
        internetChecking.start();
    }

    private void internetAnswer(Message msg) {
        if (msg.obj.toString().equalsIgnoreCase("ok")) {
            Toast.makeText(this, "TEM INTERNET", Toast.LENGTH_SHORT).show();
            request("popular");
        } else if (msg.obj.toString().equalsIgnoreCase("error")) {
            Toast.makeText(this, "SEM INTERNET", Toast.LENGTH_SHORT).show();
            showErrorMessage();
        }
    }

    private void request(String filter) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);

        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<Page> call;

        if (filter.equalsIgnoreCase("topRated")) {
            call = service.getTopRated(i);
        } else {
            call = service.getPopular(i);
        }

        call.enqueue(new Callback<Page>() {
            @Override
            public void onResponse(Call<Page> call, Response<Page> response) {
                Page page = response.body();
                generateData(page.getMovies());
            }

            @Override
            public void onFailure(Call<Page> call, Throwable t) {
                showErrorMessage();
            }
        });
    }

    private void generateData(List<Movie> movieList) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movieList != null) {
            showDataView();
            mMoviesAdapter.setMoviesList(movieList);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();

        switch (idItem) {
            case R.id.action_filter_popular:
                request("popular");
                break;

            case R.id.action_filter_topRated:
                request("topRated");
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


//        public void requisita(){
//        mRecyclerViewMovies.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                visibleItemCount = mRecyclerViewMovies.getChildCount();
//                totalItemCount = layoutManager.getItemCount();
//                firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
//
//                if (loading) {
//                    if (totalItemCount > previousTotal) {
//                        loading = false;
//                        previousTotal = totalItemCount;
//                    }
//                }
//                if (!loading && (totalItemCount - visibleItemCount)
//                        <= (firstVisibleItem + visibleThreshold)) {
//                    // End has been reached
//
//                    Log.i("Yaeye!", "end called");
//
//                    // Do something
//                    request("teste");
//
//                    loading = true;
//                }
//            }
//        });
//    }


}
