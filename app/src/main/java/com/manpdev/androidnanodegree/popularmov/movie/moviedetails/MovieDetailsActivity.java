package com.manpdev.androidnanodegree.popularmov.movie.moviedetails;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.manpdev.androidnanodegree.popularmov.R;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String MOVIE_DETAIL_TAG = "DETAIL_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if(savedInstanceState == null){
            updateMovieDetailFragment(getIntent().getExtras());
        }

        ActionBar aBar = getSupportActionBar();
        if(aBar != null) {
            aBar.setTitle(R.string.movie_details_activity_title);
            aBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMovieDetailFragment(Bundle arg) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments(arg);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_movie_details_container, fragment, MOVIE_DETAIL_TAG)
                .commit();
    }
}
