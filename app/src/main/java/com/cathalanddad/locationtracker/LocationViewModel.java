package com.cathalanddad.locationtracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationViewModel extends ViewModel {

    private MutableLiveData<Double> latitude = new MutableLiveData<>();
    private MutableLiveData<Double> longitude = new MutableLiveData<>();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRepository locationRepository;

    public LocationViewModel() {
        Log.d("LocationViewModel", "ViewModel created");
        locationRepository = new LocationRepository();
    }

    public LiveData<Double> getLatitude() {
        return latitude;
    }

    public LiveData<Double> getLongitude() {
        return longitude;
    }

    public void startLocationUpdates(Context context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    updateLocation(locationResult.getLastLocation());
                }
            }
        };
        createLocationRequest();
    }

    @SuppressLint("MissingPermission")
    private void createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(10000)
                .build();

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void updateLocation(Location location) {
        latitude.setValue(location.getLatitude());
        longitude.setValue(location.getLongitude());
        locationRepository.sendLocation(location.getLatitude(), location.getLongitude());
    }

    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d("LocationViewModel", "ViewModel cleared");
        stopLocationUpdates();
    }
}