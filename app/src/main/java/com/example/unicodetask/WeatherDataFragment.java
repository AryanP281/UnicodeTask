package com.example.unicodetask;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class WeatherDataFragment extends Fragment
{
    private JSONObject weatherData; //The json object containing the weather data to be displayed
    private final String MONTHS[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}; //List of months

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_weather_data, container, false);

        //Displaying the date and time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        ((TextView)fragmentView.findViewById(R.id.datetime_label)).setText(String.format("%d %s, %d:%d %s", calendar.get(Calendar.DAY_OF_MONTH),
                MONTHS[calendar.get(Calendar.MONTH)], hour > 12 ? hour - 12 : hour, calendar.get(Calendar.MINUTE), (hour < 12) ? "AM" : "PM")); //Setting the date and time

        //Displaying the weather data
        displayWeatherData(fragmentView);

        return fragmentView;
    }

    private void displayWeatherData(View fragmentView)
    {
        /*Displays the weather data from the api call*/

        //Displaying the weather data
        try
        {
            JSONObject mainObj = weatherData.getJSONObject("main"); //The json object containing the temperature data

            //Displaying the current temperature
            int currentTemperature = mainObj.getInt("temp") - 273; //Getting the current temperature
            ((TextView) fragmentView.findViewById(R.id.temp_label)).setText(String.format("%d", currentTemperature));

            //Displaying the feels like temperature
            int feelsLikeTemp = mainObj.getInt("feels_like") - 273; //Getting the feels like temperature
            ((TextView)fragmentView.findViewById(R.id.feelslike_label)).setText(String.format("Feels like %d\u2103", feelsLikeTemp));
        }
        catch(JSONException e)
        {
            Toast.makeText(getActivity(), "Unable to read weather data", Toast.LENGTH_LONG);
            getActivity().onBackPressed(); //Returning to weather search page
        }

    }

    void setWeatherData(JSONObject data)
    {
        /*Receives the weather data json object from the api call*/

        this.weatherData = data;
    }
}