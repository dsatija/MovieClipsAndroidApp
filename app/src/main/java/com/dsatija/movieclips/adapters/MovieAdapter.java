package com.dsatija.movieclips.adapters;

import static android.R.drawable.btn_star_big_off;
import static android.R.drawable.btn_star_big_on;
import static android.R.drawable.star_big_off;
import static android.R.drawable.star_big_on;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.dsatija.movieclips.R;
import com.dsatija.movieclips.activities.MovieYoutubeActivity;
import com.dsatija.movieclips.database.DatabaseUtils;
import com.dsatija.movieclips.models.Movie;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MovieAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;
    private final int POPULAR = 0, UNPOPULAR = 1 ,VIEWTYPE_LOADER=2;
    private boolean showLoader;
    private DatabaseUtils du ;
    private String trailerUrl;
    private FragmentManager fm;


    private Context getContext() {
        return mContext;
    }

    public MovieAdapter(Context context, List<Movie> movies) {
        mMovies = movies;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {

        /*
        if (position == mMovies.size() - 1) {
            return VIEWTYPE_LOADER;
        }
        */
        if (mMovies.get(position).isPopularMovie()) {
            return POPULAR;
        } else {
            return UNPOPULAR;
        }

    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.ivPlayIcon)
        ImageView ivPlayIcon;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvOverview)
        TextView tvOverview;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class LoaderViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar getProgressBar() {
            return progressBar;
        }

        private ProgressBar progressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEWTYPE_LOADER) {

            // Your Loader XML view here
            view = inflater.inflate(R.layout.footer, parent, false);

            // Your LoaderViewHolder class
            return new LoaderViewHolder(view);

        }

        else if (viewType == UNPOPULAR) {
            view = inflater.inflate(R.layout.unpopular_movie, parent, false);
            viewHolder = new UnPopularMovieHolder(view);
        } else {
            view = inflater.inflate(R.layout.popular_movie, parent, false);
            viewHolder = new PopularMovieHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        if (viewHolder instanceof LoaderViewHolder) {
            LoaderViewHolder loaderViewHolder = (LoaderViewHolder)viewHolder;
            if (showLoader) {
                loaderViewHolder.progressBar.setVisibility(View.VISIBLE);
            } else {
                loaderViewHolder.progressBar.setVisibility(View.GONE);
            }

            return;
        }

        if (viewHolder.getItemViewType() == POPULAR) {
            PopularMovieHolder mh1 = (PopularMovieHolder) viewHolder;
            configurePopularMovieViewHolder(mh1, position);

        }
        else  {
            UnPopularMovieHolder mh2 = (UnPopularMovieHolder) viewHolder;
            configureUnpopularMovieViewHolder(mh2, position);

        }


    }

    private void configurePopularMovieViewHolder(final PopularMovieHolder viewHolder, final int position) {
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
        if(readState(movie)) {
            viewHolder.ibFav.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    android.R.drawable.star_big_on));
        }
        else{
            viewHolder.ibFav.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    android.R.drawable.star_big_off));

        }
        viewHolder.ibFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavourite = readState(movie);

                if (isFavourite) {
                    viewHolder.ibFav.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            android.R.drawable.star_big_off));
                    isFavourite = false;
                    saveState(isFavourite,movie.getId());
                    System.out.println("unfavorited");
                    new MovieFragmentPagerAdapter(fm,getContext()).notifyDataSetChanged();

                } else {
                    viewHolder.ibFav.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            android.R.drawable.star_big_on));
                    isFavourite = true;
                    saveState(isFavourite,movie.getId());
                    System.out.println("favorited");
                    new MovieFragmentPagerAdapter( fm,getContext()).notifyDataSetChanged();

                }

            }
        });

    }

    private void configureUnpopularMovieViewHolder(final UnPopularMovieHolder viewHolder, final int position) {
        final Movie movie = mMovies.get(position);
        viewHolder.ivImage.setImageResource(0);
        viewHolder.tvTitle.setText(movie.getOriginalTitle());
        viewHolder.tvOverview.setText(movie.getOverview());
        int orientation = getContext().getResources().getConfiguration().orientation;
        String imagePath =
                (orientation == Configuration.ORIENTATION_PORTRAIT) ? movie.getPosterPath()
                        : movie.getBackdropPath();
        Picasso.with(getContext()).load(imagePath).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).fit().transform(
                new RoundedCornersTransformation(10, 10)).into(viewHolder.ivImage);
        if(readState(movie)) {
            viewHolder.ibfav1.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    android.R.drawable.star_big_on));
        }
        else{
            viewHolder.ibfav1.setImageDrawable(ContextCompat.getDrawable(getContext(),
                    android.R.drawable.star_big_off));
        }

        viewHolder.ibfav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFavourite = readState(movie);

                if (isFavourite) {
                    viewHolder.ibfav1.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            android.R.drawable.star_big_off));
                    isFavourite = false;
                    saveState(isFavourite,movie.getId());
                    System.out.println("unfavorited");
                    new MovieFragmentPagerAdapter( fm,getContext()).notifyDataSetChanged();

                } else {
                    viewHolder.ibfav1.setImageDrawable(ContextCompat.getDrawable(getContext(),
                            android.R.drawable.star_big_on));

                    isFavourite = true;
                    saveState(isFavourite,movie.getId());
                    System.out.println("favorited");
                    new MovieFragmentPagerAdapter(fm,getContext()).notifyDataSetChanged();
                }

            }
        });
        viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CheckTrailer.checkTrailer(movie.getId(),getContext());
               // trailerUrl = CheckTrailer.trailerUrl.get(movie.getId());
                //if(trailerUrl != null){
                    Intent intent = new Intent(getContext(), MovieYoutubeActivity.class);
                    intent.putExtra("movie_id", movie.getId());
                    getContext().startActivity(intent);
                //}
                //else if (trailerUrl == null){
                   // TastyToast.makeText(getContext(),"Trailer not available",TastyToast.LENGTH_SHORT,TastyToast.INFO);
               // }
            }
        });
    }
    @Override
    public int getItemCount() {
       // return mMovies.size()+1;
        return mMovies == null ? 0 : mMovies.size();
    }

    private void saveState(boolean isFavourite,long movieId) {
        du.updateData(movieId,isFavourite == true ? 1:0);
    }

    private boolean readState(Movie movie) {
        du = new DatabaseUtils(getContext());
        return du.readData(movie,movie.getId());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    public void showLoading(boolean status) {
        showLoader = status;
    }

}