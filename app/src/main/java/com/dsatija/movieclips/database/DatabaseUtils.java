package com.dsatija.movieclips.database;

import static com.dsatija.movieclips.database.SqlOpenHelper.BACKDROP_PATH;
import static com.dsatija.movieclips.database.SqlOpenHelper.FAV;
import static com.dsatija.movieclips.database.SqlOpenHelper.MOVIE_ID;
import static com.dsatija.movieclips.database.SqlOpenHelper.OVERVIEW;
import static com.dsatija.movieclips.database.SqlOpenHelper.POPULARITY;
import static com.dsatija.movieclips.database.SqlOpenHelper.POSTER_PATH;
import static com.dsatija.movieclips.database.SqlOpenHelper.RELEASE_DATE;
import static com.dsatija.movieclips.database.SqlOpenHelper.TABLE_NAME;
import static com.dsatija.movieclips.database.SqlOpenHelper.TITLE;
import static com.dsatija.movieclips.database.SqlOpenHelper.VOTE_AVERAGE;
import static com.dsatija.movieclips.database.SqlOpenHelper.VOTE_COUNT;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dsatija.movieclips.models.Movie;

import java.sql.Struct;
import java.util.ArrayList;

/**
 * Created by dishasatija on 9/6/17.
 */

public class DatabaseUtils {
    Context ctx;

    public DatabaseUtils(Context ctx) {
        this.ctx = ctx;
    }


    public SQLiteDatabase getHandle(Context ctx) {
        SqlOpenHelper helper = SqlOpenHelper.getInstance(ctx);
        SQLiteDatabase handle = helper.getWritableDatabase();
        return handle;
    }




    public long insertData(long movie_id, int fav, String posterPath, String backDropPath,
            String title, String overview, Double voteAvg, int voteCount, String releaseDate,
            double popularity) {
        ContentValues values = new ContentValues(4);
        values.put(MOVIE_ID, movie_id);
        values.put(FAV, fav);
        values.put(POSTER_PATH,posterPath);
        values.put(BACKDROP_PATH,backDropPath);
        values.put(TITLE,title);
        values.put(OVERVIEW,overview);
        values.put(VOTE_AVERAGE,voteAvg);
        values.put(VOTE_COUNT,voteCount);
        values.put(RELEASE_DATE,releaseDate);
        values.put(POPULARITY,popularity);
        long l = getHandle(ctx).insert(TABLE_NAME, null, values);
        getHandle(ctx).close();
        return l;
    }

    public boolean readData(Movie movie, long movieId) {
        Cursor cursor =
                getHandle(ctx).query(TABLE_NAME, new String[]{MOVIE_ID, FAV}, "MOVIE_ID=?",
                        new String[]{
                                String.valueOf(movieId)}, null, null, null);

        try {
            if (cursor.moveToNext()) {
                Integer fav = cursor.getInt(1);
                return fav == 0 ? false : true;

            } else {

                insertData(movieId, 0, movie.getPosterPath().substring(33), movie.getBackdropPath().substring(33),
                        movie.getOriginalTitle(),
                        movie.getOverview(), movie.getVoteAverage(), movie.getVoteCount(),
                        movie.getReleaseDate(),
                        movie.getPopularity());
                return false;
            }
        } finally {
            cursor.close();
            getHandle(ctx).close();
        }

    }

    public void updateData(long movie_id, Integer fav) {
        ContentValues values = new ContentValues(4);
        values.put(MOVIE_ID, movie_id);
        values.put(FAV, fav);
        getHandle(ctx).update(TABLE_NAME, values, "MOVIE_ID=" + movie_id, null);
        getHandle(ctx).close();
    }


    public ArrayList<Movie> getFavMovies() {
        Cursor cursor =
                getHandle(ctx).query(TABLE_NAME,
                        new String[]{},
                        "favorite = ?",
                        new String[]{String.valueOf(1)}, null, null, null);

        ArrayList<Movie> favMovies = new ArrayList<>(20);

        while (cursor.moveToNext()) {
            if (cursor != null && cursor.getCount() > 0) {
                long primKey = cursor.getLong(0);
                Long id = cursor.getLong(1);
                int fav = cursor.getInt(2);
                String posterPath = cursor.getString(3);
                String backdropPath = cursor.getString(4);
                String originalTitle = cursor.getString(5);
                String overview = cursor.getString(6);
                double voteAverage = cursor.getDouble(7);
                int voteCount = cursor.getInt(8);
                String releaseDate = cursor.getString(9);
                double popularity = cursor.getDouble(10);
                Movie favMovie = new Movie(id, posterPath, backdropPath, originalTitle, overview,
                        voteAverage, voteCount,
                        releaseDate, popularity);
                favMovies.add(favMovie);
            }
            getHandle(ctx).close();

        }
        return favMovies;
    }
}