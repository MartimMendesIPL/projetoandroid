package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;

public class BilhetesJsonParser {

    /**
     * Converte um JSONArray (resposta da API) numa lista de objetos Bilhete
     */
    public static ArrayList<Bilhete> parserJsonBilhetes(JSONArray response) {
        ArrayList<Bilhete> bilhetes = new ArrayList<>();
        if (response != null) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    Bilhete bilhete = parserJsonBilhete(jsonObject);
                    bilhetes.add(bilhete);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return bilhetes;
    }

    /**
     * Converte um único JSONObject num objeto Bilhete
     */
    public static Bilhete parserJsonBilhete(JSONObject jsonObject) {
        try {
            String codigo = jsonObject.getString("codigo");
            int reserva_id = jsonObject.getInt("reserva_id");
            int local_id = jsonObject.getInt("local_id");
            String local_nome = jsonObject.getString("local_nome");
            String data_visita = jsonObject.getString("data_visita");
            int tipo_bilhete_id = jsonObject.getInt("tipo_bilhete_id");
            String tipo_bilhete_nome = jsonObject.getString("tipo_bilhete_nome");
            String estado = jsonObject.getString("estado");

            String precoStr = jsonObject.getString("preco");
            double preco = 0.0;
            try {
                preco = Double.parseDouble(precoStr.replace(",", ""));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            return new Bilhete(
                    codigo,
                    reserva_id,
                    local_id,
                    local_nome,
                    data_visita,
                    tipo_bilhete_id,
                    tipo_bilhete_nome,
                    preco,
                    estado
            );

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verifica se existe ligação à Internet
     */
    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}