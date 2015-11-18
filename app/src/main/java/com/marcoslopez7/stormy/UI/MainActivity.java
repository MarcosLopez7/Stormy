package com.marcoslopez7.stormy.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.marcoslopez7.stormy.R;
import com.marcoslopez7.stormy.weather.Current;
import com.marcoslopez7.stormy.weather.Day;
import com.marcoslopez7.stormy.weather.Forecast;
import com.marcoslopez7.stormy.weather.Hour;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static String DAILY_FORECAST = "DAILY_FORECAST";
    private Forecast forecast;

    @Bind(R.id.time) TextView time;
    @Bind(R.id.temperature) TextView temperature;
    @Bind(R.id.humidityValue) TextView humidity;
    @Bind(R.id.precipitationValue) TextView precipitation;
    @Bind(R.id.summary) TextView summary;
    @Bind(R.id.icon) ImageView icon;
    @Bind(R.id.refresh) ImageView refrsh;
    @Bind(R.id.progressBar) ProgressBar progress;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progress.setVisibility(View.INVISIBLE);
        final double latitude = 19.3771716;
        final double longitud = -99.1519004;

        forecast = new Forecast();
        refrsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitud);

            }
        });


    }

    private void getForecast(double latitude, double longitud) {
        String apiKey = "14c8bb3b28365cedd1b19be118467f5f";

        String forecastURL = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitud;

        if(isNetworkAvailable()) {
            refresh();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh();
                        }
                    });
                    AlertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh();
                        }
                    });
                    try {
                        String jsondata = response.body().string();
                        if (response.isSuccessful()) {
                            forecast = parseForecastDetails(jsondata);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });

                        } else {
                            AlertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: " + e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception caught: " + e);
                    }
                }
            });

            Log.d(TAG, "Main UI is running");
        }
        else
            Toast.makeText(this, R.string.error_network, Toast.LENGTH_LONG);
    }

    private Forecast parseForecastDetails (String jsonData) throws JSONException{
        Forecast forecast_new = new Forecast();
        forecast_new.setCurrent(getCurrentDetails(jsonData));
        forecast_new.setHourlyForecast(getHourlyForecast(jsonData));
        forecast_new.setDailyForecast(getDailyForecast(jsonData));

        return forecast_new;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast_new = new JSONObject(jsonData);
        String timezone = forecast_new.getString("timezone");

        JSONObject daily = forecast_new.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] days = new Day[data.length()];

        for (int i = 0; i < data.length(); i++){
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();

            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTime(jsonDay.getLong("time"));
            day.setTimezone(timezone);

            days[i] = day;
        }

        return days;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast_new = new JSONObject(jsonData);
        String timezone = forecast_new.getString("timezone");
        JSONObject hourly = forecast_new.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");
        Hour[] hours = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++)
        {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }

    private void refresh() {
        if(progress.getVisibility() == View.INVISIBLE){
            progress.setVisibility(View.VISIBLE);
            refrsh.setVisibility(View.INVISIBLE);
        }
        else{
            progress.setVisibility(View.INVISIBLE);
            refrsh.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay()
    {
        Current current = forecast.getCurrent();
        temperature.setText(current.getTemperature() + "");
        time.setText("En " + current.getFormattedTime() + " estará en");
        humidity.setText(current.getHumidity() + "");
        precipitation.setText(current.getPrecipChance() + "%");
        summary.setText(current.getSummary());

        Drawable drawable = getResources().getDrawable(current.getIconID());
        icon.setImageDrawable(drawable);
    }

    private Current getCurrentDetails (String json) throws JSONException
    {
        JSONObject forecast = new JSONObject(json);
        JSONObject currently = forecast.getJSONObject("currently");

        String timezone = forecast.getString("timezone");
        Log.i(TAG, "JSON: " + timezone);

        Current data = new Current();
        data.setIcon(currently.getString("icon"));
        data.setPrecipChance(currently.getDouble("precipProbability"));
        data.setSummary(currently.getString("summary"));
        data.setTemperature(currently.getDouble("temperature"));
        data.setTime(currently.getLong("time"));
        data.setTimeZone(timezone);
        return data;
    }

    private void AlertUserAboutError()
    {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected())
        {
                    isAvailable = true;
        }

        return isAvailable;
    }

    @OnClick(R.id.daily)
    public void startDailyActivity(View view){
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, forecast.getDailyForecast());
        startActivity(intent);
    }
}
