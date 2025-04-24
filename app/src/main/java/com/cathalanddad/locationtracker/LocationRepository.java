package com.cathalanddad.locationtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class LocationRepository {
    private static final String TAG = "LocationRepository";

    public void sendLocation(double lat, double lon) {
        Log.d(TAG, "sending location");
        Data inputData = new Data.Builder()
                .putDouble("lat", lat)
                .putDouble("lon", lon)
                .build();
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(PostWorker.class)
                .setInputData(inputData)
                .setInitialDelay(0, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueue(workRequest);
    }
}