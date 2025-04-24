package com.example.olioohjelmointiharjoitusty.ShowData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EmploymentRateDataRetriever {

    public void getData(Context context,
                        String municipality,
                        TextView employmentRatePercentageText) {

        getData(context, municipality, employmentRatePercentageText, null);
    }

    public void getData(Context context,
                        String municipality,
                        TextView employmentRatePercentageText,
                        ResultCallback<Double> callback) {

        new Thread(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode metadata = mapper.readTree(
                        new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_115x.px"));

                JsonNode alueVar = null;
                for (JsonNode var : metadata.get("variables")) {
                    if ("Alue".equals(var.get("code").asText())) {
                        alueVar = var;
                        break;
                    }
                }
                if (alueVar == null) return;

                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> vals = new ArrayList<>();
                for (JsonNode n : alueVar.get("valueTexts")) keys.add(n.asText());
                for (JsonNode n : alueVar.get("values"))     vals.add(n.asText());

                HashMap<String, String> codes = new HashMap<>();
                for (int i = 0; i < keys.size(); i++) codes.put(keys.get(i).trim(), vals.get(i));

                String code = codes.getOrDefault(municipality.trim(),
                        codes.keySet().stream()
                                .filter(k -> k.equalsIgnoreCase(municipality.trim()))
                                .findFirst()
                                .map(codes::get)
                                .orElse(null));
                if (code == null) return;

                ObjectNode root = mapper.createObjectNode();
                ArrayNode arr = mapper.createArrayNode();

                ObjectNode tiedot = mapper.createObjectNode();
                tiedot.put("code", "Tiedot");
                ObjectNode selTiedot = mapper.createObjectNode();
                selTiedot.put("filter", "item");
                selTiedot.set("values", mapper.createArrayNode().add("tyollisyysaste"));
                tiedot.set("selection", selTiedot);
                arr.add(tiedot);

                ObjectNode alue = mapper.createObjectNode();
                alue.put("code", "Alue");
                ObjectNode selAlue = mapper.createObjectNode();
                selAlue.put("filter", "item");
                selAlue.set("values", mapper.createArrayNode().add(code));
                alue.set("selection", selAlue);
                arr.add(alue);

                ObjectNode vuosi = mapper.createObjectNode();
                vuosi.put("code", "Vuosi");
                ObjectNode selVuosi = mapper.createObjectNode();
                selVuosi.put("filter", "top");
                selVuosi.set("values", mapper.createArrayNode().add("1"));
                vuosi.set("selection", selVuosi);
                arr.add(vuosi);

                root.set("query", arr);
                root.set("response", mapper.createObjectNode().put("format", "json-stat2"));

                URL api = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_115x.px");
                HttpURLConnection con = (HttpURLConnection) api.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                byte[] body = mapper.writeValueAsBytes(root);
                try (OutputStream os = con.getOutputStream()) { os.write(body); }

                StringBuilder resp = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) resp.append(line.trim());
                }

                JsonNode valueNode = mapper.readTree(resp.toString()).get("value");
                if (valueNode == null || valueNode.size() == 0) return;

                double rate = valueNode.get(0).asDouble();
                String txt = rate + " %";

                new Handler(Looper.getMainLooper()).post(() -> {
                    employmentRatePercentageText.setText(txt);
                    if (callback != null) callback.onResult(rate);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
