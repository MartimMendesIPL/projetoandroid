package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Mapa;

public class MapaJsonParser {

    public static Mapa parserJsonMapa(String response) {
        Mapa auxMapa = null;
        try {
            JSONObject mapa = new JSONObject(response);
            auxMapa = parseSingleObject(mapa);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return auxMapa;
    }

    @NonNull
    public static ArrayList<Mapa> parserJsonMapaLocais(JSONArray response) {
        ArrayList<Mapa> mapaLocais = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject mapa = (JSONObject) response.get(i);
                Mapa auxlocal = parseSingleObject(mapa);

                // Only add if coordinates are valid
                if (auxlocal.getLatitude() != 0.0 && auxlocal.getLongitude() != 0.0) {
                    mapaLocais.add(auxlocal);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapaLocais;
    }

    // Helper method to parse a single object safely using 'opt'
    private static Mapa parseSingleObject(JSONObject mapa) {
        int id = mapa.optInt("id", 0);
        String nome = mapa.optString("nome", "Sem Nome");
        String imagem = mapa.optString("imagem", ""); // Returns empty string if null
        String tipo = mapa.optString("tipo", "");

        // Handle coordinates carefully. If they are strings in JSON, optDouble still works.
        Double latitude = mapa.optDouble("latitude", 0.0);
        Double longitude = mapa.optDouble("longitude", 0.0);

        // Use a default marker if null
        String markerImagem = mapa.optString("markerImagem", "");

        return new Mapa(id, nome, imagem, tipo, markerImagem, latitude, longitude);
    }

    // Converts Java List -> JSON String for the WebView
    public static String mapasListToJson(ArrayList<Mapa> lista) {
        JSONArray jsonArray = new JSONArray();
        for (Mapa m : lista) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", m.getId());
                jsonObject.put("nome", m.getNome());
                // Handle full URL logic here if needed
                jsonObject.put("imagem", m.getImagem());
                jsonObject.put("tipo", m.getTipo());
                jsonObject.put("latitude", m.getLatitude());
                jsonObject.put("longitude", m.getLongitude());

                // If marker is empty, you might want to send a default path,
                // or handle the empty string in your HTML JS.
                jsonObject.put("markerImagem", m.getMarkerImagem());

                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}