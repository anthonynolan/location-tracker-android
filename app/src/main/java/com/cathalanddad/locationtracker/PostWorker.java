package com.cathalanddad.locationtracker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.common.api.Result;

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

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"lat\": " + lat + ", \"lon\": " + lon + "}");
        Request request = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.e(TAG, "Error sending data: " + e.getMessage());
            return Result.failure();
        }

        return Result.success();
    }
}