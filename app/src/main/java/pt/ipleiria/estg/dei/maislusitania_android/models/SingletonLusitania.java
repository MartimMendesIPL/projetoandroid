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

import pt.ipleiria.estg.dei.maislusitania_android.listeners.EventoListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LoginListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.MapaListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.NoticiaListener;
import pt.ipleiria.estg.dei.maislusitania_android.utils.EventosJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.LocalJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.MapaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.NoticiaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;

public class SingletonLusitania {

    private static volatile SingletonLusitania instance;
    private ArrayList<Local> locais;
    private ArrayList<Mapa> mapaLocais;
    private LocaisFavDBHelper dbHelper;
    private static RequestQueue volleyQueue = null;

    private static final String KEY_TOKEN = "auth_key";

    // URLs
    private static final String mUrlAPILogin = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/login-form";
    private static final String mUrlAPILocais = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/local-culturals";
    private static final String mUrlAPINoticias = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/noticias";
    private static final String mUrlAPIToggleFavorito = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/favoritos/toggle/";
    private static final String mUrlAPIMapa = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/mapas";
    private static final String mUrlAPIEvento = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/eventos";

    // SharedPreferences
    private static final String PREF_NAME = "MaisLusitaniaPrefs";
    private static final String KEY_USERNAME = "username";

