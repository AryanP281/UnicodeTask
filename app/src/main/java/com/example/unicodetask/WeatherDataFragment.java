package com.example.unicodetask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class WeatherDataFragment extends Fragment
{
    private final String WEATHER_DATA = "WEATHER_DATA"; //The key of the weather data when saved in bundle

    private JSONObject weatherData; //The json object containing the weather data to be displayed
    private final String MONTHS[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}; //List of months

    class RetrieveWeatherIcon extends AsyncTask<String, Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... iconName)
        {
            Bitmap iconBitmap = null; //The bitmap for the icon
            InputStream inputStream = null; //Input stream to the icon

            try
            {
                URL iconUrl = new URL(String.format("https://openweathermap.org/img/wn/%s@2x.png", iconName)); //The api request url for the weather icon

                //Reading the image data
                inputStream = iconUrl.openStream(); //Input stream to the icon image
                iconBitmap = BitmapFactory.decodeStream(inputStream); //Getting the icon image from the stream
            }
            catch (Exception e)
            {
                Log.e("ERROR", e.getMessage(), e);
            }
            finally
            {
                try
                {
                    //Closing the stream
                    inputStream.close();
                }
                catch(IOException e) {}
            }

            return iconBitmap;
        }

    }; //Async Task for getting the current weather icon from api

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_weather_data, container, false);

        //Retrieving the saved instance
        if(savedInstanceState != null)
        {
            try
            {
                weatherData = new JSONObject(savedInstanceState.getString(WEATHER_DATA)); //Converting bundled string to json object
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                //Failed to retrieve weather data
                getActivity().onBackPressed(); //Returning to weather search fragment
            }
        }

        //Displaying the date and time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        ((TextView)fragmentView.findViewById(R.id.datetime_label)).setText(String.format("%d %s, %d:%d %s", calendar.get(Calendar.DAY_OF_MONTH),
                MONTHS[calendar.get(Calendar.MONTH)], hour > 12 ? hour - 12 : hour, calendar.get(Calendar.MINUTE), (hour < 12) ? "AM" : "PM")); //Setting the date and time

        //Displaying the weather data
        displayWeatherData(fragmentView);

        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        //Saving the weather data
        outState.putString(WEATHER_DATA, weatherData.toString());
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

            //Displaying the weather conditions
            JSONArray weatherArray = weatherData.getJSONArray("weather"); //The array in the json response containing the weather conditions data
           RetrieveWeatherIcon iconRetriever = (RetrieveWeatherIcon)new RetrieveWeatherIcon().execute(weatherArray.getJSONObject(0).getString("icon"));
           try
           {
               Bitmap weatherIconBitmap = (Bitmap) iconRetriever.get(); //Getting the icon bitmap
               ((ImageView)fragmentView.findViewById(R.id.weather_icon)).setImageBitmap(weatherIconBitmap); //Displaying the weather icon
           }
           catch (Exception e)
           {
               //Display default icon
           }
           ((TextView)fragmentView.findViewById(R.id.weather_cond_label)).setText(weatherArray.getJSONObject(0).getString("main")); //Setting the weather condition label

            //Displaying the min and max temps
            int minAndMaxTemps[] = {mainObj.getInt("temp_min") - 273, mainObj.getInt("temp_max") - 273};
            ((TextView)fragmentView.findViewById(R.id.min_temp)).setText(String.format("Min Temperature: %d\u2103", minAndMaxTemps[0])); //Displaying the min temp
            ((TextView)fragmentView.findViewById(R.id.max_temp)).setText(String.format("Max Temperature: %d\u2103", minAndMaxTemps[1])); //Displaying the max temp

            //Displaying the pressure
            ((TextView)fragmentView.findViewById(R.id.pressure)).setText(String.format("Pressure: %d mBar", mainObj.getInt("pressure")));

            //Displaying the humidity
            ((TextView)fragmentView.findViewById(R.id.humidity)).setText(String.format("Humidity: %d %%", mainObj.getInt("humidity")));

        }
        catch(JSONException e)
        {
            Toast.makeText(getActivity(), "Unable to read weather data", Toast.LENGTH_LONG).show();
            getActivity().onBackPressed(); //Returning to weather search page
        }

    }

    void setWeatherData(JSONObject data)
    {
        /*Receives the weather data json object from the api call*/

        this.weatherData = data;
    }
}