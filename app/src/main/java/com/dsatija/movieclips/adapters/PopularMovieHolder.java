package com.dsatija.movieclips.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatija.movieclips.R;

public class PopularMovieHolder extends RecyclerView.ViewHolder {
    ImageView ivImage;
    ImageView ivPlayIcon;
    TextView tvTitle;
    TextView tvOverview;
    ImageButton ibFav;



    public PopularMovieHolder(View itemView) {
        super(itemView);
        ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
        ivPlayIcon = (ImageView) itemView.findViewById(R.id.ivPlayIcon);
        ibFav = (ImageButton) itemView.findViewById(R.id.ibFav);
    }

    public ImageView getIvImage() {
        return ivImage;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public TextView getTvOverview() {
        return tvOverview;
    }

    public ImageView getIvPlayIcon() {
        return ivPlayIcon;
    }

    public ImageButton getIbFav() {
        return ibFav;
    }
}
