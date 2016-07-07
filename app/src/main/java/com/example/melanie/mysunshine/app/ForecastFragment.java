package com.example.melanie.mysunshine.app;

/**
 * Created by Melanie on 6/2/2016.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.util.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.net.Uri;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter mForecastAdapter;
    private View rootView;

    public class FetchWeatherTask extends AsyncTask<String, Void,String[] > {


        @Override
        protected String[] doInBackground(String... params) {
            String forecastJsonStr=null;
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            String[] forecast=null;
            WeatherDataParser weatherData = new WeatherDataParser();
            int numDays =7;

            try{
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter("q", params[0])
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("appid", "b2ba93254e510fd6fb28f2f53ca9bbec")
                        .appendQueryParameter("units", "metric");
                //String myUrl = builder.build().toString();
                //API LINK == "http://api.openweathermap.org/data/2.5/forecast/daily?q=Atlanta,GA&mode=json&appid=b2ba93254e510fd6fb28f2f53ca9bbec&units=metric"
                //LOG.(builder.build().toString());
                Log.v("ForecastFragment", "uri build "+builder.build().toString());
                URL url= new URL(builder.build().toString());
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream= connection.getInputStream();
                StringBuffer buffer= new StringBuffer();
            /*if(buffer=null){
                forecastJsonStr=null;
            }*/
                reader = new BufferedReader(new InputStreamReader(inputStream)) ;
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();


            }catch(IOException e){
                Log.e("ForecastFragment", "Error ", e);
                return null;
            }finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                forecast = weatherData.getWeatherDataFromJson(forecastJsonStr, numDays);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return forecast;
        }

        @Override
        protected void onPostExecute(String[] weatherCast) {

        /*
                ArrayList<String> weekForecast = new ArrayList<String>(Arrays.asList(weatherCast));
                mForecastAdapter = new ArrayAdapter<>(
                        getActivity(),
                        R.layout.list_item_forecast,
                        R.id.list_item_forecast_textview,
                        weekForecast);
                ListView forecastView = (ListView) rootView.findViewById(R.id.listView_forecast);
                forecastView.setAdapter(mForecastAdapter);
        */
            if(weatherCast!=null){
                mForecastAdapter.clear();
                for(String dayForecastStr :weatherCast){
                    mForecastAdapter.add(dayForecastStr);
                }
            }
           // super.onPostExecute(o);
        }
    }



    public ForecastFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        inflater.inflate(R.menu.forecastfragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            String cityName= "Atlanta";
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("Atlanta");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);


        //String[] foreCast ={"Today - Sunny","Tomorrow - Rainy","Wednesday - Foogy"};
        //ArrayList<String> weekForecast= new ArrayList<String>(Arrays.asList(foreCast));
        mForecastAdapter= new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview
                );
        ListView forecastView=(ListView)rootView.findViewById(R.id.listView_forecast);
        forecastView.setAdapter(mForecastAdapter);



        return rootView;
    }
}