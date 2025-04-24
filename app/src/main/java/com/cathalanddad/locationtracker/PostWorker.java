package com.cathalanddad.locationtracker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostWorker extends Worker {
    private static final String TAG = "PostWorker";
    public PostWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "post location data");
        double lat = getInputData().getDouble("lat", 0);
        double lon = getInputData().getDouble("lon", 0);
        long timestamp = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        // Create the JSON body using a JSONObject
        JSONObject jsonBody = new JSONObject();
        try {
            // Convert latitude and longitude to strings
            jsonBody.put("lat", String.valueOf(lat));
            jsonBody.put("lon", String.valueOf(lon));
            jsonBody.put("timestamp", timestamp);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON body: " + e.getMessage());
            return Result.failure();
        }

        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
        Request request = new Request.Builder()
                .url("https://api.cathalanddad.com:8000/action/loc/") // Correct URL
                .post(body)
                .addHeader("Content-Type", "application/json") // Correct header
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e(TAG, "Error response code: " + response.code());
                return Result.failure();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error sending data: " + e.getMessage());
            return Result.failure();
        }

        return Result.success();
    }
}