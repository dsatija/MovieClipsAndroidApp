package com.dsatija.movieclips.activities;

import static com.dsatija.movieclips.R.id.swipeContainer;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.adapters.ItemClickSupport;
import com.dsatija.movieclips.adapters.SearchMovieAdapter;
import com.dsatija.movieclips.models.Movie;
import com.dsatija.movieclips.network.Connectivity;
import com.dsatija.movieclips.network.NetworkCall;
import com.dsatija.movieclips.utils.EndlessRecyclerViewScrollListener;
import com.sdsmdg.tastytoast.TastyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultsActivity extends AppCompatActivity {

    ArrayList<Movie> movies = new ArrayList<Movie>();
    private EndlessRecyclerViewScrollListener scrollListener;
    SearchMovieAdapter mSearchMovieAdapter = new SearchMovieAdapter(this, movies);
    private OkHttpClient client = new OkHttpClient();
    @BindView(R.id.slvMovies)
    RecyclerView slvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        // View view = inflater.inflate(R.layout.fragment_page, container, false);
        ButterKnife.bind(this);
        if (!Connectivity.isConnected(this)) {
            TastyToast.makeText(this, "Please check your internet connection",
                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
        final String QUERY = getIntent().getStringExtra("key");
        slvItems.setAdapter(mSearchMovieAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        slvItems.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(QUERY, page + 1);
            }
        };
        slvItems.addOnScrollListener(scrollListener);
        // Toast.makeText(this,"Searching for: " + query,Toast.LENGTH_LONG).show();

        ItemClickSupport.addTo(slvItems).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Movie movie = movies.get(position);
                        Intent intent = new Intent(getApplicationContext(), MovieDetails.class);
                        intent.putExtra("movieObject", movie);
                        startActivity(intent);
                        finish();
                    }
                }
        );
        search(QUERY);

    }

    private void loadNextDataFromApi(String query, int page) {

        String url = "https://api.themoviedb.org/3/search/movie?query=" + query
                + "&api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&page=" + page;
        asyncCall(url, this, false);

    }


    private void search(String query) {
        //call the url

        String url = String.format("https://api.themoviedb.org/3/search/movie?query=%s"
                + "&api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", query);
        asyncCall(url, this, true);


    }


    public ArrayList<Movie> asyncCall(String url,final Activity act,final boolean isClear){

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
                            mSearchMovieAdapter.notifyDataSetChanged();

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


    public void homeActionSearch(View view) {

        Intent intent = new Intent(SearchResultsActivity.this, MovieClipsMainActivity.class);
        startActivity(intent);
    }
}

