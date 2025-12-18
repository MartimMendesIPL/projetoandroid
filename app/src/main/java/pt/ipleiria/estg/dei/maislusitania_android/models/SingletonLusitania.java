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
import pt.ipleiria.estg.dei.maislusitania_android.listeners.PerfilListener;
import pt.ipleiria.estg.dei.maislusitania_android.utils.EventosJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.LocalJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.MapaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.NoticiaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UserJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;

public class SingletonLusitania {

    private static volatile SingletonLusitania instance;
    private ArrayList<Local> locais;
    private ArrayList<Mapa> mapaLocais;
    private LocaisFavDBHelper dbHelper;
    private static RequestQueue volleyQueue = null;

    private Context context;
    private String mainUrl;

    private static final String KEY_TOKEN = "auth_key";

    // URLs
    private static final String mUrlAPILogin = "/login-form";
    private static final String mUrlAPILocais = "/local-culturals";
    private static final String mUrlAPINoticias = "/noticias";
    private static final String mUrlAPIToggleFavorito = "/toggle/";
    private static final String mUrlAPIMapa = "/mapas";
    private static final String mUrlAPIEvento = "/eventos";

    private static final String mUrlUser = "/user-profile";

    // SharedPreferences
    private static final String PREF_NAME = "MaisLusitaniaPrefs";
    private static final String KEY_USERNAME = "username";

    private static final String KEY_MAIN_URL = "main_url";
    private static final String DEFAULT_MAIN_URL = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/";

    // Listeners
    private LoginListener loginListener;
    private LocaisListener locaisListener;
    private MapaListener mapaListener;
    private NoticiaListener noticiaListener;
    private EventoListener eventoListener;

    private PerfilListener perfilListener;

    //region - Construtor e Instância
    private SingletonLusitania(Context context) {
        locais = new ArrayList<>();
        dbHelper = new LocaisFavDBHelper(context);
        volleyQueue = Volley.newRequestQueue(context);
        this.context = context;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mainUrl = prefs.getString(KEY_MAIN_URL, DEFAULT_MAIN_URL);
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

    public void setPerfilListener(PerfilListener perfilListener) {
        this.perfilListener = perfilListener;
    }
    public void setEventoListener(EventoListener eventoListener) {
        this.eventoListener = eventoListener;
    }

    //endregion

    // region Gestão da URL da API

    //guarda o novo url no SharedPreferences
    public void setMainUrl(String url) {
        this.mainUrl = url;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_MAIN_URL, url).apply();
    }

    //construtor simples de endpoints
    public String buildUrl(String endpoint) {
        String base = this.mainUrl;

        // Remove barra final da base se existir
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }

        // Adiciona barra inicial ao endpoint se não tiver
        if (!endpoint.startsWith("/")) {
            endpoint = "/" + endpoint;
        }

        return base + endpoint;
    }
    //endregion

    //region - SharedPreferences (Sessão)
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

            String url = buildUrl(mUrlAPILogin);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
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

    // LÓGICA DOS LOCAIS
    public void getAllLocaisAPI(final Context context) {
        // 1. Check Internet
        if (!LocalJsonParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            if (locaisListener != null) {
                locaisListener.onLocaisLoaded(new ArrayList<>());
            }
            return;
        }

        // 2. Check Token
        String token = getAuthToken(context);
        if (token == null) {
            Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            if (locaisListener != null) {
                locaisListener.onLocaisError("Sem autenticação");
            }
            return;
        }

        // 3. Prepare Request
        String url = mUrlAPILocais + "?access-token=" + token;

        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            locais = LocalJsonParser.parserJsonLocais(response);

                            // Update UI
                            if (locaisListener != null) {
                                locaisListener.onLocaisLoaded(locais);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (locaisListener != null) {
                                locaisListener.onLocaisError("Erro ao processar dados");
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = error.getMessage() != null ? error.getMessage() : "Erro ao carregar locais";
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        // log no logcat
                        android.util.Log.e("LocaisAPI", "Erro ao carregar locais: " + message);
                    }
                });

        volleyQueue.add(req);
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
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, mUrlAPINoticiasAuth, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        ArrayList<Noticia> noticias = NoticiaJsonParser.parserJsonNoticias(response);

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
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, mUrlAPINoticiaAuth, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
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

            String url = buildUrl(mUrlAPIMapa);

            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                ArrayList<Mapa> mapaLocais = MapaJsonParser.parserJsonMapaLocais(response);

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

    //region - Get User Profile

    public void getUserProfileAPI(final Context context) {

        String token = getAuthToken(context);
        if (token == null){
            Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }

        String mUrlUserAuth = mUrlUser + "/me?access-token=" + token;
        // Verificar ligação à internet
        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem Ligação a internet", Toast.LENGTH_SHORT).show();
        }
        else {
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, mUrlUserAuth, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    User user = UserJsonParser.parserJsonUser(response.toString());
                    if (perfilListener != null)
                        perfilListener.onPerfilLoaded(user); // Notifica a atualização o utilizador
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String message = error.getMessage() != null ? error.getMessage() : "Erro ao carregar utilizador";
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
            volleyQueue.add(req); // FIXED: Added volleyQueue.add
        } // FIXED: Closed else block
    } // FIXED: Closed method block
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
            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, mUrlAPIEventosAuth, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                ArrayList<Evento> eventos = EventosJsonParser.parserJsonEventos(response);

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
