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

import pt.ipleiria.estg.dei.maislusitania_android.listeners.BilheteListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.EventoListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.FavoritoListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LoginListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.MapaListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.NoticiaListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.PerfilListener;
import pt.ipleiria.estg.dei.maislusitania_android.utils.BilhetesJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.EventosJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.LocalJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.MapaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.NoticiaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UserJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.FavoritoJsonParser;

public class SingletonLusitania {

    private static volatile SingletonLusitania instance;
    private ArrayList<Local> locais;
    private ArrayList<Favorito> favoritos;
    private final LocaisFavDBHelper dbHelper;
    private static RequestQueue volleyQueue = null;

    private Context context;
    private String mainUrl;

    // Keys
    private static final String KEY_TOKEN = "auth_key";
    private static final String PREF_NAME = "MaisLusitaniaPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_MAIN_URL = "main_url";

    private static final String KEY_USER_ID = "user_id";

    // Default URL
    private static final String DEFAULT_MAIN_URL = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/";

    // Endpoints
    private static final String mUrlAPILogin = "/login-form";
    private static final String mUrlAPILocais = "/local-culturals";
    private static final String mUrlAPINoticias = "/noticias";
    private static final String mUrladdFavorito = "/favoritos/add";
    private static final String mUrlAPIremoveFavorito = "/favoritos/remove";
    private static final String mUrlAPIMapa = "/mapas";
    private static final String mUrlAPIEvento = "/eventos";
    private static final String mUrlUser = "/user-profile";

    private static final String mUrlAPIBilhete = "/reservas/bilhetes";


    // Listeners
    private LoginListener loginListener;
    private LocaisListener locaisListener;
    private MapaListener mapaListener;
    private NoticiaListener noticiaListener;
    private EventoListener eventoListener;
    private PerfilListener perfilListener;
    private FavoritoListener favoritoListener;

    private BilheteListener bilheteListener;

    //region - Construtor e Instância
    private SingletonLusitania(Context context) {

        //FIx em um memory leak que o IDE estava a avisar!!!
        this.context = context.getApplicationContext();


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

    // Setters Listeners
    public void setMapaListener(MapaListener mapaListener) { this.mapaListener = mapaListener; }
    public void setLoginListener(LoginListener loginListener) { this.loginListener = loginListener; }
    public void setLocaisListener(LocaisListener locaisListener) { this.locaisListener = locaisListener; }
    public void setNoticiaListener(NoticiaListener noticiaListener) { this.noticiaListener = noticiaListener; }
    public void setPerfilListener(PerfilListener perfilListener) { this.perfilListener = perfilListener; }
    public void setEventoListener(EventoListener eventoListener) { this.eventoListener = eventoListener; }
    public void setFavoritoListener(FavoritoListener favoritoListener) { this.favoritoListener = favoritoListener; }
    public void setBilheteListener(BilheteListener BilheteListener) { this.bilheteListener = BilheteListener; }
    private BilheteListener bilhetesListener;



    //endregion

    // region Gestão da URL e Sessão
    public void setMainUrl(String url) {
        this.mainUrl = url;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_MAIN_URL, url).apply();
    }

    public String buildUrl(String endpoint) {
        String base = this.mainUrl;
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        if (!endpoint.startsWith("/")) endpoint = "/" + endpoint;
        return base + endpoint;
    }

