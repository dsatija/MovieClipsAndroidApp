package com.dsatija.movieclips.activities;

import static com.dsatija.movieclips.R.id.searchView;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import com.dsatija.movieclips.R;
import com.dsatija.movieclips.adapters.MovieFragmentPagerAdapter;
import com.sdsmdg.tastytoast.TastyToast;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieClipsMainActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;
    @BindView(searchView)
    SearchView mSearchView;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    public static int scrolledPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_clips_main);
        ButterKnife.bind(this);
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setQueryRefinementEnabled(true);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        viewPager.setAdapter(new MovieFragmentPagerAdapter(getSupportFragmentManager(),
                MovieClipsMainActivity.this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {
                scrolledPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);

        //customizing search view
        customizeSearchView();
        //search
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            search();
        }
    }

    private void search() {
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        TastyToast.makeText(getApplicationContext(), " Searching :" + query,
                TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
        Intent i = new Intent(this, SearchResultsActivity.class);
        i.putExtra("key", query);
        startActivity(i);
        finish();
    }

    private void customizeSearchView() {
        int searchPlateId = mSearchView.getContext().getResources().getIdentifier(
                "android:id/search_plate", null, null);
        View searchPlate = mSearchView.findViewById(searchPlateId);
        //change color
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(Color.WHITE);
            int closeButtonId = mSearchView.getContext().getResources().getIdentifier(
                    "android:id/search_close_btn", null, null);
            ImageView closeButtonImage = (ImageView) mSearchView.findViewById(closeButtonId);
            closeButtonImage.setColorFilter(
                    ContextCompat.getColor(MovieClipsMainActivity.this, R.color.colorPrimary),
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
}
