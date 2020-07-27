package com.example.unicodetask;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherFragment extends Fragment
{
    private final int INTERNET_PERMISSION_REQUEST_CODE = 100;

    private boolean internetPermissionGranted = false; //Whether the app has been granted permission to use the internet
    private RetriveWeatherData weatherDataRetriever = null; //Retrieves the weather data

    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            //Checking if the app has permission to use the internet
            if(internetPermissionGranted)
            {
                //Getting the city name
                String city = ((TextView) getView().findViewById(R.id.city_name)).getText().toString();

                //Getting the weather for the city
                if (weatherDataRetriever == null)
                    weatherDataRetriever = (RetriveWeatherData) (new RetriveWeatherData(WeatherFragment.this).execute(city));
                else weatherDataRetriever.execute(city);
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

        //Setting the click listener for the search button
        ((Button)fragmentView.findViewById(R.id.search_city_weather_btn)).setOnClickListener(searchClickListener);

        return fragmentView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[])
    {
        //Checking if the app was granted permission
        internetPermissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
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

}