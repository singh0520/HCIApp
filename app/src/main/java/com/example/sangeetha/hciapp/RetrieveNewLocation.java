package com.example.sangeetha.hciapp;

/**
 * Created by sangeetha on 4/9/16.
 */

import android.app.Application;
import android.util.Log;

public class RetrieveNewLocation extends Application {

    private double userLatitude = 0.0;
    private double userLongtitude = 0.0;

    public double getUserLatitude() {
        return userLatitude;
    }

    public void setUserLatitude(double lt) {
        userLatitude = lt;
        Log.d("updated latitude with", Double.toString(userLatitude));
    }

    public double getUserLongtitude() {
        return userLongtitude;
    }

    public void setUserLongtitude(double ln) {
        userLongtitude = ln;
        Log.d("updated longtitude with", Double.toString(userLongtitude));
    }
}
