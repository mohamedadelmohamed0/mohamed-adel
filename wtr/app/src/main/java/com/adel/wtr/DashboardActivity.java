package com.adel.wtr;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import com.adel.wtr.databinding.ActivityDashboardBinding;
import com.adel.wtr.details.Main;
import com.adel.wtr.details.WeatherApp;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {

    public static final String API = "48682374017b347b425ea26b251e9df2";
    private RequestQueue mRequestQueue;
    ActivityDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWeatherData("Cairo");
        searchCity();
    }

    private void searchCity() {
        // Access the TextView inside the SearchView
        int searchTextViewId = binding.cityEditText.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchTextView = binding.cityEditText.findViewById(searchTextViewId);

        // Set the text color to white
        searchTextView.setTextColor(Color.WHITE);

        // Set the hint text color to white
        searchTextView.setHintTextColor(Color.GRAY);

        binding.cityEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(s != null)
                    getWeatherData(s.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void getWeatherData(String city) {

        if(city.length() > 0)
        {
            StringBuilder sb = new StringBuilder(city);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            binding.cityNameTextView.setText(sb.toString());
        }

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=48682374017b347b425ea26b251e9df2&units=metric";
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Log.e("TAGG", response);
                WeatherApp weatherApp = new Gson().fromJson(response, WeatherApp.class);
                setData(weatherApp);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error :" + error.toString());
                Toast.makeText(DashboardActivity.this, "Unable to get weather data", Toast.LENGTH_SHORT).show();
            }
        });
        mRequestQueue.add(mStringRequest);
    }

    private void setData(WeatherApp weatherApp)
    {
        binding.temperature.setText((int)Math.floor(weatherApp.getMain().getTemp())+"");
        if(weatherApp.getWeather().size() > 0) {
            binding.weatherStatusTextView.setText("It's " + weatherApp.getWeather().get(0).getMain());
            backgroundCondition(weatherApp.getWeather().get(0).getMain());
        }
        binding.sunriseTextView.setText(convertUnixToReadableTime(weatherApp.getSys().getSunrise(), weatherApp.getTimezone())+"");
        binding.sunsetTextView.setText(convertUnixToReadableTime(weatherApp.getSys().getSunset(), weatherApp.getTimezone())+"");
        binding.humidityTextView.setText(weatherApp.getMain().getHumidity()+"%");
        backgroundCondition(binding.weatherStatusTextView.getText().toString());
    }

    private void backgroundCondition(String conditions) {
        switch (conditions){
            case "Clear Sky", "Sunny", "Clear" -> {
                binding.lottieAnimationView.setAnimation(R.raw.sun);
            }
            case "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" -> {
                binding.lottieAnimationView.setAnimation(R.raw.cloud);
            }
            case "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.lottieAnimationView.setAnimation(R.raw.rain);
            }
            case "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.lottieAnimationView.setAnimation(R.raw.snow);
            }
        }
        binding.lottieAnimationView.playAnimation();
    }

    private static String convertUnixToReadableTime(long unixTime, Long offsetInSeconds) {
         /*  Date date = new Date(unixTime * 1000); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);*/

        Instant instant = Instant.ofEpochSecond(unixTime);
        ZoneId zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofTotalSeconds(Math.toIntExact(offsetInSeconds)));
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        String formattedTime = zonedDateTime.format(formatter);
        return formattedTime;
    }
}