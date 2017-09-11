package com.dsatija.movieclips.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsatija.movieclips.R;

public class UnPopularMovieHolder extends RecyclerView.ViewHolder {
    ImageView ivImage;
    TextView tvTitle;
    TextView tvOverview;
    ImageView ivPlayIcon2;
    ImageButton ibShare;
    ImageButton ibfav1;



    public UnPopularMovieHolder(View itemView) {
        super(itemView);
        ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        ivPlayIcon2 = (ImageView) itemView.findViewById(R.id.ivPlayIcon2);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
        ibShare = (ImageButton) itemView.findViewById(R.id.ibShare);
        ibfav1 = (ImageButton) itemView.findViewById(R.id.ibFav1);
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

    public ImageView getIvPlayIcon2() {
        return ivPlayIcon2;
    }
    public ImageButton getIbShare() {
        return ibShare;
    }

    public ImageButton getIbfav1() {
        return ibfav1;
    }
}
