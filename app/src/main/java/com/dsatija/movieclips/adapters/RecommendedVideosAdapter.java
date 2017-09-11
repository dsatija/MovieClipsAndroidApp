package com.dsatija.movieclips.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.activities.MovieDetails;
import com.dsatija.movieclips.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by dishasatija on 9/1/17.
 */

public class RecommendedVideosAdapter extends
        RecyclerView.Adapter<RecommendedVideosAdapter.MyViewHolder> {

    private List<Movie> movieList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView txtView;

        public MyViewHolder(View view) {
            super(view);
            txtView = (ImageView) view.findViewById(R.id.txtView);
        }
    }

    public RecommendedVideosAdapter(Context context, List<Movie> movieList) {
        this.movieList = movieList;
        mContext = context;
    }

    @Override
    public RecommendedVideosAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);
        return new RecommendedVideosAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecommendedVideosAdapter.MyViewHolder holder,
            final int position) {

        Picasso.with(mContext).load(movieList.get(position).getPosterPath()).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).fit().into(holder.txtView);
        holder.txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetails.class);
                intent.putExtra("movieObject", movieList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
