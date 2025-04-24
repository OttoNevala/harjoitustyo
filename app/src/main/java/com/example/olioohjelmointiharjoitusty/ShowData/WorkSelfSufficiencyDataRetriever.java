package com.example.olioohjelmointiharjoitusty.ShowData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.example.olioohjelmointiharjoitusty.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkSelfSufficiencyDataRetriever {

    public void getData(Context context,
                        String municipality,
                        TextView workSelfSufficiencyText) {

        getData(context, municipality, workSelfSufficiencyText, null);
    }

    public void getData(Context context,
                        String municipality,
                        TextView workSelfSufficiencyText,
                        ResultCallback<Double> callback) {

        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode areas = mapper.readTree(
                        new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_125s.px"));

                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> vals = new ArrayList<>();
                for (JsonNode n : areas.get("variables").get(1).get("values"))     vals.add(n.asText());
                for (JsonNode n : areas.get("variables").get(1).get("valueTexts")) keys.add(n.asText());

                HashMap<String, String> codes = new HashMap<>();
                for (int i = 0; i < keys.size(); i++) codes.put(keys.get(i), vals.get(i));
                String code = codes.get(municipality);

                URL api = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_125s.px");
                HttpURLConnection con = (HttpURLConnection) api.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                JsonNode query = mapper.readTree(context.getResources().openRawResource(R.raw.sufficiency_query));
                ((ObjectNode) query.get("query").get(0).get("selection"))
                        .putArray("values").add(code);

                byte[] body = mapper.writeValueAsBytes(query);
                try (OutputStream os = con.getOutputStream()) { os.write(body); }

                StringBuilder resp = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) resp.append(line.trim());
                }

                JsonNode data = mapper.readTree(resp.toString());
                ArrayList<String> years = new ArrayList<>();
                for (JsonNode n : data.get("dimension").get("Vuosi").get("category").get("label"))
                    years.add(n.asText());

                JsonNode valsNode = data.get("value");
                int col = data.get("dimension").get("Tiedot").get("category").get("label").size();

                if (years.size() > 0) {
                    int base = (years.size() - 1) * col;
                    double pct = Double.valueOf(valsNode.get(base).asText());
                    String txt = "Työomavaraisuus: " + pct + "%";

                    new Handler(Looper.getMainLooper()).post(() -> {
                        workSelfSufficiencyText.setText(txt);
                        if (callback != null) callback.onResult(pct);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
