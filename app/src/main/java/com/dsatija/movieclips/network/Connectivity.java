package com.dsatija.movieclips.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Context;

import java.io.IOException;


/**
 * Created by dishasatija on 8/25/17.
 */

public class Connectivity {

    public static boolean isConnected(Context ctx) {
        return (isNetworkConnected(ctx) && isInternetConnect());
    }

    private static  boolean isInternetConnect() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static  boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


}
