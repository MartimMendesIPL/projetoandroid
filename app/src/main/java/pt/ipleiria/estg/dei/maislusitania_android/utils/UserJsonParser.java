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
    public static User parserJsonUser(JSONArray response) {
        User auxuser = null;
        try {
            //Verificar se o array não está vazio
            if (response.length() == 0) {
                android.util.Log.e("UserJsonParser", "Array vazio");
                return null;
            }

            //Pegar o primeiro objeto do array
            JSONObject user = response.getJSONObject(0);

            int id = user.getInt("id");
            String primeiroNome = user.getString("primeiro_nome");
            String ultimoNome = user.getString("ultimo_nome");
            String imagemPerfil = user.optString("imagem_perfil", ""); // ✅ Usar optString para null
            int userId = user.getInt("user_id");
            String username = user.getString("username");
            String email = user.getString("email");
            String dataAdesao = user.getString("data_adesao");

            auxuser = new User(id, primeiroNome, ultimoNome, imagemPerfil, userId, username, email, dataAdesao);

        } catch (JSONException e) {
            android.util.Log.e("UserJsonParser", "Erro ao parsear JSON: " + e.getMessage());
            e.printStackTrace();
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
