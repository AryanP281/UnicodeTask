package com.example.unicodetask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class RetriveLocation implements LocationListener
{
    private final long MIN_TIME = 1; //The min time in milliseconds between location updates
    private final long MIN_DISTANCE = 500; //The min distance in metres between location updates

    private Fragment fragment; //The WeatherFragment with which the object is associated
    private LocationManager locationManager; //The location manager
    private String currentProvider = null; //The current location provider
    private Location userLocation = null; //The user's location
    boolean isListening; //Whether the object is listening for location changes

    RetriveLocation(Fragment fragment)
    {
        this.fragment = fragment;
        initializesLocationManager();
    }

    void initializesLocationManager()
    {
        /*Initializes the location manager*/

        try
        {
            //Getting the location manager
            locationManager = (LocationManager)fragment.getActivity().getSystemService(Context.LOCATION_SERVICE);

            //Checking if network provider is enabled
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                //Requesting location updates
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                currentProvider = LocationManager.NETWORK_PROVIDER;
                isListening = true;
            }
            //Checking if gps is enabled
            else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                //Requesting location updates
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                currentProvider = LocationManager.GPS_PROVIDER;
                isListening = true;
            }
            else
            {
                isListening = false;
                //Asking the user to enable location provider
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(fragment.getActivity()); //The dialog builder
                dialogBuilder.setTitle(R.string.switch_on_location_dialog_title)
                        .setMessage(R.string.switch_on_location_dialog_msg)
                        .setPositiveButton(R.string.permission_rationale_accept_btn, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS); //The intent for switching on the location sources
                                fragment.getActivity().startActivity(intent); //Switching on the location services
                            }
                        });
                dialogBuilder.show();
            }

            userLocation = locationManager.getLastKnownLocation(currentProvider);
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location newLocation)
    {
        this.userLocation = newLocation;

        ((WeatherFragment)fragment).receiveUserLocation(newLocation); //Sending the location to the weather fragment
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        if(currentProvider == null) initializesLocationManager();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        if(provider.equals(currentProvider)) initializesLocationManager();
    }

    Location getUserLocation()
    {
        /*Returns the users location*/

        return userLocation;
    }

    void stopListener()
    {
        /*Stops listening for location updates*/

        locationManager.removeUpdates(RetriveLocation.this);
        isListening = false;
    }
}
