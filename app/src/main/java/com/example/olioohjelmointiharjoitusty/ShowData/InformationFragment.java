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
import com.example.olioohjelmointiharjoitusty.ShowData.WorkSelfSufficiencyDataRetriever;


import com.example.olioohjelmointiharjoitusty.ShowData.EmploymentRateDataRetriever;
import com.example.olioohjelmointiharjoitusty.ShowData.PopulationDataRetriever;
import com.example.olioohjelmointiharjoitusty.ShowData.PopulationData;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InformationFragment extends Fragment {

    private TextView temperatureText, lastSearch, humidityText, windText, weatherDescription;
    private TextView populationText, employmentText, selfSufficiencyText;
    private ImageView weatherIcon;
    private SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);

        temperatureText = view.findViewById(R.id.temperatureText);
        lastSearch = view.findViewById(R.id.lastSearch);
        humidityText = view.findViewById(R.id.humidityText);
        windText = view.findViewById(R.id.windText);
        weatherDescription = view.findViewById(R.id.wheatherDescription);
        weatherIcon = view.findViewById(R.id.weatherIcon);
        populationText = view.findViewById(R.id.populationValue);
        employmentText = view.findViewById(R.id.employmentRateValue);
        selfSufficiencyText = view.findViewById(R.id.selfSufficiencyValue);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getCityName().observe(getViewLifecycleOwner(), city -> {
            if (city != null && !city.isEmpty()) {
                lastSearch.setText(city);
                fetchWeatherData(city);
                fetchPopulationData(city);
                fetchEmploymentData(city);
                fetchSelfSufficiencyData(city);

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
                    WeatherParser.WeatherData data = WeatherParser.parse(result);
                    requireActivity().runOnUiThread(() -> updateUi(data));
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(), "Tyhjä vastaus palvelimelta", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
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

    private void fetchPopulationData(String city) {
        new PopulationDataRetriever().getData(requireContext(), city, populationText, population -> {
            populationText.setText(population + "");
            if (city.equalsIgnoreCase(sharedViewModel.getFirstCity().getValue())) {
                sharedViewModel.setPopulation1(population);
            } else if (city.equalsIgnoreCase(sharedViewModel.getSecondCity().getValue())) {
                sharedViewModel.setPopulation2(population);
            }
        });
    }

    private void fetchEmploymentData(String city) {
        new EmploymentRateDataRetriever().getData(requireContext(), city, employmentText, rate -> {
            employmentText.setText(String.format("%.1f %%", rate));
            if (city.equalsIgnoreCase(sharedViewModel.getFirstCity().getValue())) {
                sharedViewModel.setEmployment1(rate);
            } else if (city.equalsIgnoreCase(sharedViewModel.getSecondCity().getValue())) {
                sharedViewModel.setEmployment2(rate);
            }
        });
    }

    private void fetchSelfSufficiencyData(String city) {
        new WorkSelfSufficiencyDataRetriever().getData(requireContext(), city, selfSufficiencyText, rate -> {
            selfSufficiencyText.setText(String.format("Työomavaraisuus: %.1f %%", rate));
            if (city.equalsIgnoreCase(sharedViewModel.getFirstCity().getValue())) {
                sharedViewModel.setSufficiency1(rate);
            } else if (city.equalsIgnoreCase(sharedViewModel.getSecondCity().getValue())) {
                sharedViewModel.setSufficiency2(rate);
            }
        });
    }
}