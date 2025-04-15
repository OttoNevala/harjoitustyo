package com.example.olioohjelmointiharjoitusty.ShowData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.olioohjelmointiharjoitusty.R;
import com.example.olioohjelmointiharjoitusty.SharedViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InformationFragment extends Fragment {

    private TextView temperatureText, lastSearch, humidityText, windText, weatherDescription;
    private ImageView weatherIcon;
    private SharedViewModel sharedViewModel;

    public InformationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        temperatureText = view.findViewById(R.id.temperatureText);
        lastSearch = view.findViewById(R.id.lastSearch);
        humidityText = view.findViewById(R.id.humidityText);
        windText = view.findViewById(R.id.windText);
        weatherDescription = view.findViewById(R.id.wheatherDescription);
        weatherIcon = view.findViewById(R.id.weatherIcon);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getCityName().observe(getViewLifecycleOwner(), city -> {
            if (city != null && !city.isEmpty()) {
                lastSearch.setText(city);
                fetchWeatherData(city);
            }
        });

        return view;
    }

    private void fetchWeatherData(String city) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            WeatherRepository repo = new WeatherRepository();
            try {
                String result = repo.fetch(city);
                if (result != null) {
                    //parse result and crete wheather object
                    WeatherParser.WeatherData data = WeatherParser.parse(result);

                    requireActivity().runOnUiThread(() -> updateUi(data));
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Tyhjä vastaus palvelimelta", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                // Show error if something goes wrong
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getActivity(), "Virhe haettaessa säätietoja", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUi(WeatherParser.WeatherData data) {
        int roundedTemp = (int) Math.round(data.temperature);
        temperatureText.setText(roundedTemp + "°C");
        humidityText.setText("Kosteus: " + data.humidity + "%");
        windText.setText("Tuuli: " + data.windSpeed + " m/s");
        weatherDescription.setText(data.description);

        String resourceName = "ic_" + data.iconCode;
        int resourceId = getResources().getIdentifier(resourceName, "drawable", requireActivity().getPackageName());
        weatherIcon.setImageResource(resourceId);
    }
}