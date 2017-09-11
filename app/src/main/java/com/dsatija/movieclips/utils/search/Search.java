package com.dsatija.movieclips.utils.search;

import static android.view.Gravity.CENTER;
import static android.view.Gravity.LEFT;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dsatija.movieclips.R;
import com.dsatija.movieclips.activities.SearchResultsActivity;

import com.dsatija.movieclips.models.Movie;
import com.dsatija.movieclips.network.NetworkCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dishasatija on 8/29/17.
 */

public class Search {

    Context ctx;
    Activity mActivity;
    View v;
    SearchView sv;
    private ArrayList<Movie> movies = new ArrayList<>(5);
    final ArrayList<String> suggestions = new ArrayList<>();

    public Search(Activity mActivity) {
        this.ctx = mActivity.getApplicationContext();
        this.mActivity = mActivity;
        this.v = mActivity.findViewById(android.R.id.content);
    }


    public void setSearchView() {
        ((SearchView) mActivity.findViewById(R.id.searchView)).setMaxWidth(Integer.MAX_VALUE);
        ((SearchView) mActivity.findViewById(R.id.searchView)).setQueryRefinementEnabled(true);
        SearchManager searchManager = (SearchManager) ctx.getSystemService(Context.SEARCH_SERVICE);
        ((SearchView) mActivity.findViewById(R.id.searchView)).setSearchableInfo(
                searchManager.getSearchableInfo(mActivity.getComponentName()));
    }

    public void customizeSearchView() {
        SearchView sv = ((SearchView) mActivity.findViewById(
                R.id.searchView));
        int searchPlateId = sv.getContext().getResources().getIdentifier(
                "android:id/search_plate", null, null);
        View searchPlate = sv.findViewById(searchPlateId);
        //change color
        if (searchPlate != null) {

            searchPlate.setBackgroundColor(Color.WHITE);
            int closeButtonId = sv.getContext().getResources().getIdentifier(
                    "android:id/search_close_btn", null, null);
            ImageView closeButtonImage = (ImageView) (sv).findViewById(closeButtonId);
            closeButtonImage.setColorFilter(
                    ContextCompat.getColor(ctx, R.color.colorPrimary),
                    PorterDuff.Mode.SRC_IN);
            int searchTextId = searchPlate.getContext().getResources().getIdentifier(
                    "android:id/search_src_text", null, null);
            TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            if (searchText != null) {
                searchText.setTextColor(Color.BLACK);
                searchText.setHintTextColor(Color.GRAY);

            }
        }
    }

    public void doSearch(String query) {
        Intent i = new Intent(ctx, SearchResultsActivity.class);
        i.putExtra("key", query);
        mActivity.startActivity(i);
        mActivity.finish();
    }

    public void setListeners() {


        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(ctx,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setBackgroundColor(Color.LTGRAY);
                text.setEllipsize(TextUtils.TruncateAt.END);
                text.setPadding(30,5,30,5);
                return view;
            }

        };
        sv = (SearchView) v.findViewById(R.id.searchView);
        sv.setSuggestionsAdapter(suggestionAdapter);

        sv.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                sv.setQuery(suggestions.get(position), true);
                sv.clearFocus();
                return true;
            }
        });
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; //false if you want implicit call to searchable activity
                // or true if you want to handle submit yourself
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Hit the network and take all the suggestions and store them in List 'suggestions'
                if (newText.length() > 2) {
                    getSuggestions(newText);
                    String[] columns = {BaseColumns._ID,
                            SearchManager.SUGGEST_COLUMN_TEXT_1,
                            SearchManager.SUGGEST_COLUMN_INTENT_DATA,
                    };
                    MatrixCursor cursor = new MatrixCursor(columns);
                    for (int i = 0; i < suggestions.size(); i++) {
                        String[] tmp = {Integer.toString(i), suggestions.get(i)
                               , suggestions.get(i)
                        };
                        cursor.addRow(tmp);
                    }
                    suggestionAdapter.swapCursor(cursor);

                }
                return true;
            }
        });


    }


    private void getSuggestions(String newText) {

        String url = String.format("https://api.themoviedb.org/3/search/movie?query=%s"
                + "&api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", newText);
        movies = NetworkCall.asyncCall(url, mActivity,true);
        suggestions.clear();
        for (int i = 0; i < 6; i++) {
            if (movies != null && movies.size() > 0 && i < movies.size()) {
                suggestions.add(movies.get(i).getOriginalTitle());
            }
        }
    }
}









