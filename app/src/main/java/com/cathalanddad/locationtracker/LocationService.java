package com.cathalanddad.locationtracker;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import android.util.Log;

public class LocationService extends Service {

    private static final String CHANNEL_ID = "location_channel";
    private static final int NOTIFICATION_ID = 101;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final String TAG = "main-activity";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Prepare the location callback to receive updates
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.d(TAG, "onLocation result called");
                if (locationResult == null) {
                    return;
                }
                // For each update, log the location. Replace this with your data saving logic.
                LocationServiceHelper helper = new LocationServiceHelper();
                for (Location location : locationResult.getLocations()) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    // For debugging or display purposes
                    Log.d(TAG, "Location update: lat=" + lat + ", lon=" + lon);

                    // TODO: Send or store this data as needed
                    Intent intent = new Intent("com.cathalanddad.locationtracker.LOCATION_UPDATE");
                    intent.putExtra("lat", lat);
                    intent.putExtra("lon", lon);
                    sendBroadcast(intent);

                    helper.sendLocation(lat, lon);
                }
            }
        };

        // Create notification channel and start the service in the foreground
        createNotificationChannel();
        Notification notification = buildNotification();
        startForeground(NOTIFICATION_ID, notification);
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Create a location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);         // 5-second interval for updates
        locationRequest.setFastestInterval(3000);    // Fastest update interval of 3 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Request location updates (make sure you've already granted runtime permissions)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Remove location updates to prevent memory leaks
        fusedLocationClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // No binding provided
        return null;
    }

    // Build a simple notification for the foreground service
    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Tracking Active")
                .setContentText("Your location is being collected in the background.")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build();
    }

    // Create a notification channel for API 26+
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Location Service Channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Channel used by Location Service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
