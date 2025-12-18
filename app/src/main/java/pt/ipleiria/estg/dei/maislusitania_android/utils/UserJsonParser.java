package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.User;

public class UserJsonParser {
    // 1. Método para converter um Objeto JSON num Objeto Java (user)
    public static User parserJsonUser(String response) {
        User auxuser = null;
        try
        {
            JSONObject jsonResponse = new JSONObject(response);

            // ✅ Verificar se a resposta foi bem-sucedida
            if (!jsonResponse.optBoolean("success", false)) {
                android.util.Log.e("UserJsonParser", "API retornou success: false");
                return null;
            }

            // ✅ Aceder ao objeto 'data' que contém os dados do utilizador
            if (!jsonResponse.has("data")) {
                android.util.Log.e("UserJsonParser", "Campo 'data' não encontrado");
                return null;
            }

            JSONObject user = jsonResponse.getJSONObject("data");


            int id = user.getInt("id");
            String primeiroNome = user.getString("primeiro_nome");
            String ultimoNome = user.getString("ultimo_nome");
            String imagemPerfil = user.getString("imagem_perfil");
            int userId = user.getInt("user_id");
            String username = user.getString("username");
            String email = user.getString("email");
            String dataAdesao = user.getString("data_adesao");


            auxuser = new User(id, primeiroNome, ultimoNome, imagemPerfil, userId, username, email, dataAdesao);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
        return auxuser;
    }

    // 2. Método para verificar a Internet
    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
