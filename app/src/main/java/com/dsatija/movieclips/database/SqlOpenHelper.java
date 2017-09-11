package com.dsatija.movieclips.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dishasatija on 9/6/17.
 */

public class SqlOpenHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "moviesdb.sqlite";
    public static final int VERSION = 3;
    public static final String TABLE_NAME = "movies";
    public static final String ID = "id";
    public static final String MOVIE_ID = "movie_id";
    public static final String FAV = "favorite";
    public static final String POSTER_PATH = "posterPath";
    public static final String BACKDROP_PATH = "backdropPath" ;
    public static final String TITLE = "originalTitle";
    public static final String OVERVIEW = "overview";
    public static final String VOTE_AVERAGE = "voteAverage";
    public static final String VOTE_COUNT = "voteCount";
    public static final String RELEASE_DATE = "releaseDate";
    public static final String POPULARITY = "popularity";

    private static SqlOpenHelper mInstance = null;

    public SqlOpenHelper(Context ctx) {
        super(ctx,DBNAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDatabase(db);

    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("create table "+ TABLE_NAME + "(" + ID + " integer primary key autoincrement"
                + " not null," +MOVIE_ID + " long not null unique, "+ FAV +
                " integer DEFAULT 0," + POSTER_PATH + " text not null, "+ BACKDROP_PATH + " "
                + "text, "+ TITLE+ " text not null, "+ OVERVIEW + " text , " + VOTE_AVERAGE + " real, "
        + VOTE_COUNT +" integer, " + RELEASE_DATE + " text," + POPULARITY + " real );");
    }

    public static SqlOpenHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new SqlOpenHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
