package com.dsatija.movieclips.activities;

import static com.dsatija.movieclips.R.id.ibFav;
import static com.dsatija.movieclips.R.id.searchView;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.BackgroundToForegroundTransformer;
import com.ToxicBakery.viewpager.transforms.CubeInTransformer;
import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;
import com.ToxicBakery.viewpager.transforms.FlipHorizontalTransformer;
import com.ToxicBakery.viewpager.transforms.FlipVerticalTransformer;
import com.ToxicBakery.viewpager.transforms.ForegroundToBackgroundTransformer;
import com.ToxicBakery.viewpager.transforms.RotateDownTransformer;
import com.ToxicBakery.viewpager.transforms.RotateUpTransformer;
import com.ToxicBakery.viewpager.transforms.ScaleInOutTransformer;
import com.ToxicBakery.viewpager.transforms.StackTransformer;
import com.ToxicBakery.viewpager.transforms.ZoomInTransformer;
import com.dsatija.movieclips.R;
import com.dsatija.movieclips.adapters.MovieFragmentPagerAdapter;
import com.dsatija.movieclips.network.Connectivity;
import com.dsatija.movieclips.utils.search.Search;
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
        Search search = new Search(this);
        search.setSearchView();
        setViewPager();
        if (!Connectivity.isConnected(this)) {
            TastyToast.makeText(this, "Please check your internet connection",
                    TastyToast.LENGTH_LONG, TastyToast.ERROR);
        } else {
            //customizing search view
            search.customizeSearchView();
            search.setListeners();
            //search

            if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
                String query = getIntent().getStringExtra(SearchManager.QUERY);
                TastyToast.makeText(getApplicationContext(), " Searching :" + query,
                        TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                search.doSearch(query);
            }
        }
    }


    private void setViewPager() {


        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new MovieFragmentPagerAdapter(getSupportFragmentManager(),
                MovieClipsMainActivity.this));

        viewPager.setPageTransformer(true, new RotateUpTransformer());
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

    }



}
