package com.example.unicodetask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetriveWeatherData extends AsyncTask<String, Void, String>
{

    private Fragment weatherFragment; //The fragment that is going to display the data

    RetriveWeatherData(Fragment frag)
    {
        weatherFragment = frag;
    }

    protected String doInBackground(String... url)
    {
        try
        {
            URL requestUrl = new URL(url[0]); //The api request url
            HttpURLConnection urlConnection = (HttpURLConnection)requestUrl.openConnection(); //Sending the request
            try
            {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); //Reader for reading the request response

                //Reading the respons
                StringBuilder stringBuilder = new StringBuilder();
                String responseLine = "";
                while((responseLine = inputReader.readLine()) != null)
                {
                    stringBuilder.append(responseLine);
                }

                //Closing the input reader after use
                inputReader.close();

                return stringBuilder.toString(); //Returning the received response
            }
            catch(Exception e)
            {
                Log.e("Error", e.getMessage(), e);
            }
            finally
            {
                urlConnection.disconnect(); //Closing the url connection
            }
        }
        catch(Exception e)
        {
            Log.e("Error", e.getMessage(), e);
        }

        return null;
    }

    protected void onPostExecute(String response)
    {
        //Checking if a response was received
        if(response != null)
        {
            JSONObject jsonData = null;
            try
            {
                jsonData = new JSONObject(response); //Parsing to json
            }
            catch(JSONException e)
            {
                jsonData = null;
            }

            ((WeatherFragment)weatherFragment).displayWeatherData(jsonData);
        }
    }
}
