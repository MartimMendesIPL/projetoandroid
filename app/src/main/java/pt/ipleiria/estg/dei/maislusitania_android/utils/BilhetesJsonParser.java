package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;

public class BilhetesJsonParser {

    // 1. Método para converter um Objeto JSON num Objeto Java (Bilhete)
    public static Bilhete parserJsonBilhete(String response) {
        Bilhete auxBilhete = null;
        try {
            JSONObject bilhete = new JSONObject(response);
            String codigo = bilhete.getString("codigo");
            int reserva_id = bilhete.getInt("reserva_id");

            // Parser do objeto local aninhado
            JSONObject localJson = bilhete.getJSONObject("local");
            int localId = localJson.getInt("id");
            String localNome = localJson.getString("nome");
            Bilhete.Local local = new Bilhete.Local(localId, localNome);

            String data_visita = bilhete.getString("data_visita");
            String tipo_bilhete = bilhete.getString("tipo_bilhete");
            String preco = bilhete.getString("preco");
            String estado = bilhete.getString("estado");
            String data_criacao = bilhete.getString("data_criacao");

            auxBilhete = new Bilhete(codigo, reserva_id, local, data_visita,
                    tipo_bilhete, preco, estado, data_criacao);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return auxBilhete;
    }

    // 2. Método para converter uma LISTA de JSONs numa LISTA de Bilhetes
    @NonNull
    public static ArrayList<Bilhete> parserJsonBilhetes(JSONArray response) {
        ArrayList<Bilhete> bilhetes = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject bilhete = (JSONObject) response.get(i);
                String codigo = bilhete.getString("codigo");
                int reserva_id = bilhete.getInt("reserva_id");

                // Parser do objeto local aninhado
                JSONObject localJson = bilhete.getJSONObject("local");
                int localId = localJson.getInt("id");
                String localNome = localJson.getString("nome");
                Bilhete.Local local = new Bilhete.Local(localId, localNome);

                String data_visita = bilhete.getString("data_visita");
                String tipo_bilhete = bilhete.getString("tipo_bilhete");
                String preco = bilhete.getString("preco");
                String estado = bilhete.getString("estado");
                String data_criacao = bilhete.getString("data_criacao");

                Bilhete auxBilhete = new Bilhete(codigo, reserva_id, local, data_visita,
                        tipo_bilhete, preco, estado, data_criacao);
                bilhetes.add(auxBilhete);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return bilhetes;
    }

    // 3. Método para verificar a Internet
    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
