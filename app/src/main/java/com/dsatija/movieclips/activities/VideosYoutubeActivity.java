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

import okhttp3.OkHttpClient;

public class VideosYoutubeActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_youtube);
        if(!Connectivity.isConnected(this)) {
            TastyToast.makeText(this, "Please check your internet connection",
                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
        Intent intent = getIntent();
        final String movieId = intent.getStringExtra("movie_id");
        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) findViewById(R.id.myplayer);
        youTubePlayerView.initialize(getString(R.string.youtube_api_key),
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                            final YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.loadVideo(movieId);
                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                            YouTubeInitializationResult youTubeInitializationResult) {
                    }
                });
    }
}