    public void guardarUtilizador(Context context, String username, String token, String user_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_ID, user_id);
        editor.apply();
    }

    public boolean isUtilizadorLogado(Context context) {
        return getAuthToken(context) != null;
    }

    public void logout(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    private String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    //endregion

    //region - Helpers Volley

    /**
     * Helper para fazer pedidos que esperam um JSONArray.
     */
    private void makeJsonArrayRequest(Context context, int method, String endpoint, boolean requiresAuth,
                                      final Response.Listener<JSONArray> onSuccess,
                                      final Response.ErrorListener onError) {

        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = buildUrl(endpoint);
        if (requiresAuth) {
            String token = getAuthToken(context);
            if (token == null) {
                Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Adiciona token (verifica se já existem parametros na query)
            url += (url.contains("?") ? "&" : "?") + "access-token=" + token;
        }

        JsonArrayRequest req = new JsonArrayRequest(method, url, null,
                onSuccess,
                error -> {
                    if (onError != null) onError.onErrorResponse(error);
                    else handleDefaultError(context, error);
                });
        volleyQueue.add(req);
    }

    /**
     * Helper para fazer pedidos que esperam um JSONObject.
     */

    //TODO: Fazer a API retornar sempre um JSONArray para usar apenas um helper e simplificar o Singleton!!!!
    private void makeJsonObjectRequest(Context context, int method, String endpoint, boolean requiresAuth,
                                       JSONObject jsonBody,
                                       final Response.Listener<JSONObject> onSuccess,
                                       final Response.ErrorListener onError) {

        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = buildUrl(endpoint);
        if (requiresAuth) {
            String token = getAuthToken(context);
            if (token == null) {
                Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
                return;
            }
            url += (url.contains("?") ? "&" : "?") + "access-token=" + token;
        }

        JsonObjectRequest req = new JsonObjectRequest(method, url, jsonBody,
                onSuccess,
                error -> {
                    if (onError != null) onError.onErrorResponse(error);
                    else handleDefaultError(context, error);
                });
        volleyQueue.add(req);
    }
    //Tratar dos erros
    private void handleDefaultError(Context context, VolleyError error) {
        String msg = "Erro na comunicação com o servidor";
        if (error.getMessage() != null) msg = error.getMessage();
        android.util.Log.e("API_ERROR", msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //endregion

    //region - CRUD Local (Favoritos BD & API)
    public ArrayList<Local> getFavoritosBD() {
        return dbHelper.getAllFavoritos();
    }

    public void addFavoritoBD(Local local) {
        dbHelper.adicionarFavorito(local);
    }

    public void removeFavoritoBD(int id) {
        dbHelper.removerFavorito(id);
    }

    public void getallFavoritosAPI(final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, "/favoritos", true,
                response -> {
                    try {
                        ArrayList<Favorito> favoritos = FavoritoJsonParser.parserJsonFavoritos(response);
                        if (favoritoListener != null) favoritoListener.onFavoritosLoaded(favoritos);
                    } catch (Exception e) {
                        if (favoritoListener != null) favoritoListener.onFavoritosError("Erro JSON Favoritos");
                    }
                },
                error -> {
                    if (favoritoListener != null) favoritoListener.onFavoritosError(error.getMessage());
                }
        );
    }

    public void toggleFavoritoAPI(final Context context, final Local local) { // para os locais
        // Define Endpoint e Metodo baseado no estado atual
        String endpoint;
        int method;

        if (local.isFavorite()) {
            endpoint = mUrlAPIremoveFavorito + "/" + local.getId();
            method = Request.Method.DELETE;
        } else {
            endpoint = mUrladdFavorito + "/" + local.getId();
            method = Request.Method.POST;
        }

        // Usa o helper (requiresAuth = true)
        makeJsonObjectRequest(context, method, endpoint, true, null,
                response -> {
                    local.setFavorite(!local.isFavorite());
                    String mensagem = local.isFavorite() ? "Adicionado aos favoritos" : "Removido dos favoritos";
                    Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();

                    if (locaisListener != null) {
                        locaisListener.onLocaisLoaded(locais);
                    }
                },
                error -> Toast.makeText(context, "Erro ao alterar favorito", Toast.LENGTH_SHORT).show()
        );
    }
    public void toggleFavoritoAPI(final Context context, final Favorito favorito) { // para os favoritos (quando fiz esse codigo só eu e deus sabiamos, agora só deus sabe)
        // Define Endpoint e Metodo baseado no estado atual
        String endpoint;
        int method;

        if (favorito.isFavorite()) {
            endpoint = mUrlAPIremoveFavorito + "/" + favorito.getLocalId();
            method = Request.Method.DELETE;
        } else {
            endpoint = mUrladdFavorito + "/" + favorito.getLocalId();
            method = Request.Method.POST;
        }

        // Usa o helper (requiresAuth = true)
        makeJsonObjectRequest(context, method, endpoint, true, null,
                response -> {
                    favorito.setFavorite(!favorito.isFavorite());
                    String mensagem = favorito.isFavorite() ? "Adicionado aos favoritos" : "Removido dos favoritos";
                    Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
                    // Atualiza a lista de favoritos
                    SingletonLusitania.getInstance(context).getallFavoritosAPI(context);
                },
                error -> Toast.makeText(context, "Erro ao alterar favorito", Toast.LENGTH_SHORT).show()
        );
    }
    //endregion

    //region - Login API
    public void loginAPI(final String username, final String password, final Context context) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao criar pedido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login não requer Auth Token na URL (requiresAuth = false)
        makeJsonObjectRequest(context, Request.Method.POST, mUrlAPILogin, false, jsonBody,
                response -> {
                    try {
                        String token = response.getString("auth_key");
                        String user = response.getString("username");
                        String user_id = response.getString("user_id");
                        guardarUtilizador(context, user, token, user_id);
                        if (loginListener != null) loginListener.onValidateLogin(token, user, user_id);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro no Login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String mensagem = "Erro no Login";
                    // Tenta extrair mensagem especifica do erro
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, "UTF-8");
                            JSONObject jsonError = new JSONObject(body);
                            if (jsonError.has("message")) mensagem = jsonError.getString("message");
                        } catch (Exception ignored) {}
                    }
                    Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
                }
        );
    }
    //endregion

    //region - Locais API
    public void getAllLocaisAPI(final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPILocais, true,
                response -> {
                    try {
                        locais = LocalJsonParser.parserJsonLocais(response);
                        if (locaisListener != null) locaisListener.onLocaisLoaded(locais);
                    } catch (Exception e) {
                        if (locaisListener != null) locaisListener.onLocaisError("Erro JSON");
                    }
                },
                error -> {
                    if (locaisListener != null) locaisListener.onLocaisError(error.getMessage());
                }
        );
    }
    //endregion

    //region - Noticias API
    public void getNoticiasAPI(final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPINoticias, true,
                response -> {
                    try {
                        ArrayList<Noticia> noticias = NoticiaJsonParser.parserJsonNoticias(response);
                        if (noticiaListener != null) noticiaListener.onNoticiasLoaded(noticias);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro parser notícias", Toast.LENGTH_SHORT).show();
                    }
                },
                null // Usa erro padrão
        );
    }

    public void getNoticiaAPI(final int noticiaId, final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPINoticias + "/" + noticiaId, true,
                response -> {
                    try {
                        Noticia noticia = NoticiaJsonParser.parserJsonNoticia(response);
                        if (noticiaListener != null) noticiaListener.onNoticiaLoaded(noticia);
                    } catch (Exception e) {
                        if (noticiaListener != null) noticiaListener.onNoticiaError(e.getMessage());
                    }
                },
                error -> {
                    if (noticiaListener != null) noticiaListener.onNoticiaError(error.getMessage());
                }
        );
    }
    //endregion

    //region - Mapas API
    public void getAllMapasAPI(final Context context) {
        // Mapas não requer token (requiresAuth = false)
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIMapa, false,
                response -> {
                    try {
                        ArrayList<Mapa> lista = MapaJsonParser.parserJsonMapaLocais(response);
                        if (mapaListener != null) mapaListener.onMapaLoaded(lista);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro JSON Mapa", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (mapaListener != null) mapaListener.onMapaError(error.getMessage());
                    else Toast.makeText(context, "Erro Mapa: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
    }

    public void searchMapaAPI(final Context context, final String query) {
        // Mapas não requer token (requiresAuth = false)
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIMapa + "/search/" + query, false,
                response -> {
                    try {
                        ArrayList<Mapa> lista = MapaJsonParser.parserJsonMapaLocais(response);
                        if (mapaListener != null) mapaListener.onMapaLoaded(lista);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro JSON Pesquisa", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Erro pesquisa: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
    //endregion

    //region - User Profile
    public void getUserProfileAPI(final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlUser + "/me", true,
                response -> {
                    User user = UserJsonParser.parserJsonUser(response.toString());
                    if (user != null && perfilListener != null) {
                        perfilListener.onPerfilLoaded(user);
                    } else if (perfilListener != null) {
                        perfilListener.onPerfilError("Erro ao processar utilizador");
                    }
                },
                error -> {
                    if (perfilListener != null) perfilListener.onPerfilError(error.getMessage());
                }
        );
    }
    //endregion

    //region - Eventos API
    public void getAllEventosAPI(final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIEvento, true,
                response -> {
                    try {
                        ArrayList<Evento> eventos = EventosJsonParser.parserJsonEventos(response);
                        if (eventoListener != null) eventoListener.onEventosLoaded(eventos);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro JSON Eventos", Toast.LENGTH_SHORT).show();
                    }
                },
                null
        );
    }

    public void getEventoAPI(final int eventoId, final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIEvento + "/" + eventoId, true,
                response -> {
                    try {
                        Evento evento = EventosJsonParser.parserJsonEvento(response);
                        if (eventoListener != null) eventoListener.onEventoLoaded(evento);
                    } catch (Exception e) {
                        if (eventoListener != null) eventoListener.onEventoError(e.getMessage());
                    }
                },
                error -> {
                    if (eventoListener != null) eventoListener.onEventoError(error.getMessage());
                }
        );
    }

    public void searchEventoAPI(final Context context, final String query) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIEvento + "/search/" + query, true,
                response -> {
                    try {
                        ArrayList<Evento> eventos = EventosJsonParser.parserJsonEventos(response);
                        if (eventoListener != null) eventoListener.onEventosLoaded(eventos);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro JSON Pesquisa Eventos", Toast.LENGTH_SHORT).show();
                    }
                },
                null
        );
    }
    //endregion

    //region - Bilhetes API
    public void getAllBilhetesAPI(final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIBilhete, true,
                response -> {
                    try {
                        ArrayList<Bilhete> bilhetes = BilhetesJsonParser.parserJsonBilhetes(response);
                        if (bilheteListener != null) bilheteListener.onBilhetesLoaded(bilhetes);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro JSON Bilhetes", Toast.LENGTH_SHORT).show();
                    }
                },
                null
        );
    }
    //endregion
}