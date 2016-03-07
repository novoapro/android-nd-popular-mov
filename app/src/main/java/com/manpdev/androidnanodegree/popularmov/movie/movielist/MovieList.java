package com.manpdev.androidnanodegree.popularmov.movie.movielist;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.manpdev.androidnanodegree.popularmov.R;
import com.manpdev.androidnanodegree.popularmov.movie.Preferences;
import com.manpdev.androidnanodegree.popularmov.movie.data.operation.base.Observer;
import com.manpdev.androidnanodegree.popularmov.movie.data.operation.GetMovieListOperation;
import com.manpdev.androidnanodegree.popularmov.movie.data.model.MovieModel;
import com.manpdev.androidnanodegree.popularmov.movie.data.model.MovieWrapperModel;
import com.manpdev.androidnanodegree.popularmov.movie.data.provider.MovieContract;
import com.manpdev.androidnanodegree.popularmov.movie.data.provider.MoviesProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by novoa.pro@gmail.com on 2/14/16
 */
public class MovieList implements MovieListContract.PopularMovieListPresenter,
        LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MovieList";

    private Context mContext;

    private static final int MOVIE_LOADER_ID = 214;
    private LoaderManager mLoadManager;

    private MovieListContract.PopularMovieListView mView;

    private GetMovieListOperation mMovieListOperation;
    private Observer<MovieWrapperModel> mMovieListObserver = new Observer<MovieWrapperModel>() {
        @Override
        public void onResult(MovieWrapperModel data) {
            Log.d(TAG, "onResult: " + data.getResults().size());
            mView.showMovieList(data.getResults());
        }

        @Override
        public void onError(Throwable th) {
            Log.e(TAG, "onError: ", th);
            mView.showMessage(R.string.sync_data_failed);
        }
    };

    public MovieList(Context context, MovieListContract.PopularMovieListView view, LoaderManager loaderManager) {
        this.mContext = context;
        this.mView = view;
        this.mLoadManager = loaderManager;
        this.mMovieListOperation = new GetMovieListOperation(context);
    }

    @Override
    public void loadMovieList() {
        if (Preferences.getSelectionOption(mContext).equals(Preferences.FAVORITES))
            mLoadManager.initLoader(MOVIE_LOADER_ID, null, this);
        else {
            this.mMovieListOperation
                    .setSelectionOption(Preferences.getSelectionOption(mContext))
                    .execute();
        }
    }

    @Override
    public void dismissMovieList() {
        mLoadManager.destroyLoader(MOVIE_LOADER_ID);
    }

    @Override
    public void register() {
        Preferences.registerPreferencesListener(mContext, this);
        this.mMovieListOperation.subscribe(this.mMovieListObserver);
    }

    @Override
    public void unregister() {
        Preferences.unregisterPreferencesListener(mContext, this);
        this.mMovieListOperation.unsubscribe();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                MovieContract.MovieEntry.baseURI(MoviesProvider.sAUTHORITY),
                MovieContract.MovieEntry.COLUMNS_ALL,
                null,
                null,
                String.format("%s DESC", MovieContract.MovieEntry._ID)
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, String.format("onLoadFinished : %d items", data.getCount()));
        mView.showMovieList(buildMovieModelList(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(Preferences.MOVIE_SELECTION_OPTION))
            refreshMovieList();
    }

    private void refreshMovieList() {
        if (Preferences.getSelectionOption(mContext).equals(Preferences.FAVORITES))
            mLoadManager.restartLoader(MOVIE_LOADER_ID, null, this);
        else {
            this.mMovieListOperation
                    .setSelectionOption(Preferences.getSelectionOption(mContext))
                    .execute();
        }
    }


    private List<MovieModel> buildMovieModelList(Cursor data) {
        List<MovieModel> result = new ArrayList<>();
        if (data == null || !data.moveToFirst())
            return result;

        MovieModel model;

        while (!data.isAfterLast()) {
            model = new MovieModel();
            model.setId(data.getInt(1));
            model.setTitle(data.getString(2));
            model.setPosterPath(data.getString(3));
            model.setOverview(data.getString(4));
            model.setVoteAverage(data.getDouble(5));
            model.setReleaseDate(new SimpleDateFormat(MovieModel.RElEASE_DATE_FORMAT, Locale.US)
                    .format(new Date(data.getLong(6))));
            model.setPopularity(data.getDouble(7));

            result.add(model);
            data.moveToNext();
        }
        return result;
    }
}
