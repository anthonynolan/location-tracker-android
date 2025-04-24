package com.cathalanddad.locationtracker;

import android.util.Log;
import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationServiceHelper {

    private static final String TAG = "LocationServiceHelper";
    // Use "10.0.2.2" if testing on an emulator and your server is on your host machine.
    private static final String URL = "https://api.cathalanddad.com:8000/action/loc/"; // Replace with your server address if needed

    private OkHttpClient client = new OkHttpClient();

    public void sendLocation(final double lat, final double lon) {
        // Create JSON payload
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", String.valueOf(lat));
            jsonObject.put("lon", String.valueOf(lon));
            // Capture the current timestamp (as a string) if that fits your endpoint signature
//            jsonObject.put("timestamp", String.valueOf(System.currentTimeMillis()));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();

        // Asynchronously send the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Failed to send location", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected response: " + response);
                } else {
                    Log.d(TAG, "Location sent successfully!");
                }
                response.close();
            }
        });
    }
}
