//Read me
//This class is created for searching for a city

package com.example.olioohjelmointiharjoitusty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class SearchFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        EditText cityNameInput = view.findViewById(R.id.cityNameInput);
        Button searchCity = view.findViewById(R.id.searchCity);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        searchCity.setOnClickListener(v -> {
            String city = cityNameInput.getText().toString().trim();
            if (!city.isEmpty()) {
                sharedViewModel.setCityName(city);
                Toast.makeText(getContext(), "Tietoja haetaan...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Syötä kunnan nimi", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}