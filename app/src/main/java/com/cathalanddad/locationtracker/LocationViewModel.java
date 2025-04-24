package com.cathalanddad.locationtracker;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class LocationViewModel extends ViewModel {

    private MutableLiveData<Double> latitude = new MutableLiveData<>();
    private MutableLiveData<Double> longitude = new MutableLiveData<>();

    public LocationViewModel() {
        Log.d("LocationViewModel", "ViewModel created");
    }

    public LiveData<Double> getLatitude() {
        return latitude;
    }

    public LiveData<Double> getLongitude() {
        return longitude;
    }

    public void updateLocation(double lat, double lon){
        latitude.setValue(lat);
        longitude.setValue(lon);
    }
}