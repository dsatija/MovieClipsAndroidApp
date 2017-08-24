package com.dsatija.movieclips.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.activities.MovieYoutubeActivity;
import com.dsatija.movieclips.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by dishasatija on 8/20/17.
 */

public class SearchMovieAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;

    private Context getContext() {
        return mContext;
    }

    public SearchMovieAdapter(Context context, List<Movie> movies) {
        mMovies = movies;
        mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.popular_movie, parent, false);
        viewHolder = new PopularMovieHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PopularMovieHolder sh1 = (PopularMovieHolder) holder;
        configureSearchMovieViewHolder(sh1, position);
    }

    private void configureSearchMovieViewHolder(PopularMovieHolder viewHolder, int position) {

        final Movie movie = mMovies.get(position);
        viewHolder.ivImage.setImageResource(0);
        viewHolder.tvTitle.setText(movie.getOriginalTitle());
        viewHolder.tvOverview.setText(movie.getOverview());
        String imagePath = movie.getBackdropPath();
        Picasso.with(getContext()).load(imagePath).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).fit().transform(
                new RoundedCornersTransformation(10, 10)).into(viewHolder.ivImage);


        viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MovieYoutubeActivity.class);
                intent.putExtra("movie_id", movie.getId());
                getContext().startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvOverview)
        TextView tvOverview;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
