package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setContentView(R.layout.list_item_forecast);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements AsyncResponse{

        public Date today;
        public int temperature;
        public String weatherCondition;
        ArrayAdapter<String> listAdapter;
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        URL openWeatherURL;
        StringBuffer sb;
        String forecastJsonString="";
        JSONTokener jsonparser;
        JSONObject jsonObject;
        JSONArray jsonArray;
        Weather[] sevenDayWeatherReport = new Weather[7];
        String[] sevenDayWeatherReportString = new String[7];
        /*     new String[]{"Today - Sunny - 18/1", "Tomorrow - Sunny - 18/5",
                "Oct 24 - Sunny - 9/-1", "Oct 25 - Snowy - 1/-5",
                "Oct 26 - Sunny - 2/-5", "Oct 27 - Overcast - 1/-10"};
        */
        public PlaceholderFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            OpenWeatherConnection owc = new OpenWeatherConnection();
            owc.aResponse = this;
            owc.execute("http://api.openweathermap.org/data/2.5/forecast/daily?zip=94043,us&cnt=7&mode=json&units=metric&appid=bd82977b86bf27fb59a04b61b657fb6f");

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            View textView = inflater.inflate(R.layout.list_item_forecast, container, false);

            try {
                while(forecastJsonString.equals("")){

                }
                jsonparser = new JSONTokener(forecastJsonString);
                jsonObject = (JSONObject)jsonparser.nextValue();
                jsonArray = jsonObject.getJSONArray("list");

                for(int i=0; i<jsonArray.length();i++){
                    Log.d("PlaceHolderFragment", jsonArray.getString(i));
                    sevenDayWeatherReport[i] = parseWeatherJson(jsonArray.getString(i));
                    sevenDayWeatherReportString[i] = sevenDayWeatherReport[i].toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            List<String> weatherList = Arrays.asList(sevenDayWeatherReportString);
            listAdapter = new ArrayAdapter<String>(getActivity()/*rootView.getContext()*/, R.layout.list_item_forecast, R.id.list_item_forecast_textview, weatherList);
            //listAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weatherList);
            /*TextView tv = (TextView)textView.findViewById(R.id.list_item_forecast_textview);
            for(int i=0; i<sevenDayWeatherReport.length; i++){
                listAdapter.add(sevenDayWeatherReport[i]);
            }
            listAdapter.add(tv.getText().toString());
            */
            ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
            listView.setAdapter(listAdapter);
            return rootView;
        }

        public Weather parseWeatherJson(String jsonString){

            Weather theWeather = new Weather();
            JSONTokener token = new JSONTokener(jsonString);
            try {
                JSONObject object = (JSONObject)token.nextValue();
                int dt = (Integer)object.get("dt");
                double speed = (Double)object.get("speed");
                int degrees = (Integer)object.get("deg");
                int clouds = (Integer)object.get("clouds");
                double pressure = (Double)object.get("pressure");
                int humidity = (Integer)object.get("humidity");
                double rain = 0.0;
                if(object.toString().contains("rain:")) {
                    rain = (Double)object.get("rain");
                }
                JSONArray weatherArray = object.getJSONArray("weather");
                JSONObject weather = weatherArray.getJSONObject(0);
                int id = (Integer)weather.get("id");
                String main = (String)weather.get("main");
                String description = (String)weather.get("description");
                String icon = (String)weather.get("icon");
                WeatherInfo weatherInfo = new WeatherInfo(id, main, description, icon);

                Log.d("PlaceHolderFragment", "ParsingJSON:" + object.getJSONObject("temp").toString());
                JSONObject temp = object.getJSONObject("temp");
                double day = ((Number)temp.get("day")).doubleValue();
                double night = ((Number)temp.get("night")).doubleValue();
                double morn = ((Number)temp.get("morn")).doubleValue();
                double eve = ((Number)temp.get("eve")).doubleValue();
                double min = ((Number)temp.get("min")).doubleValue();
                double max = ((Number)temp.get("max")).doubleValue();
                TempForecast tempForecast = new TempForecast(day, min, max, night, eve, morn);

                theWeather = new Weather(dt, speed,degrees,clouds, pressure, humidity, tempForecast, weatherInfo, rain);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return theWeather;
        }

        @Override
        public void getOutput(String output) {
            forecastJsonString = output;
        }
    }
}

class OpenWeatherConnection extends AsyncTask<String, String, String>{

    HttpURLConnection conn = null;
    BufferedReader reader = null;
    URL openWeatherURL;
    StringBuffer sb;
    String forecastJsonString;
    AsyncResponse aResponse = null;


    protected String doInBackground(String... urls) {
        try {
            openWeatherURL = new URL(urls[0]);//for zip code=94043
            //Set up HTTP connection to Open Weather API
            conn = (HttpURLConnection)openWeatherURL.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            sb = new StringBuffer();

            String line;
            while((line=reader.readLine())!= null){
                sb.append(line+"\n");
            }

            if(sb.length()==0){
                Log.e("PlaceHolderFragment","Empty result from URL");
            }else {
                forecastJsonString = sb.toString();
            }
        } catch (MalformedURLException e){
            Log.e("PlaceHolderFragment", "Bad URL");
        } catch (IOException e) {
            Log.e("PlaceHolderFragment", "IO error");
            e.printStackTrace();
        } finally {
            //disconnect connection and close and reader
            if (conn != null) {
                conn.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("PlaceHolderFragment", "IO Error ", e);
                }
            }
        }

        aResponse.getOutput(forecastJsonString);
        return forecastJsonString;

    }

    protected void onPostExecute(String result) {

        //String mine = result;
       // aResponse.getOutput(mine);
    }
}

interface AsyncResponse{
    void getOutput(String output);
}