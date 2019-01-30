package com.thiagomatheusms.famousmovies;

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
import com.thiagomatheusms.famousmovies.Endpoints.GetDataService;
import com.thiagomatheusms.famousmovies.Model.Movie;
import com.thiagomatheusms.famousmovies.Model.Page;
import com.thiagomatheusms.famousmovies.Utilities.EndlessScroll;
import com.thiagomatheusms.famousmovies.Utilities.InternetChecking;
import com.thiagomatheusms.famousmovies.Utilities.RetrofitClientInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderCallbacks<List<Movie>> {

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

    //constant for loader
    private static final int LOADER_ID = 100;

    //constant for Bundle's SavedInstance
    private static final String FILTER_KEY = "filterKey";
    private static final String CURRENT_PAGE_KEY = "currentPageKey";
    public static final String MOVIES_LIST_KEY = "moviesListKey";

    GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.rv_movies);

        layoutManager = new GridLayoutManager(this, 2);
        mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this, mMoviesList);
        mRecyclerViewMovies.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null) {
            filter = savedInstanceState.getString(FILTER_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);

            if (savedInstanceState.containsKey(MOVIES_LIST_KEY)) {
                mMoviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY);

                if (mMoviesList != null) {
                    mMoviesAdapter.setMoviesList(mMoviesList, 1);
                }
            }
        }


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                internetAnswer(msg);
            }
        };

        sendHandlerMessage();

        scroolListener = new EndlessScroll(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    Toast.makeText(MainActivity.this, "No Internet Network!", Toast.LENGTH_SHORT).show();
            }
        };

        mRecyclerViewMovies.addOnScrollListener(scroolListener);

    }

    private void sendHandlerMessage() {
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

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> searchLoader = loaderManager.getLoader(LOADER_ID);
        if (searchLoader == null) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, null, this);
        }

    }

    /* SHOW */

    private void showDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /* MENU */

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

    private void listClear() {
        mCurrentPage = 0;
        mMoviesList.clear();
        mMoviesAdapter.notifyDataSetChanged();
        scroolListener.resetState();
        sendHandlerMessage();
    }

    /* CLICK */

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

    /* LOADER */

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mGithubJson;

            @Override
            protected void onStartLoading() {

                if (mGithubJson != null) {
                    return;
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    mErrorMessage.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                mCurrentPage++;
                Log.i("PAGINA:", mCurrentPage + "");


                service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

                retrofit2.Call<Page> call;

                List<Movie> retorno = null;

                if (filter.equalsIgnoreCase("topRated")) {
                    call = service.getTopRated(mCurrentPage);
                } else {
                    call = service.getPopular(mCurrentPage);
                }


                try {
                    retorno = call.execute().body().getMovies();
                    Log.i("RETORNO: ", "asdasf");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return retorno;
            }

            @Override
            public void deliverResult(List<Movie> data) {
                mGithubJson = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        currentItems = mMoviesAdapter.getItemCount();

        if (data != null) {
            mMoviesList.addAll(data);
            showDataView();
            mMoviesAdapter.setMoviesList(mMoviesList, currentItems);

        } else {
            showErrorMessage();
        }


    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<List<Movie>> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FILTER_KEY, filter);
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
        outState.putParcelableArrayList(MOVIES_LIST_KEY, (ArrayList<Movie>) mMoviesList);
    }
}
