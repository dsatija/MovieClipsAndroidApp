package com.dsatija.movieclips.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.adapters.ItemClickSupport;
import com.dsatija.movieclips.adapters.MovieAdapter;
import com.dsatija.movieclips.adapters.MovieFragmentPagerAdapter;
import com.dsatija.movieclips.database.DatabaseUtils;
import com.dsatija.movieclips.models.Movie;
import com.dsatija.movieclips.network.Connectivity;
import com.dsatija.movieclips.utils.EndlessRecyclerViewScrollListener;
import com.sdsmdg.tastytoast.TastyToast;

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
    private EndlessRecyclerViewScrollListener scrollListener;
    ArrayList<Movie> movies;
    MovieAdapter movieAdapter;
    @BindView(R.id.lvMovies)
    RecyclerView lvItems;

    ArrayList<Movie> favMovies = new ArrayList<>();
    private Unbinder unbinder;
    private OkHttpClient client = new OkHttpClient();
    DatabaseUtils du ;
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
        //du = new DatabaseUtils(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!Connectivity.isConnected(this.getContext())) {
            TastyToast.makeText(this.getContext(), "Please check your internet connection",
                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }

        swipeContainer = (SwipeRefreshLayout) view;

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String url;
                swipeContainer.setRefreshing(true);
                switch (MovieClipsMainActivity.scrolledPage) {

                    case 0:
                        //swipeContainer.setRefreshing(true);
                        url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + getString(
                                R.string.movie_db_key);
                        break;
                    case 1:
                        //swipeContainer.setRefreshing(true);
                        url = "https://api.themoviedb.org/3/movie/popular?api_key=" + getString(
                                R.string.movie_db_key);
                        break;
                    case 2:
                       // swipeContainer.setRefreshing(true);
                        url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + getString(
                                R.string.movie_db_key);
                        break;
                    default:
                        url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + getString(R.string.movie_db_key);
                }
                asyncCall(url, true);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);

        movies = new ArrayList<>();


        switch(mPage) {

            case 1:

                asyncCall("https://api.themoviedb.org/3/movie/now_playing?api_key=" + getString(
                        R.string.movie_db_key), true);
                break;
            case 2:

            asyncCall("https://api.themoviedb.org/3/movie/popular?api_key=" + getString(
                    R.string.movie_db_key), true);
                break;


            default:

                asyncCall("https://api.themoviedb.org/3/movie/upcoming?api_key=" + getString(
                        R.string.movie_db_key), true);
        }

        movieAdapter = new MovieAdapter(MovieListFragment.this.getContext(), movies);
        lvItems.setAdapter(movieAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this.getContext());
        lvItems.setLayoutManager(layoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                //mProgressbar.setVisibility(view.VISIBLE);
                movieAdapter.showLoading(true);
               // movieAdapter.notifyDataSetChanged();
                if(mPage < 4) {
                    loadNextDataFromApi(page + 1);
                }
                movieAdapter.showLoading(false);
               // movieAdapter.notifyDataSetChanged();

                //mProgressbar.setVisibility(view.GONE);
            }
        };
        lvItems.addOnScrollListener(scrollListener);

        ItemClickSupport.addTo(lvItems).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(getActivity(), MovieDetails.class);
                            Movie movie = movies.get(position);
                            intent.putExtra("movieObject", movie);
                        /*else {
                            Movie favMovie = favMovies.get(position);
                            intent.putExtra("movieObject", favMovie);
                        }*/
                        startActivity(intent);
                        //  getActivity().finish();
                    }
                }
        );

        return view;
    }

    private void loadNextDataFromApi(int page) {

        String url;

        switch (mPage) {

            case 1:
                url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + getString(
                        R.string.movie_db_key)+"&page="+page;
                break;
            case 2:
                url = "https://api.themoviedb.org/3/movie/popular?api_key=" + getString(
                        R.string.movie_db_key)+"&page="+page;
                break;
            case 3:
                url = "https://api.themoviedb.org/3/movie/upcoming?api_key=" + getString(
                        R.string.movie_db_key)+"&page="+page;
                break;
            default:
                url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + getString(
                        R.string.movie_db_key)+"&page="+page;
        }

            asyncCall(url, false);

    }

    public void asyncCall(String url, final boolean isClear) {
        System.out.println("Printing url:" + url);
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
                            if (isClear){
                                movies.clear();
                            }
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