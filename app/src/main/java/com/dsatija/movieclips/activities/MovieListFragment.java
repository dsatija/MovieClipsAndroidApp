package com.dsatija.movieclips.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.adapters.ItemClickSupport;
import com.dsatija.movieclips.adapters.MovieAdapter;
import com.dsatija.movieclips.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieListFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private SwipeRefreshLayout swipeContainer;

    ArrayList<Movie> movies;
    MovieAdapter movieAdapter;
    @BindView(R.id.lvMovies)
    RecyclerView lvItems;

    private Unbinder unbinder;

    public static MovieListFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MovieListFragment fragment = new MovieListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeContainer = (SwipeRefreshLayout) view;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String url;
                switch (MovieClipsMainActivity.scrolledPage) {
                    case 0:
                        url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + getString(
                                R.string.movie_db_key);
                        break;
                    case 1:
                        url = "https://api.themoviedb.org/3/movie/popular?api_key=" + getString(
                                R.string.movie_db_key);
                        break;
                    case 2:
                        url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + getString(
                                R.string.movie_db_key);
                        break;
                    default:
                        url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + getString(
                                R.string.movie_db_key);
                }
                asyncCall(url);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        movies = new ArrayList<>();

        if (mPage == 1) {
            asyncCall("https://api.themoviedb.org/3/movie/now_playing?api_key=" + getString(
                    R.string.movie_db_key));
        } else if (mPage == 2) {
            asyncCall("https://api.themoviedb.org/3/movie/popular?api_key=" + getString(
                    R.string.movie_db_key));
        } else {
            asyncCall("https://api.themoviedb.org/3/movie/upcoming?api_key=" + getString(
                    R.string.movie_db_key));
        }

        movieAdapter = new MovieAdapter(MovieListFragment.this.getContext(), movies);
        lvItems.setAdapter(movieAdapter);
        lvItems.setLayoutManager(new LinearLayoutManager(this.getContext()));

        ItemClickSupport.addTo(lvItems).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Movie movie = movies.get(position);
                        Intent intent = new Intent(getActivity(), MovieDetails.class);
                        intent.putExtra("movieObject", movie);
                        startActivity(intent);
                        //  getActivity().finish();
                    }
                }
        );
        return view;
    }

    private OkHttpClient client = new OkHttpClient();

    public void asyncCall(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseData = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseData);
                            JSONArray movieJsonResults = responseJSON.getJSONArray("results");
                            movies.clear();
                            movies.addAll(Movie.fromJSONArray(movieJsonResults));
                            movieAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        swipeContainer.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}