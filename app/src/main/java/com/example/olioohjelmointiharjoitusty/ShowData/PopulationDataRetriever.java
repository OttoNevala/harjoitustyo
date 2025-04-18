package com.example.olioohjelmointiharjoitusty.ShowData;


import android.content.Context;
import android.widget.TextView;
import android.os.Handler;
import android.os.Looper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.olioohjelmointiharjoitusty.R;

/**
 * PopulationDataRetriever fetches population and population change data from the Tilastokeskus API.
 * It uses a JSON query stored in R.raw.population_query and updates the provided TextView (populationText)
 * on the main thread with the latest population and percent change data.
 */
public class PopulationDataRetriever {

    // Retrieves population data and updates the provided TextView.
    public void getData(final Context context, final String municipality, final TextView populationText) {
        new Thread(() -> {
            ObjectMapper objectMapper = new ObjectMapper();

            // First, retrieve the municipality codes from the API
            JsonNode areas = null;
            try {
                areas = objectMapper.readTree(
                        new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px")
                );
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            System.out.println(areas.toPrettyString());

            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            // Loop through the municipality codes in the "Alue" dimension
            for (JsonNode node : areas.get("variables").get(1).get("values")) {
                values.add(node.asText());
            }
            for (JsonNode node : areas.get("variables").get(1).get("valueTexts")) {
                keys.add(node.asText());
            }

            HashMap<String, String> municipalityCodes = new HashMap<>();
            for (int i = 0; i < keys.size(); i++) {
                municipalityCodes.put(keys.get(i), values.get(i));
            }
            String code = municipalityCodes.get(municipality);

            try {
                // This opens connection to the API that delivers population data.
                URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                // Load the JSON query from raw resources.
                // The query should include the necessary dimensions (for example "Alue", "Vuosi", and "Tiedot")
                JsonNode jsonInputString = objectMapper.readTree(
                        context.getResources().openRawResource(R.raw.population_query)
                );
                // Insert the municipality code into the query under the "Alue" dimension.
                ((ObjectNode) jsonInputString.get("query").get(0).get("selection"))
                        .putArray("values").add(code);

                // Write the query to the API.
                byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
                OutputStream os = con.getOutputStream();
                os.write(input, 0, input.length);
                os.close();

                // Read the API response.
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                br.close();

                // Parse the JSON response.
                JsonNode municipalityData = objectMapper.readTree(response.toString());

                // Extract the year labels from the "Vuosi" dimension.
                ArrayList<String> years = new ArrayList<>();
                for (JsonNode node : municipalityData.get("dimension").get("Vuosi").get("category").get("label")) {
                    years.add(node.asText());
                }

                // Get the data values from the "value" field.
                JsonNode valuesNode = municipalityData.get("value");
                // In this query, we expect two measures: population and population change.
                int index = municipalityData.get("dimension")
                        .get("Tiedot").get("category").get("label").size();

                // Retrieve the data for the most recent year.
                if (years.size() > 0) {
                    int lastIndex = years.size() - 1;
                    int baseIndex = lastIndex * index;
                    // The population is found at baseIndex + 1.
                    int population = Integer.valueOf(valuesNode.get(baseIndex + 1).asText());

                    // This calculates percent change using the previous year's population.
                    double percentChange = 0;
                    if (lastIndex > 0) {
                        int previousPopulation = Integer.valueOf(valuesNode.get((lastIndex - 1) * index + 1).asText());
                        if (previousPopulation > 0) {
                            percentChange = (population - previousPopulation) / (double) previousPopulation * 100;
                            percentChange = Math.round(percentChange * 100.0) / 100.0;
                        }
                    }

                    // This makes the result text
                    String resultText = "Väestö: " + population
                            + "\nVäestön muutos: " + percentChange + "%";

                    // This updates the UI on the main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        populationText.setText(resultText);
                    });
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
