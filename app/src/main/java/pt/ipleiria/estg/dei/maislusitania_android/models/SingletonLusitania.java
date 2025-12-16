package pt.ipleiria.estg.dei.maislusitania_android.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LoginListener;
import pt.ipleiria.estg.dei.maislusitania_android.utils.LocalJsonParser;

public class SingletonLusitania {

    private static volatile SingletonLusitania instance;
    private ArrayList<Local> locais;
    private LocaisFavDBHelper dbHelper;
    private static RequestQueue volleyQueue = null;

    // URLs
    private static final String mUrlAPILogin = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/login-form";
    private static final String mUrlAPILocais = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/local-culturals";

    // SharedPreferences
    private static final String PREF_NAME = "MaisLusitaniaPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "auth_key";

    private LoginListener loginListener;
    private LocaisListener locaisListener;

    //region - Construtor e Instância
    private SingletonLusitania(Context context) {
        locais = new ArrayList<>();
        dbHelper = new LocaisFavDBHelper(context);
        volleyQueue = Volley.newRequestQueue(context);
    }

    public static synchronized SingletonLusitania getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonLusitania(context.getApplicationContext());
        }
        return instance;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setLocaisListener(LocaisListener locaisListener) {
        this.locaisListener = locaisListener;
    }
    //endregion

    //region - SharedPreferences (Sessão)
    // Mantive esta parte igual porque a professora não tinha Login no exemplo,
    // mas a lógica é de armazenamento local simples.
    public void guardarUtilizador(Context context, String username, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public boolean isUtilizadorLogado(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null) != null;
    }

    public void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }



    //endregion

    //region - CRUD Local (Favoritos)
    // A professora expõe métodos diretos para a BD aqui
    public ArrayList<Local> getFavoritosBD() {
        return dbHelper.getAllFavoritos();
    }

    public void addFavoritoBD(Local local) {
        dbHelper.adicionarFavorito(local);
    }

    public void removeFavoritoBD(int id) {
        dbHelper.removerFavorito(id);
    }

    // Método auxiliar para atualizar o estado na lista em memória
    public void toggleFavorito(Local local) {
        if (local.isFavorite()) {
            removeFavoritoBD(local.getId());
            local.setFavorite(false);
        } else {
            addFavoritoBD(local);
            local.setFavorite(true);
        }
    }

    // LÓGICA DO LOGIN (POST)
    public void loginAPI(final String username, final String password, final Context context) {
        if (!LocalJsonParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet.", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("username", username);
                jsonBody.put("password", password);
            } catch (Exception e) {
                Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show();
            }

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, mUrlAPILogin, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token = response.getString("auth_key");
                                String user = response.getString("username");

                                guardarUtilizador(context, user, token);

                                if (loginListener != null)
                                    loginListener.onValidateLogin(token, user);

                            } catch (Exception e) {
                                Toast.makeText(context, "Erro no Login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String mensagem = "Erro no Login";

                            // Extrai a mensagem do corpo da resposta da API
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject jsonError = new JSONObject(responseBody);

                                    // Tenta obter a mensagem (ajusta a chave conforme a tua API)
                                    if (jsonError.has("message")) {
                                        mensagem = jsonError.getString("message");
                                    } else if (jsonError.has("error")) {
                                        mensagem = jsonError.getString("error");
                                    }
                                } catch (Exception e) {
                                    mensagem = "Erro no Login: " + error.getMessage();
                                }
                            }

                            Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
                        }
                    });
            volleyQueue.add(req);
        }
    }



    // LÓGICA DOS LOCAIS (Equivalente ao getAllBooksAPI - GET)
    public void getAllLocaisAPI(final Context context) {
        if (!LocalJsonParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet", Toast.LENGTH_SHORT).show();

            ArrayList<Local> favoritos = getFavoritosBD();
            if (locaisListener != null)
                locaisListener.onLocaisLoaded(favoritos);
        } else {
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, mUrlAPILocais, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Log da resposta completa
                                android.util.Log.d("API_RESPONSE", "Resposta completa: " + response.toString());

                                // Verifica se tem o campo "data"
                                if (!response.has("data")) {
                                    android.util.Log.e("API_ERROR", "Campo 'data' não encontrado na resposta");
                                    if (locaisListener != null)
                                        locaisListener.onLocaisError("Resposta da API inválida");
                                    return;
                                }

                                JSONArray data = response.getJSONArray("data");
                                android.util.Log.d("API_DATA", "Total de locais: " + data.length());

                                ArrayList<Local> apiLocais = LocalJsonParser.parserJsonLocais(data);

                                for (Local local : apiLocais) {
                                    if (dbHelper.isFavorito(local.getId())) {
                                        local.setFavorite(true);
                                    }
                                }

                                locais = apiLocais;

                                if (locaisListener != null)
                                    locaisListener.onLocaisLoaded(locais);

                            } catch (Exception e) {
                                android.util.Log.e("API_PARSE_ERROR", "Erro detalhado: " + e.getMessage(), e);
                                if (locaisListener != null)
                                    locaisListener.onLocaisError("Erro ao processar dados: " + e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String mensagem = "Erro ao carregar locais";

                            android.util.Log.e("API_ERROR", "Erro no pedido", error);

                            if (error.networkResponse != null) {
                                android.util.Log.e("API_ERROR", "Código HTTP: " + error.networkResponse.statusCode);

                                if (error.networkResponse.data != null) {
                                    try {
                                        String responseBody = new String(error.networkResponse.data, "UTF-8");
                                        android.util.Log.e("API_ERROR", "Resposta de erro: " + responseBody);

                                        JSONObject jsonError = new JSONObject(responseBody);
                                        if (jsonError.has("message")) {
                                            mensagem = jsonError.getString("message");
                                        }
                                    } catch (Exception e) {
                                        android.util.Log.e("API_ERROR", "Erro ao ler resposta de erro", e);
                                    }
                                }
                            }

                            if (locaisListener != null)
                                locaisListener.onLocaisError(mensagem);
                        }
                    });
            volleyQueue.add(req);
        }
    }


    //endregion
}