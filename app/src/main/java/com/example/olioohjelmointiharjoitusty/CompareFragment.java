/*package com.example.olioohjelmointiharjoitusty.comparison;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.olioohjelmointiharjoitusty.R;
import com.example.olioohjelmointiharjoitusty.data.PopulationDataFetcher;
import com.example.olioohjelmointiharjoitusty.data.EmploymentDataFetcher;

public class CompareFragment extends Fragment {

    private TextView cityOneName;
    private TextView cityTwoName;
    private TextView cityOnePopulation;
    private TextView cityTwoPopulation;
    private TextView cityOneEmployment;
    private TextView cityTwoEmployment;

    public CompareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compare, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        cityOneName = view.findViewById(R.id.cityOneName);
        cityTwoName = view.findViewById(R.id.cityTwoName);
        cityOnePopulation = view.findViewById(R.id.cityOnePopulation);
        cityTwoPopulation = view.findViewById(R.id.cityTwoPopulation);
        cityOneEmployment = view.findViewById(R.id.cityOneEmployment);
        cityTwoEmployment = view.findViewById(R.id.cityTwoEmployment);

        String city1 = "Helsinki";
        String city2 = "Tampere";

        cityOneName.setText(city1);
        cityTwoName.setText(city2);

        int population1 = PopulationDataFetcher.getPopulation(city1);
        int population2 = PopulationDataFetcher.getPopulation(city2);

        cityOnePopulation.setText(String.valueOf(population1));
        cityTwoPopulation.setText(String.valueOf(population2));

        double employment1 = EmploymentDataFetcher.getEmploymentRate(city1);
        double employment2 = EmploymentDataFetcher.getEmploymentRate(city2);

        cityOneEmployment.setText(String.format("%.1f%%", employment1));
        cityTwoEmployment.setText(String.format("%.1f%%", employment2));
    }
}*/
