package com.dsatija.movieclips.activities;

import android.content.Intent;
import android.os.Bundle;


import com.dsatija.movieclips.R;
import com.dsatija.movieclips.network.Connectivity;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieYoutubeActivity extends YouTubeBaseActivity {
    private OkHttpClient client = new OkHttpClient();
    public static boolean hasTrailer  = false;
    public static HashMap<Long,String> trialerUrl = new HashMap<>(10);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_youtube);
        if(!Connectivity.isConnected(this)) {
            TastyToast.makeText(this, "Please check your internet connection",
                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
        final Intent intent = getIntent();
        final Long movieId = intent.getLongExtra("movie_id", -1);
        if (movieId != -1) {
            YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.myplayer);
            youTubePlayerView.initialize(getString(R.string.youtube_api_key),
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                final YouTubePlayer youTubePlayer, boolean b) {

                            String url = String.format(
                                    "https://api.themoviedb.org/3/movie/%s/trailers?api_key="
                                            + getString(R.string.movie_db_key), movieId.toString());
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onResponse(Call call, final Response response)
                                        throws IOException {
                                    final String responseData = response.body().string();
                                    try {
                                        JSONObject jsonObject = new JSONObject(responseData);
                                        JSONArray youtubeArray = jsonObject.getJSONArray("youtube");
                                        if (youtubeArray.length() > 0) {
                                            hasTrailer = true;
                                            JSONObject y = youtubeArray.getJSONObject(0);
                                            trialerUrl.put(movieId,y.getString("source"));
                                            youTubePlayer.loadVideo(y.getString("source"));

                                        }else {
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call call, IOException e) {

                                }
                            });
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                YouTubeInitializationResult youTubeInitializationResult) {

                        }
                    });
        } else {
            finish();
        }
    }
}