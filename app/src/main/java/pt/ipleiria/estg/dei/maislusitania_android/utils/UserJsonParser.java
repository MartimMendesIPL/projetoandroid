package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ipleiria.estg.dei.maislusitania_android.models.User;

public class UserJsonParser {

    public static User parserJsonUser(JSONArray response) {
        User auxuser = null;
        try {
            if (response.length() == 0) {
                return null;
            }

            JSONObject user = response.getJSONObject(0);

            int id = user.optInt("id", -1);
            String primeiroNome = user.optString("primeiro_nome", "Sem Nome");
            String ultimoNome = user.optString("ultimo_nome", "");
            String imagemPerfil = user.optString("imagem_perfil", "");

            int userId = user.optInt("user_id", -1);
            String username = user.optString("username", "");
            String email = user.optString("email", "");
            String dataAdesao = user.optString("data_adesao", "");

            auxuser = new User(id, primeiroNome, ultimoNome, imagemPerfil, userId, username, email, dataAdesao);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return auxuser;
    }

    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}