//Read me
//This class is created for searching for a city

package com.example.olioohjelmointiharjoitusty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olioohjelmointiharjoitusty.ShowData.EmploymentRateDataRetriever;
import com.example.olioohjelmointiharjoitusty.ShowData.PopulationDataRetriever;
import com.example.olioohjelmointiharjoitusty.ShowData.WorkSelfSufficiencyDataRetriever;

import com.example.olioohjelmointiharjoitusty.history.SearchHistoryAdapter;
import com.example.olioohjelmointiharjoitusty.history.SearchHistoryStorage;
import com.example.olioohjelmointiharjoitusty.SearchValidity;

public class SearchFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private SearchHistoryAdapter historyAdapter;

    // uudet DataRetriever-oliot
    private final PopulationDataRetriever          populationDataRetriever    = new PopulationDataRetriever();
    private final EmploymentRateDataRetriever      employmentRateDataRetriever= new EmploymentRateDataRetriever();
    private final WorkSelfSufficiencyDataRetriever sufficiencyDataRetriever   = new WorkSelfSufficiencyDataRetriever();

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        EditText  cityNameInput = view.findViewById(R.id.cityNameInput);
        Button    searchCity    = view.findViewById(R.id.searchCity);
        TextView  searchStatus  = view.findViewById(R.id.searchStatus);
        RecyclerView recycler   = view.findViewById(R.id.searchHistoryRecycler);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new SearchHistoryAdapter(SearchHistoryStorage.getInstance().getSearchHistory());
        recycler.setAdapter(historyAdapter);

        searchCity.setOnClickListener(v -> {
            String city = cityNameInput.getText().toString().trim();
            if (city.isEmpty()) {
                Toast.makeText(getContext(), "Syötä kunnan nimi", Toast.LENGTH_SHORT).show();
                return;
            }

            new SearchValidity().CheckSearchValidity(requireContext(), city, searchStatus);

            // tallennetaan ensimmäinen ja toinen kaupunki
            if (sharedViewModel.getFirstCity().getValue() == null) {
                sharedViewModel.setFirstCity(city);
                fetchStatistics(city, true);
                Toast.makeText(getContext(), "1. kaupunki tallennettu: " + city, Toast.LENGTH_SHORT).show();
            } else if (sharedViewModel.getSecondCity().getValue() == null &&
                    !city.equalsIgnoreCase(sharedViewModel.getFirstCity().getValue())) {
                sharedViewModel.setSecondCity(city);
                fetchStatistics(city, false);
                Toast.makeText(getContext(), "2. kaupunki tallennettu: " + city, Toast.LENGTH_SHORT).show();

                // siirry suoraan Visa-välilehdelle
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).switchToTab(2);
                }
            } else {
                Toast.makeText(getContext(), "Kaksi kaupunkia on jo valittu.", Toast.LENGTH_SHORT).show();
            }

            sharedViewModel.setCityName(city);
            SearchHistoryStorage.getInstance().addSearchEntry(city);
            historyAdapter.updateData(SearchHistoryStorage.getInstance().getSearchHistory());
        });

        historyAdapter.setOnItemClickListener(city -> {
            cityNameInput.setText(city);
            sharedViewModel.setCityName(city);
            Toast.makeText(getContext(), "Tietoja haetaan: " + city, Toast.LENGTH_SHORT).show();

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(1);  // Tiedot-tab
            }
        });
    }

    /** Hakee väkiluvun, työllisyysasteen ja työomavaraisuuden,
     *  tallettaa tulokset SharedViewModel-instanssiin. */
    private void fetchStatistics(String city, boolean first) {

        populationDataRetriever.getData(requireContext(), city, new TextView(requireContext()),
                value -> {
                    if (first) sharedViewModel.setPopulation1(value);
                    else       sharedViewModel.setPopulation2(value);
                });

        employmentRateDataRetriever.getData(requireContext(), city, new TextView(requireContext()),
                value -> {
                    if (first) sharedViewModel.setEmployment1(value);
                    else       sharedViewModel.setEmployment2(value);
                });

        sufficiencyDataRetriever.getData(requireContext(), city, new TextView(requireContext()),
                value -> {
                    if (first) sharedViewModel.setSufficiency1(value);
                    else       sharedViewModel.setSufficiency2(value);
                });
    }
}
