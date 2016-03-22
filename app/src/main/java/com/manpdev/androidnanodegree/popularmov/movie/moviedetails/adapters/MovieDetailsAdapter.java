package com.manpdev.androidnanodegree.popularmov.movie.moviedetails.adapters;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manpdev.androidnanodegree.popularmov.R;
import com.manpdev.androidnanodegree.popularmov.movie.data.model.MovieModel;
import com.manpdev.androidnanodegree.popularmov.movie.data.model.wrapper.MovieExtras;
import com.manpdev.androidnanodegree.popularmov.movie.moviedetails.adapters.holders.MovieDetailsReviewVH;
import com.manpdev.androidnanodegree.popularmov.movie.moviedetails.adapters.holders.MovieDetailsTrailerVH;
import com.manpdev.androidnanodegree.popularmov.movie.moviedetails.adapters.holders.MovieDetailsVH;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by novoa.pro@gmail.com on 2/21/16
 */
public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_DESCRIPTION, TYPE_TRAILER, TYPE_REVIEW})
    public @interface MovieDetailItemType{}
    public static final int TYPE_DESCRIPTION = 0;
    public static final int TYPE_TRAILER = 1;
    public static final int TYPE_REVIEW = 2;


    private MovieModel mMovie;
    private MovieExtras mMovieExtras;

    public MovieDetailsAdapter(Context context, MovieModel movie) {
        this.mMovie = movie;
    }

    public void setMovieExtras(MovieExtras mMovieExtras) {
        if(mMovie.getId() != mMovieExtras.getmMovieId())
            return;

        this.mMovieExtras = mMovieExtras;
    }

    @Override
    @MovieDetailItemType
    public int getItemViewType(int position) {
        if(mMovieExtras != null){
            if((position - 1) <= mMovieExtras.getExtrasTotal() - mMovieExtras.getTrailers().size())
                return TYPE_TRAILER;

            if((position - 1) > mMovieExtras.getExtrasTotal() - mMovieExtras.getTrailers().size())
                return TYPE_REVIEW;
        }

        return TYPE_DESCRIPTION;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View root;
        switch (viewType){
            case TYPE_TRAILER:
                root = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_movie_trailer, parent, false);
                return new MovieDetailsTrailerVH(root);

            case TYPE_REVIEW:
                root = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_movie_review, parent, false);
                return new MovieDetailsReviewVH(root);

            case TYPE_DESCRIPTION:
                default:
                root = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_movie_details, parent, false);
                return new MovieDetailsVH(root);
        }
    }

    // TODO: 3/22/16 double check this method.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case TYPE_DESCRIPTION:
                ((MovieDetailsVH)holder).bindContent(mMovie);
                break;
            case TYPE_TRAILER:
                //((MovieDetailsTrailerVH)holder).bindContent(mMovieExtras.getTrailers().get((position - 1)));
                break;
            case TYPE_REVIEW:
                ((MovieDetailsReviewVH)holder).bindContent(mMovieExtras.getReviews().get((position - 1 - mMovieExtras.getTrailers().size())));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMovieExtras == null ? 1 : 1 + mMovieExtras.getExtrasTotal();
    }

}
