package com.example.unicodetask;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherFragment extends Fragment
{
    private final int INTERNET_PERMISSION_REQUEST_CODE = 100;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 200;

    private boolean internetPermissionGranted = false; //Whether the app has been granted permission to use the internet
    private boolean locationPermissionGranted = false; //Whether the app has been granted permission to use location services

    private RetriveLocation locationRetreiver; //Retrieves the user's location

    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            //Checking if the app has permission to use the internet
            if(internetPermissionGranted)
            {
                //Getting the city name
                String city = ((TextView) getView().findViewById(R.id.city_name)).getText().toString();

                String requestUrl = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", city,
                        "74bbe85a0553d698bff821e7f1f5c4bd"); //The api request url

                //Getting the weather for the city
                (new RetriveWeatherData(WeatherFragment.this)).execute(requestUrl);
            }
            else
            {
                //Requesting user permission
                getInternetPermission();
            }
        }
    }; //Listens for clicks on the search button

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Requsting user permission to use the internet
        getInternetPermission();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_weather, container, false);

        //Enabling the toolbar
        setHasOptionsMenu(true);

        //Setting the click listener for the search button
        ((Button)fragmentView.findViewById(R.id.search_city_weather_btn)).setOnClickListener(searchClickListener);

        return fragmentView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[])
    {
        //Checking if the app was granted permission
        switch(requestCode)
        {
            case INTERNET_PERMISSION_REQUEST_CODE : internetPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED; break;
            case LOCATION_PERMISSION_REQUEST_CODE : getUserLocation(); break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        /*Inflates the toolbar menu*/

        inflater.inflate(R.menu.weather_menu, menu); //Inflating the fragment menu
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        /*Handles menu item clicks*/

        //Determining which item has been clicked
        switch(item.getItemId())
        {
            case R.id.get_location : getUserLocation(); break;
        }

        return true;
    }

    private void getInternetPermission()
    {
        /*Asks the user for permission to access the internet*/

        //Checking if the app already has permission
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
        {
            internetPermissionGranted = false; //The app doesnt have permission

            //Checking if the user should be shown permission rationale explaining the need for internet access
            if(shouldShowRequestPermissionRationale(Manifest.permission.INTERNET))
            {
                AlertDialog.Builder rationaleDialogBuilder = new AlertDialog.Builder(getActivity());
                rationaleDialogBuilder.setTitle(R.string.permission_rationale_title)
                                    .setMessage(R.string.internet_permission_rationale_msg)
                                    .setPositiveButton(R.string.permission_rationale_accept_btn, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            //Requesting permission
                                            requestPermissions(new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE);
                                        }
                                    });
                rationaleDialogBuilder.show();
            }
            else requestPermissions(new String[]{Manifest.permission.INTERNET}, INTERNET_PERMISSION_REQUEST_CODE); //Directly requesting permission
        }
        else
            internetPermissionGranted = true; //The app already has permission
    }

    void displayWeatherData(JSONObject weatherJson)
    {
        /*Displays the weather data provided in json format*/

        if(weatherJson != null)
        {
            //Creating the weather data display fragment
            WeatherDataFragment fragment = new WeatherDataFragment();
            fragment.setWeatherData(weatherJson);

            //Switching the fragment
            ((HomeActivity)getActivity()).displayFragment(fragment, true);
        }
    }

    private void getUserLocation()
    {
        /*Gets the users location*/

        //Getting location access permission
        getLocationServicesPermission();

        //Getting the user location coordinates
        if(locationPermissionGranted)
        {
            if(locationRetreiver == null || !locationRetreiver.isListening)
                locationRetreiver = new RetriveLocation(this);
            else if(locationRetreiver.isListening) locationRetreiver.initializesLocationManager(); //Starts listening for new location

        }
    }

    private void getLocationServicesPermission()
    {
        /*Asks the user for permission to use location services*/

        //Checking if the app already has permission
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            locationPermissionGranted = false;
            //Checking if the user should be show permission rationale explaining the need to access location service
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
            {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()); //The dialog box builder
                dialogBuilder.setTitle(R.string.permission_rationale_title)
                            .setMessage(R.string.location_permission_rationale_msg)
                            .setPositiveButton(R.string.permission_rationale_accept_btn, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    //Requesting permission
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                }
                            }); //Building the dialog box
                dialogBuilder.show();
            }
            else
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE); //Requesting permission
        }
        else
            locationPermissionGranted = true;
    }

    void receiveUserLocation(Location userLocation)
    {
        /*Retrieves the user location from the location retriever*/

        locationRetreiver.stopListener(); //Stops listening for location updates as location has been received

        String requestUrl = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s",
                userLocation.getLatitude(), userLocation.getLongitude(), "74bbe85a0553d698bff821e7f1f5c4bd"); //The api request

        //Getting the weather for the city
        (new RetriveWeatherData(WeatherFragment.this)).execute(requestUrl);
    }
}