    // Listeners
    private LoginListener loginListener;
    private LocaisListener locaisListener;
    private MapaListener mapaListener;
    private NoticiaListener noticiaListener;
    private EventoListener eventoListener;

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
    public void setMapaListener(MapaListener mapaListener) {
        this.mapaListener = mapaListener;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setLocaisListener(LocaisListener locaisListener) {
        this.locaisListener = locaisListener;
    }

    public void setNoticiaListener(NoticiaListener noticiaListener) {
        this.noticiaListener = noticiaListener;
    }

    public void setEventoListener(EventoListener eventoListener) {
        this.eventoListener = eventoListener;
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

    // Método para obter o token guardado
    private String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
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
    public void toggleFavoritoAPI(final Context context, final Local local) {
        if (!LocalJsonParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = getAuthToken(context);

        // VALIDAÇÃO: Verifica se o utilizador está autenticado
        if (token == null) {
            Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = mUrlAPIToggleFavorito + local.getId() + "?access-token=" + token;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Inverte o estado local
                            local.setFavorite(!local.isFavorite());

                            String mensagem = local.isFavorite() ? "Adicionado aos favoritos" : "Removido dos favoritos";
                            Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();

                            // Notifica a UI para atualizar
                            if (locaisListener != null)
                                locaisListener.onLocaisLoaded(locais);

                        } catch (Exception e) {
                            Toast.makeText(context, "Erro ao atualizar favorito", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Erro ao alterar favorito", Toast.LENGTH_SHORT).show();
                    }
                });
        volleyQueue.add(req);
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
    // CARREGAR LOCAIS COM FAVORITOS
    public void getAllLocaisAPI(final Context context) {
        if (!LocalJsonParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet", Toast.LENGTH_SHORT).show();

            if (locaisListener != null)
                locaisListener.onLocaisLoaded(new ArrayList<>());
        } else {
            String token = getAuthToken(context);

            // ✅ VALIDAÇÃO: Verifica se o utilizador está autenticado
            if (token == null) {
                Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
                if (locaisListener != null)
                    locaisListener.onLocaisError("Sem autenticação");
                return;
            }

            String urlComToken = mUrlAPILocais + "?access-token=" + token;

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, urlComToken, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                android.util.Log.d("API_RESPONSE", "Resposta: " + response.toString());

                                if (!response.has("data")) {
                                    android.util.Log.e("API_ERROR", "Campo 'data' não encontrado");
                                    if (locaisListener != null)
                                        locaisListener.onLocaisError("Resposta inválida");
                                    return;
                                }

                                JSONArray data = response.getJSONArray("data");
                                android.util.Log.d("API_DATA", "Total de locais: " + data.length());

                                ArrayList<Local> apiLocais = LocalJsonParser.parserJsonLocais(data);
                                locais = apiLocais;

                                if (locaisListener != null)
                                    locaisListener.onLocaisLoaded(locais);

                            } catch (Exception e) {
                                android.util.Log.e("API_PARSE_ERROR", "Erro: " + e.getMessage(), e);
                                if (locaisListener != null)
                                    locaisListener.onLocaisError("Erro ao processar dados");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String mensagem = "Erro ao carregar locais";

                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                try {
                                    String responseBody = new String(error.networkResponse.data, "UTF-8");
                                    JSONObject jsonError = new JSONObject(responseBody);
                                    if (jsonError.has("message")) {
                                        mensagem = jsonError.getString("message");
                                    }
                                } catch (Exception e) {
                                    // Mantém mensagem padrão
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

    //region Noticias API (GET, View)
    public void getNoticiasAPI(final Context context) {
        String token = getAuthToken(context);
        if (token == null){
            Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        String mUrlAPINoticiasAuth = mUrlAPINoticias + "?access-token=" + token;

        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem Ligação a internet", Toast.LENGTH_SHORT).show();
        }
        else {
            // CORREÇÃO: Usar JsonObjectRequest porque a resposta é { "data": [...] }
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, mUrlAPINoticiasAuth, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        // Extrair o array "data" do objeto JSON
                        JSONArray data = response.getJSONArray("data");
                        ArrayList<Noticia> noticias = NoticiaJsonParser.parserJsonNoticias(data);

                        if (noticiaListener != null)
                            noticiaListener.onNoticiasLoaded(noticias);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Erro ao processar dados das notícias", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.getMessage() != null ? error.getMessage() : "Erro ao carregar notícias";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    // log no logcat
                    android.util.Log.e("NoticiasAPI", "Erro ao carregar notícias: " + message);
                }
            });
            volleyQueue.add(req);
        }
    }

    public void getNoticiaAPI(final int noticiaId, final Context context) {
        String token = getAuthToken(context);
        if (token == null){
            Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String mUrlAPINoticiaAuth = mUrlAPINoticias + "/" + noticiaId + "?access-token=" + token;
        // Verificar ligação à internet
        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem Ligação a internet", Toast.LENGTH_SHORT).show();
        }
        else {
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, mUrlAPINoticiaAuth, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Noticia noticia = NoticiaJsonParser.parserJsonNoticia(response.toString());
                    if (noticiaListener != null)
                        noticiaListener.onNoticiaLoaded(noticia); // Notifica a atualização dos detalhes da notícia
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.getMessage() != null ? error.getMessage() : "Erro ao carregar detalhes";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
            volleyQueue.add(req);
        }
    }
    //endregion

    //region Mapas API(GetAll)

    public void getAllMapasAPI(final Context context) {
        // Verificar ligação à internet
        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem Ligação a internet", Toast.LENGTH_SHORT).show();
        } else {
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, mUrlAPIMapa, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray data = response.getJSONArray("data");

                                ArrayList<Mapa> mapaLocais = MapaJsonParser.parserJsonMapaLocais(data);

                                if (mapaListener != null) {
                                    mapaListener.onMapaLoaded(mapaLocais);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Erro ao processar JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro API: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            volleyQueue.add(req);
        }
    }

    //endregion

    //region Eventos API(GET, View)
    public void getAllEventosAPI(final Context context) {
        // Verificar ligação à internet
        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem Ligação a internet", Toast.LENGTH_SHORT).show();
        } else {
            String token = getAuthToken(context);

            if (token == null){
                Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            String mUrlAPIEventosAuth = mUrlAPIEvento + "?access-token=" + token;
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, mUrlAPIEventosAuth, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray data = response.getJSONArray("data");

                                ArrayList<Evento> eventos = EventosJsonParser.parserJsonEventos(data);

                                if (eventoListener != null) {
                                    eventoListener.onEventosLoaded(eventos);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Erro ao processar JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "Erro API: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            volleyQueue.add(req);
        }
    }

    //endregion
}