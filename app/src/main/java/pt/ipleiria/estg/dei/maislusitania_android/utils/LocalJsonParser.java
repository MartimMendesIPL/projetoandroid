package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Local;

public class LocalJsonParser {

    // 1. Método para converter um Objeto JSON num Objeto Java (Local)
    public static Local parserJsonLocal(String response) {
        Local auxlocal = null;
        try
        {
            JSONObject local = new JSONObject(response);
            int id = local.getInt("id");
            String nome = local.getString("nome");
            String morada = local.getString("morada");
            String distrito = local.getString("ditrito");
            String descricao = local.getString("descricao");
            String imagem = local.getString("imagem");
            float avaliacaoMedia = (float) local.optDouble("avaliacao_media", 0.0);



            auxlocal = new Local(id, nome, morada, distrito, descricao, imagem, avaliacaoMedia);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
        return auxlocal;
    }

    // 2. Método para converter uma LISTA de JSONs numa LISTA de Locais
        @NonNull
        public static ArrayList<Local> parserJsonLocais(JSONArray response) {
            ArrayList<Local> locais = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject local = (JSONObject) response.get(i);
                    int id = local.getInt("id");
                    String nome = local.getString("nome");
                    String morada = local.getString("morada");
                    String distrito = local.getString("distrito");
                    String descricao = local.getString("descricao");
                    String imagem = local.getString("imagem");
                    float avaliacaoMedia = (float) local.optDouble("avaliacao_media", 0.0);


                    Local auxlocal = new Local(id, nome, morada, distrito, descricao, imagem, avaliacaoMedia);
                    locais.add(auxlocal);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            return locais;
        }


    // 3. Método para verificar a Internet
    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}