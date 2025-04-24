package com.example.olioohjelmointiharjoitusty;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchValidity {

    public void CheckSearchValidity(Context context, String municipalityName, TextView targetView) {
        new Thread(() -> {
            try {
                String urlString = "https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_115x.px";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(responseBuilder.toString());

                JsonNode variables = rootNode.get("variables");
                boolean found = false;
                for (JsonNode variable : variables) {
                    if (variable.get("code").asText().equals("Alue")) {
                        JsonNode valueTexts = variable.get("valueTexts");
                        for (JsonNode text : valueTexts) {
                            if (text.asText().equalsIgnoreCase(municipalityName)) {
                                found = true;
                                break;
                            }
                        }
                    }
                }

                boolean finalFound = found;
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (finalFound) {
                        targetView.setText("✅ Kunta löytyi Tilastokeskukselta");
                        targetView.setTextColor(0xFF00FF00); // vihreä
                    } else {
                        targetView.setText("❌ Kuntaa ei löytynyt");
                        targetView.setTextColor(0xFFFF0000); // punainen
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    targetView.setText("⚠️ Virhe tarkistuksessa");
                    targetView.setTextColor(0xFFFFA500); // oranssi
                });
            }
        }).start();
    }
}
