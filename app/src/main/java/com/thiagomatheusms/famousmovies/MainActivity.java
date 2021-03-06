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
import com.thiagomatheusms.famousmovies.Database.AppDataBase;
import com.thiagomatheusms.famousmovies.Database.AppExecutors;
import com.thiagomatheusms.famousmovies.Database.MainViewModel;
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
    private String filter = "topRated";
    private int mCurrentPage = 0;
    private int mTotalPage = 0;
    private EndlessScroll scroolListener;
    private int currentItems;
    private List<Movie> mMoviesList = new ArrayList<>();
    // GridLayoutManager layoutManager;

    //constant for loader
    private static final int LOADER_ID = 100;

    //constant for Bundle's SavedInstance
    private static final String FILTER_KEY = "filterKey";
    private static final String CURRENT_PAGE_KEY = "currentPageKey";
    private static final String TOTAL_PAGE_KEY = "totalPageKey";
    private static final String MOVIES_LIST_KEY = "moviesListKey";

    //Database
    private AppDataBase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        mRecyclerViewMovies = (RecyclerView) findViewById(R.id.rv_movies);

        //layoutManager = new GridLayoutManager(this, 2);
        //mRecyclerViewMovies.setLayoutManager(layoutManager);
        mRecyclerViewMovies.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this, mMoviesList);
        mRecyclerViewMovies.setAdapter(mMoviesAdapter);

        mDb = AppDataBase.getInstance(this);

        if (savedInstanceState != null) {
            filter = savedInstanceState.getString(FILTER_KEY);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE_KEY);
            mTotalPage = savedInstanceState.getInt(TOTAL_PAGE_KEY);
        }

        if (filter.equalsIgnoreCase("popular") || filter.equalsIgnoreCase("topRated")) {
            if (savedInstanceState != null) {
                if (savedInstanceState.containsKey(MOVIES_LIST_KEY)) {
                    mMoviesList = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY);

                    if (mMoviesList != null) {
                        mMoviesAdapter.setMoviesList(mMoviesList, 1);
                    }
                }
            }
            handlerInitialize();
        } else {
            getFavoritesMovies();
        }

    }

    /* Handler Methods Associates */

    private void handlerInitialize() {
        if (filter.equalsIgnoreCase("popular") || filter.equalsIgnoreCase("topRated")) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    internetAnswer(msg);
                }
            };

            sendHandlerMessage();

            scroolListener = new EndlessScroll((GridLayoutManager) mRecyclerViewMovies.getLayoutManager()) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    if (filter.equalsIgnoreCase("popular") || filter.equalsIgnoreCase("topRated")) {
                        sendHandlerMessage();
                    }
                }
            };

            mRecyclerViewMovies.addOnScrollListener(scroolListener);
        }
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

    /* Show Views */

    private void showDataView() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerViewMovies.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerViewMovies.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    /* Menu */

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
                filter = "popular";
                setTitle("Popular");
                listClear();
                break;

            case R.id.action_filter_topRated:
                filter = "topRated";
                setTitle("Top Rated");
                listClear();
                break;

            case R.id.action_filter_favorites:
                filter = "favorites";
                setTitle("Favorites");
                listFavorites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void listFavorites() {
        mCurrentPage = 0;
        mTotalPage = 0;
        mMoviesList.clear();
        mMoviesAdapter.notifyDataSetChanged();
        if (scroolListener != null) {
            scroolListener.resetState();
        }

        getFavoritesMovies();


//        if (mDb.movieDao().getFavoriteMovies() != null) {
//
//
//        } else {
//            showErrorMessage();
//        }
    }

    private void listClear() {
        mCurrentPage = 0;
        mTotalPage = 0;
        mMoviesList.clear();
        mMoviesAdapter.notifyDataSetChanged();

        if (handler == null) {
            handlerInitialize();
        }
        if (scroolListener != null) {
            scroolListener.resetState();
        }
        sendHandlerMessage();
    }

    private void getFavoritesMovies() {

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getFavoriteMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movieList) {
                Log.d("teste", "Updating list of tasks from LiveData in ViewModel");
//                mMoviesList = movieList;
                if(filter.equalsIgnoreCase("favorites")) {
                    showDataView();
                    mMoviesAdapter.setMoviesList(movieList, currentItems);
                }
            }
        });
    }

    /* LOADER */

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mMovies;

            @Override
            protected void onStartLoading() {

                if (mMovies != null || mCurrentPage > mTotalPage) {
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
                    Page page = call.execute().body();
                    retorno = page.getMovies();
                    mTotalPage = page.getTotalPages();
                    Log.i("RETORNO: ", "asdasf");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return retorno;
            }

            @Override
            public void deliverResult(List<Movie> data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        mLoadingIndicator.setVisibility(View.GONE);
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

    /* CLICK */

    @Override
    public void onClickHandler(Movie movieClicked) {
        Intent intent = new Intent(getBaseContext(), DetailMovie.class);
        intent.putExtra("ID", movieClicked.getId());
        intent.putExtra("TITLE", movieClicked.getTitle());
        intent.putExtra("POSTER_PATH", movieClicked.getPoster_path());
        intent.putExtra("ORIGINAL_TITLE", movieClicked.getOriginal_title());
        intent.putExtra("SYNOPSIS", movieClicked.getSynopsis());
        intent.putExtra("DATE_RELEASE", movieClicked.getDate_release());
        intent.putExtra("VOTE_AVERAGE", movieClicked.getVote_average());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FILTER_KEY, filter);
        outState.putInt(CURRENT_PAGE_KEY, mCurrentPage);
        outState.putInt(TOTAL_PAGE_KEY, mTotalPage);
        outState.putParcelableArrayList(MOVIES_LIST_KEY, (ArrayList<Movie>) mMoviesList);
    }
}
