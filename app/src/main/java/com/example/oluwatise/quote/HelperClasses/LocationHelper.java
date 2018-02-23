package com.example.oluwatise.quote.HelperClasses;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.oluwatise.quote.Activities.MainActivity;
import com.example.oluwatise.quote.Fragments.SettingsFragment;
import com.example.oluwatise.quote.Fragments.WriteFragment;
import com.example.oluwatise.quote.R;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.Locale;

import static android.support.v4.app.ActivityCompat.requestPermissions;

/**
 * Created by Oluwatise on 11/27/2017.
 */

public class LocationHelper implements LocationListener {
    public final int PERMISSION_ACCESS_LOCATION = 100;

    private Context context;
    private double latitude;
    private double longitude;
    private String cityName;
    private LocationManager locationManager;
    android.app.Fragment openFragment;
    private SharedPreferences cityNameSharedPreference;
    public LocationHelper(Context context) {
        this.context = context;
        openFragment = MainActivity.getMainActivity().getFragmentManager().findFragmentById(R.id.mainFragmentContainer);

        locationManager = (LocationManager) MainActivity.getMainActivity().getSystemService(Context.LOCATION_SERVICE);
        checkAndRequestPermission();

    }
    public Context getContext() {
        return context;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.v("POWER", "lat is "+ String.valueOf(latitude)+ " long is "+ String.valueOf(longitude));
        // use the long and lat to get the city name
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location
                    .getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            this.cityName=addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("POWERA", "City is: "+ this.cityName);
        storeCityNameInSharedPreference();
        // writeFragment is not the only fragment that calls this method,
        // it's needed in settingsFragment too
        if (openFragment instanceof WriteFragment) {
            //same instance of writeFragment that is open has to be used
            WriteFragment writeFragment = MainActivity.getMainActivity().getWriteFragment();
            writeFragment.storeInFireStore();
        }else if (openFragment instanceof SettingsFragment) {
            SettingsFragment settingsFragment = MainActivity.getMainActivity().getSettingsFragment();
            String cityName = MainActivity.getMainActivity().getSharedPreferences("cityName", Context.MODE_PRIVATE)
                    .getString("city", null);
            settingsFragment.setLocationText(cityName);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.v("POWER", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v("POWER", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.v("POWER", "onProviderDisabled");
    }

    private void checkAndRequestPermission() {
        // Note: RequestPermissionResult is handled in activity
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(MainActivity.getMainActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_ACCESS_LOCATION);
        }
        else {
            this.requestLocationUpdates();
        }
    }

    public void requestLocationUpdates(){
        cityNameSharedPreference = getContext().getSharedPreferences("cityName", Context.MODE_PRIVATE);
        // if cityName already exists in sharedPreference,
        // we don't even have to check for his location
        if (cityNameSharedPreference.getString("city",  null) == null || openFragment instanceof SettingsFragment) {
            try {
                // this is what actually calls 'onLocationChanged'
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 200, 10, this);
                Log.v("POWER", "requesting location updates");
            }
            catch (SecurityException e) {
                e.printStackTrace();
                Log.v("POWER", "Denied by user");

            }
        }
        else {
            MainActivity.getMainActivity().getWriteFragment().storeInFireStore();
        }
    }

    private void storeCityNameInSharedPreference(){
        SharedPreferences.Editor cityNameEditor = cityNameSharedPreference.edit();
        cityNameEditor.putString("city", this.cityName);
        cityNameEditor.apply();
        Log.v("POWER", "city name saved");
        // remove updates to save battery
        locationManager.removeUpdates(this);
    }

}


