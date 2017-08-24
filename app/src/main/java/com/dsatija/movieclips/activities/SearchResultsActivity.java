package com.dsatija.movieclips.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.adapters.ItemClickSupport;
import com.dsatija.movieclips.adapters.SearchMovieAdapter;
import com.dsatija.movieclips.models.Movie;

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
        String query = getIntent().getStringExtra("key");
        slvItems.setAdapter(mSearchMovieAdapter);
        slvItems.setLayoutManager(new LinearLayoutManager(this));
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
        doSearch(query);

    }

    private void doSearch(String query) {
        //call the url
        String url = String.format("https://api.themoviedb.org/3/search/movie?query=%s"
                + "&api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", query);
        asyncCall(url);

    }

    public void asyncCall(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseData);
                            JSONArray movieJsonSearchResults = responseJSON.getJSONArray("results");
                            if (movies != null) {
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
    }

}
