package com.cathalanddad.locationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.cathalanddad.locationtracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private LocationViewModel locationViewModel;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private ActivityMainBinding binding;
    private Intent locationServiceIntent;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        latitudeTextView = binding.latitudeTextView;
        longitudeTextView = binding.longitudeTextView;

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        locationServiceIntent = new Intent(this, LocationService.class);

        checkLocationPermission();

        locationViewModel.getLatitude().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double latitude) {
                latitudeTextView.setText("Latitude: " + latitude);
            }
        });

        locationViewModel.getLongitude().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double longitude) {
                longitudeTextView.setText("Longitude: " + longitude);
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, start location updates
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates();
            }
        } else {
            Log.w(TAG, "Could not request permission");
        }
    }

    private void startLocationUpdates() {
        //start service
        startForegroundService(locationServiceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //stop service
        stopService(locationServiceIntent);
    }
}