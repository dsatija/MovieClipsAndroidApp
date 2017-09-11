package com.dsatija.movieclips.network;

import android.app.Activity;
import android.content.Context;

import com.dsatija.movieclips.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dishasatija on 8/30/17.
 */

public class NetworkCall {

    private static OkHttpClient client = new OkHttpClient();
   private static ArrayList<Movie> movies = new ArrayList<>(5);

    public static ArrayList<Movie> asyncCall(String url,final Activity act,final boolean isClear){

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseData);
                            JSONArray movieJsonSearchResults = responseJSON.getJSONArray("results");
                            if (movies != null && isClear) {
                                movies.clear();
                            }
                            movies.addAll(Movie.fromJSONArray(movieJsonSearchResults));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
        return movies;
    }


}

