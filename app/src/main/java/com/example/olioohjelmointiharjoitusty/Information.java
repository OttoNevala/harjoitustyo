package com.example.olioohjelmointiharjoitusty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Information extends AppCompatActivity {

    private Button searchTown, compareTowns;
    private TextView temperatureText, lastSearch, humidityText, windText, weatherDescription;
    private ImageView weatherIcon;
    private static final String API_KEY = "35b1ec02c88561cae8dd2c3fd7b38e4e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information);

        searchTown = findViewById(R.id.searchTown);
        compareTowns = findViewById(R.id.CompareTowns); // Varmista, että id vastaa XML:ssä
        temperatureText = findViewById(R.id.temperatureText);
        lastSearch = findViewById(R.id.lastSearch);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windText);
        // Huomioi: jos XML:ssä on kirjoitettu "wheatherDescription", päivitä joko XML tai koodi vastaamaan samaa kirjoitusasua.
        weatherDescription = findViewById(R.id.wheatherDescription);
        weatherIcon = findViewById(R.id.weatherIcon);

        Intent intent = getIntent();
        String city = intent.getStringExtra("cityName");
        if (city != null && !city.isEmpty()) {
            lastSearch.setText(city);
            fetchWeatherData(city);
        }

        searchTown.setOnClickListener(v -> {
            Intent newIntent = new Intent(Information.this, MainActivity.class);
            startActivity(newIntent);
        });

        compareTowns.setOnClickListener(v -> {
            // Lisää toiminnallisuus tarvittaessa
        });
    }

    private void fetchWeatherData(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + API_KEY + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = client.newCall(request).execute();
                if (response.body() != null) {
                    String result = response.body().string();
                    runOnUiThread(() -> updateUi(result));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void updateUi(String result) {
        if (result != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                JSONObject wind = jsonObject.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");

                String description = jsonObject.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("description");
                String iconCode = jsonObject.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("icon");
                String resourceName = "ic_" + iconCode;
                // getResources() on metodikutsun avulla saatavilla suoraan Activity-luokassa
                int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
                weatherIcon.setImageResource(resourceId);

                int roundedTemp = (int) Math.round(temperature);
                temperatureText.setText(roundedTemp + "°C");
                humidityText.setText("Kosteus: " + humidity + "%");
                windText.setText("Tuuli: " + windSpeed + " m/s");
                weatherDescription.setText(description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
