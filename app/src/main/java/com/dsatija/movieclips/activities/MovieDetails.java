package com.dsatija.movieclips.activities;

import static android.view.View.GONE;

import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.adapters.RecommendedVideosAdapter;
import com.dsatija.movieclips.adapters.VideosHorizontalAdapter;
import com.dsatija.movieclips.models.Movie;
import com.dsatija.movieclips.network.Connectivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieDetails extends YouTubeBaseActivity {

    @BindView(R.id.tvMDTitle)
    TextView tvTitle;
    @BindView(R.id.tvMDOverview)
    TextView tvOverview;
    @BindView(R.id.tvMDReleaseDateValue)
    TextView tvReleaseDate;
    @BindView(R.id.ivMDImage)
    ImageView ivImage;
    @BindView(R.id.tvMDRuntimeValue)
    TextView tvRuntime;
    @BindView(R.id.tvMDGenreValue)
    TextView tvGenre;
    @BindView(R.id.tvMDVotingAverageValue)
    RatingBar tvVotingAverage;
    @BindView(R.id.tvMDTotalVotesValue)
    TextView tvTotalVotes;
    @BindView(R.id.player)
    YouTubePlayerView youTubePlayerView;
    @BindView(R.id.tvMDPopularityValue)
    TextView tvPopularity;
    @BindView(R.id.tvMDMoreVideosKey)
    TextView tvMoreVideos;
    @BindView(R.id.tvRecommendedMovies)
    TextView tvRecommendedMovies;
    @BindView(R.id.rvRecommendations)
    RecyclerView rvRecommendations;
    @BindView(R.id.horizontal_recycler_view)
    RecyclerView horizontal_recycler_view;
    @BindView(R.id.ibShare)
    ImageButton ibShare;
    private OkHttpClient client = new OkHttpClient();
    private ArrayList<Integer> recommendationList;
    Movie movie;
    ArrayList<Movie> movies = new ArrayList<Movie>();
    private HashMap<Long,String> movieTrailerUrl = new HashMap<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        if (!Connectivity.isConnected(this)) {
            TastyToast.makeText(this, "Please check your internet connection",
                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
        ButterKnife.bind(this);

        movie = (Movie) getIntent().getSerializableExtra("movieObject");

        String url = String.format(
                "https://api.themoviedb.org/3/movie/%s?api_key=" + getString(R.string.movie_db_key),
                movie.getId());
        asyncCall(url);

        tvTitle.setText(movie.getOriginalTitle());
        tvOverview.setText(movie.getOverview());
        tvReleaseDate.setText(movie.getReleaseDate());
        tvTotalVotes.setText(String.valueOf(movie.getVoteCount()));
        tvVotingAverage.setRating(Float.parseFloat(String.valueOf(movie.getVoteAverage())));
        tvPopularity.setText(String.format("%.2f", movie.getPopularity()));
        int orientation = getResources().getConfiguration().orientation;
        String imagePath =
                (orientation == Configuration.ORIENTATION_PORTRAIT) ? movie.getPosterPath()
                        : movie.getBackdropPath();
        Picasso.with(this).load(imagePath).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).fit().transform(
                new RoundedCornersTransformation(10, 10)).into(ivImage);

        youTubePlayerView.initialize(getString(R.string.youtube_api_key),
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                            YouTubePlayer youTubePlayer, boolean b) {
                        if (null == youTubePlayer) return;
                        String videoUrl = String.format(
                                "https://api.themoviedb.org/3/movie/%s/trailers?api_key="
                                        + getString(R.string.movie_db_key), movie.getId());
                        if (!b) {
                            asyncVideoCall(videoUrl, youTubePlayer);
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                            YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });

        setRecommendations();
    }

    private void setRecommendations() {

        String recommendationURL = String.format(
                "https://api.themoviedb.org/3/movie/%s/recommendations?api_key=" + getString(
                        R.string.movie_db_key),
                movie.getId());
        asynCallRecommendations(recommendationURL);

    }


    private void asynCallRecommendations(String recommendationURL) {
        final ArrayList<Integer> idList = new ArrayList<>(20);
        Request request = new Request.Builder()
                .url(recommendationURL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                MovieDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONArray movieJsonRecommendationResults = jsonObject.getJSONArray(
                                    "results");
                            if (movies != null) {
                                movies.clear();
                            }
                            movies.addAll(Movie.fromJSONArray(movieJsonRecommendationResults));
                            if (movies.size() < 1) {
                                tvRecommendedMovies.setVisibility(GONE);

                            }
                            RecommendedVideosAdapter recommendedVideosAdapter;
                            recommendedVideosAdapter = new RecommendedVideosAdapter(
                                    MovieDetails.this,
                                    movies);
                            LinearLayoutManager horizontalLayoutManagaer
                                    = new LinearLayoutManager(MovieDetails.this,
                                    LinearLayoutManager.HORIZONTAL, false);
                            rvRecommendations.setLayoutManager(horizontalLayoutManagaer);
                            rvRecommendations.setAdapter(recommendedVideosAdapter);
                            recommendedVideosAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void asyncCall(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                MovieDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseData);
                            int runtime = jsonObject.getInt("runtime");
                            int hours = runtime / 60;
                            int minutes = runtime % 60;
                            String time = hours + "h " + minutes + "min";
                            tvRuntime.setText(time);
                            JSONArray genreArray = jsonObject.getJSONArray("genres");
                            String genre = "";
                            for (int i = 0; i < genreArray.length(); i++) {
                                JSONObject genreObject = genreArray.getJSONObject(i);
                                genre += genreObject.getString("name");
                                if (i != genreArray.length() - 1) {
                                    genre += ", ";
                                }
                            }
                            tvGenre.setText(genre);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void asyncVideoCall(String url, final YouTubePlayer youTubePlayer) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                MovieDetails.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<String> horizontalList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(responseData);
                            JSONArray youtubeArray = jsonObject.getJSONArray("youtube");
                            if (youtubeArray.length() > 0) {
                                youTubePlayerView.setVisibility(View.VISIBLE);
                                ibShare.setVisibility(View.VISIBLE);
                                tvMoreVideos.setVisibility(View.VISIBLE);
                                horizontal_recycler_view.setVisibility(View.VISIBLE);
                                youTubePlayer.cueVideo(
                                        youtubeArray.getJSONObject(0).getString("source"));
                                movieTrailerUrl.put(movie.getId(),youtubeArray.getJSONObject(0).getString("source"));
                                for (int i = 0; i < youtubeArray.length(); i++) {
                                    JSONObject y = youtubeArray.getJSONObject(i);
                                    horizontalList.add(y.getString("source"));
                                }
                            } else {
                                horizontal_recycler_view.setVisibility(GONE);
                                tvMoreVideos.setVisibility(GONE);
                                RelativeLayout.LayoutParams params =
                                        (RelativeLayout.LayoutParams) tvRecommendedMovies.getLayoutParams();
                                params.addRule(RelativeLayout.BELOW, R.id.tvMDReleaseDateKey);

                            }

                            VideosHorizontalAdapter videosHorizontalAdapter;

                            videosHorizontalAdapter = new VideosHorizontalAdapter(MovieDetails.this,
                                    horizontalList);
                            LinearLayoutManager horizontalLayoutManagaer
                                    = new LinearLayoutManager(MovieDetails.this,
                                    LinearLayoutManager.HORIZONTAL, false);
                            horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
                            horizontal_recycler_view.setAdapter(videosHorizontalAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void homeAction(View view) {
        Intent intent = new Intent(MovieDetails.this, MovieClipsMainActivity.class);
        startActivity(intent);
    }

    public void ShareIt(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing a favorite movie info...");
        String prefix = "http://www.youtube.com/watch?v=";
        String mShareDetails = "Here is an interesting movie!\n" +
                movie.getOriginalTitle() + "\nand here is a youtube video to check out:\n" +
                prefix + movieTrailerUrl.get(movie.getId());
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareDetails);
        this.startActivity(shareIntent);
    }


}




