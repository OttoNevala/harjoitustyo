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
import com.example.olioohjelmointiharjoitusty.ShowData.ResultCallback;

/**
 * PopulationDataRetriever fetches population and population change data from the Tilastokeskus API.
 * It uses a JSON query stored in R.raw.population_query and updates the provided TextView (populationText)
 * on the main thread with the latest population and percent change data.
 */
public class PopulationDataRetriever {


    public void getData(final Context context,
                        final String municipality,
                        final TextView populationText) {
        getData(context, municipality, populationText, null);
    }


    public void getData(final Context context,
                        final String municipality,
                        final TextView populationText,
                        final ResultCallback<Integer> callback) {

        new Thread(() -> {
            ObjectMapper objectMapper = new ObjectMapper();



            // First, retrieve the municipality codes from the API
            JsonNode areas = null;
            try {
                areas = objectMapper.readTree(
                        new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px")
                );
            } catch (MalformedURLException e) { e.printStackTrace(); return; }
            catch (IOException e)         { e.printStackTrace(); return; }

            ArrayList<String> keys   = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            for (JsonNode node : areas.get("variables").get(1).get("values"))
                values.add(node.asText());
            for (JsonNode node : areas.get("variables").get(1).get("valueTexts"))
                keys.add(node.asText());

            HashMap<String, String> municipalityCodes = new HashMap<>();
            for (int i = 0; i < keys.size(); i++)
                municipalityCodes.put(keys.get(i), values.get(i));

            String code = municipalityCodes.get(municipality);

            try {
                URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JsonNode jsonInputString = objectMapper.readTree(
                        context.getResources().openRawResource(R.raw.population_query));
                ((ObjectNode) jsonInputString.get("query").get(0).get("selection"))
                        .putArray("values").add(code);

                byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
                OutputStream os = con.getOutputStream();
                os.write(input, 0, input.length);
                os.close();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line; while ((line = br.readLine()) != null) response.append(line.trim());
                br.close();

                JsonNode municipalityData = objectMapper.readTree(response.toString());

                ArrayList<String> years = new ArrayList<>();
                for (JsonNode node : municipalityData.get("dimension")
                        .get("Vuosi").get("category").get("label"))
                    years.add(node.asText());

                JsonNode valuesNode = municipalityData.get("value");
                int index = municipalityData.get("dimension")
                        .get("Tiedot").get("category").get("label").size();

                if (years.size() > 0) {
                    int lastIndex  = years.size() - 1;
                    int baseIndex  = lastIndex * index;
                    int population = Integer.parseInt(valuesNode.get(baseIndex + 1).asText());

                    /* --- prosenttimuutoslaskenta --- */
                    double percentChange = 0;
                    if (lastIndex > 0) {
                        int prev = Integer.parseInt(valuesNode.get((lastIndex - 1) * index + 1).asText());
                        if (prev > 0) {
                            percentChange = (population - prev) / (double) prev * 100;
                            percentChange = Math.round(percentChange * 100.0) / 100.0;
                        }
                    }

                    String resultText = "Väestö: " + population +
                            "\nVäestön muutos: " + percentChange + "%";

                    new Handler(Looper.getMainLooper()).post(() -> {
                        populationText.setText(resultText);
                        if (callback != null) callback.onResult(population); // ← UUSI
                    });
                }

            } catch (MalformedURLException e) { e.printStackTrace();
            } catch (IOException e)         { e.printStackTrace(); }
        }).start();
    }
}
