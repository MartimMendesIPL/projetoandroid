package pt.ipleiria.estg.dei.maislusitania_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//Import Listeners
import pt.ipleiria.estg.dei.maislusitania_android.database.DbContract;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.AvaliacaoListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.BilheteListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.EventoListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.FavoritoListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LoginListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.MapaListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.NoticiaListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.PerfilListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.ReservaListener;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.SignupListener;
//Import Parsers
import pt.ipleiria.estg.dei.maislusitania_android.utils.ReservasJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.EventosJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.LocalJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.MapaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.NoticiaJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UserJsonParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;
import pt.ipleiria.estg.dei.maislusitania_android.utils.FavoritoJsonParser;
//MQTT
import pt.ipleiria.estg.dei.maislusitania_android.utils.MqttHelper;

//SQLite
import pt.ipleiria.estg.dei.maislusitania_android.database.DbHelper;

public class SingletonLusitania {
    // Singleton Instance
    private static volatile SingletonLusitania instance;
    // declaracao das variaveis e constantes
    private ArrayList<Local> locais;
    private ArrayList<Favorito> favoritos;
    // DB
    private final LocaisFavDBHelper favoritosDbHelper;
    private DbHelper dbHelper;
    private static RequestQueue volleyQueue = null;
    private Context context;
    private String mainUrl;
    //Helper do perfil, sharedpreferences
    private ProfileManager profileManager;
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
    private static final String mUrlAPIFavoritos = "/favoritos";
    private static final String mUrladdFavorito = "/favoritos/add";
    private static final String mUrlAPIremoveFavorito = "/favoritos/remove";
    private static final String mUrlAPIMapa = "/mapas";
    private static final String mUrlAPIEvento = "/eventos";
    private static final String mUrlUser = "/user-profile";
    private static final String mUrlAPIReserva = "/reservas";
    private static final String mUrlAPIBilhetes = "/reservas/bilhetes";
    private static final String mUrlAPIAvaliacao = "/avaliacaos";
    // Listeners
    private LoginListener loginListener;
    private LocaisListener locaisListener;
    private MapaListener mapaListener;
    private NoticiaListener noticiaListener;
    private EventoListener eventoListener;
    private PerfilListener perfilListener;
    private FavoritoListener favoritoListener;
    private ReservaListener reservaListener;
    private BilheteListener bilheteListener;
    private AvaliacaoListener avaliacaoListener;
    private SignupListener signupListener;
    //region - Construtor e Instância
    private SingletonLusitania(Context context) {
        this.context = context.getApplicationContext();
        profileManager = new ProfileManager(this.context);

        locais = new ArrayList<>();
        favoritosDbHelper = new LocaisFavDBHelper(this.context);
        volleyQueue = Volley.newRequestQueue(this.context);
        this.dbHelper = new DbHelper(this.context);

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
    public void setMapaListener(MapaListener mapaListener) {
        this.mapaListener = mapaListener;
    }
    public void setLoginListener(LoginListener loginListener) { this.loginListener = loginListener; }
    public void setSignupListener(SignupListener signupListener) { this.signupListener = signupListener; }
    public void setLocaisListener(LocaisListener locaisListener) { this.locaisListener = locaisListener; }
    public void setNoticiaListener(NoticiaListener noticiaListener) { this.noticiaListener = noticiaListener; }
    public void setPerfilListener(PerfilListener perfilListener) { this.perfilListener = perfilListener; }
    public void setEventoListener(EventoListener eventoListener) { this.eventoListener = eventoListener; }
    public void setFavoritoListener(FavoritoListener favoritoListener) { this.favoritoListener = favoritoListener; }
    public void setBilheteListener(BilheteListener bilheteListener) { this.bilheteListener = bilheteListener; }
    public void setReservaListener(ReservaListener reservaListener) { this.reservaListener = reservaListener; }
    public void setAvaliacaoListener(AvaliacaoListener avaliacaoListener){ this.avaliacaoListener = avaliacaoListener;}
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
    public User getCachedUser(){
        return profileManager.getUser();
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
        int userid = getUserId(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        // Apagar dados do utilizador armazenados localmente, se existirem
        favoritosDbHelper.deleteAllFavoritosByUser(userid);
        profileManager.clearUserProfile();
    }
    private String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }
    public int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String userIdStr = prefs.getString(KEY_USER_ID, "-1");
        try {
            return Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            return -1; // Valor padrão se não for um número válido
        }
    }
    // metodo para criar um favorito a partir de um local
    private Favorito MakeFavoritoFromLocal(Local local, int utilizadorid) {
        Favorito favorito = new Favorito(
                local.getFavoritoId() + 1,
                utilizadorid,
                local.getId(),
                local.getImagem(),
                local.getNome(),
                local.getDistrito(),
                local.getAvaliacaoMedia(),
                UtilParser.getCurrentDateString(),
                true
        );
        return favorito;
    }
    //endregion

    //region - Helpers Volley

    /**
     * Helper para fazer pedidos que esperam um JSONArray.
     */
    private void makeJsonArrayRequest(Context context, int method, String endpoint, boolean requiresAuth,
                                      final Response.Listener<JSONArray> onSuccess,
                                      final Response.ErrorListener onError) {
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

        JsonArrayRequest req = new JsonArrayRequest(method, url, null, onSuccess, onError);
        volleyQueue.add(req);
    }

    /**
     * Helper para fazer pedidos que esperam um JSONObject.
     */
    private void makeJsonObjectRequest(Context context, int method, String endpoint, boolean requiresAuth,
                                       JSONObject jsonBody,
                                       final Response.Listener<JSONObject> onSuccess,
                                       final Response.ErrorListener onError) {
        String url = buildUrl(endpoint);
        if (requiresAuth) {
            String token = getAuthToken(context);
            if (token == null) {
                Toast.makeText(context, "Sessão expirada. Faça login novamente.", Toast.LENGTH_SHORT).show();
                return;
            }
            url += (url.contains("?") ? "&" : "?") + "access-token=" + token;
        }

        JsonObjectRequest req = new JsonObjectRequest(method, url, jsonBody, onSuccess, onError);
        volleyQueue.add(req);
    }
    //endregion

    //region - CRUD Local (Favoritos BD & API)
    // metodo para obter os favoritos da base de dados local
    public ArrayList<Favorito> getFavoritosBD() {
        return favoritosDbHelper.getAllFavoritos();
    }
    // metodo para adicionar um favorito à base de dados local
    public void addFavoritoBD(Favorito favorito) {
        favoritosDbHelper.adicionarFavorito(favorito);
    }
    // metodo para remover um favorito da base de dados local
    public void removeFavoritoBD(int id, int utilizadorid) {
        favoritosDbHelper.removerFavorito(id, utilizadorid);
    }
    // metodo para obter todos os favoritos via API
    public void getallFavoritosAPI(final Context context) {
        // Se estiver offline, carrega os favoritos da BD local
        if (!UtilParser.isConnectionInternet(context)) {
            favoritos = getFavoritosBD();
            // log no logcat
            for (Favorito fav : favoritos) {
                android.util.Log.i("FAVORITOS_OFFLINE", "Favorito carregado offline: " + fav.getLocalNome());

            }
            // Dispara o listener
            if (favoritoListener != null) favoritoListener.onFavoritosLoaded(favoritos);
            return;
        }
        // Se estiver online, carrega os favoritos via API
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIFavoritos, true,
                response -> {
                    try {
                        ArrayList<Favorito> favoritos = FavoritoJsonParser.parserJsonFavoritos(response);
                        if (favoritoListener != null) favoritoListener.onFavoritosLoaded(favoritos);
                    } catch (Exception e) {
                        if (favoritoListener != null)
                            favoritoListener.onFavoritosError("Erro JSON Favoritos," + e.getMessage());
                    }
                },
                error -> {
                    if (favoritoListener != null)
                        favoritoListener.onFavoritosError(error.getMessage());
                }
        );
    }
    // metodo para reinscrever nos tópicos MQTT dos favoritos
    public void resubscribeToFavoritos(final Context context) {
        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context,"Sem ligação à internet. Não é possível subscrever aos tópicos MQTT.", Toast.LENGTH_SHORT).show();
            android.util.Log.i("MQTT_SUBSCRIBE", "Sem ligação à internet. Não é possível subscrever aos tópicos MQTT.");
            return;
        }
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIFavoritos, true,
                response -> {
                    try {
                        ArrayList<Favorito> favoritos = FavoritoJsonParser.parserJsonFavoritos(response);
                        for (Favorito fav : favoritos) {
                            MqttHelper.getInstance().subscribe(fav.getLocalNome());
                            android.util.Log.i("MQTT_SUBSCRIBE", "Subscreveu ao tópico MQTT: " + fav.getLocalNome());
                        }
                    } catch (Exception e) {
                        android.util.Log.e("MQTT_SUBSCRIBE", "Erro ao reinscrever nos tópicos MQTT: " + e.getMessage());
                    }
                },
                error -> {
                    android.util.Log.e("MQTT_SUBSCRIBE", "Erro ao obter favoritos para reinscrição MQTT: " + error.getMessage());
                }
        );
    }
    // metodo para alternar o estado de favorito na view do local via API
    public void toggleLocalFavoritoAPI(final Context context, final Local local) {
        // Define Endpoint e Metodo baseado no estado atual
        String endpoint;
        int method;
        // se estiver favorito, remove, senao adiciona
        if (local.isFavorite()) {
            endpoint = mUrlAPIremoveFavorito + "/" + local.getId();
            method = Request.Method.DELETE;
            // Remover dos favoritos locais
            removeFavoritoBD(local.getId(), getUserId(context));
            // Desinscreve do canal MQTT associado ao local
            MqttHelper.getInstance().unsubscribe(local.getNome());
        } else {
            endpoint = mUrladdFavorito + "/" + local.getId();
            method = Request.Method.POST;
            // Adicionar aos favoritos locais
            Favorito fav = MakeFavoritoFromLocal(local, getUserId(context));
            addFavoritoBD(fav);
            // Inscreve no canal MQTT associado ao local
            MqttHelper.getInstance().subscribe(local.getNome());
        }
        // Usa o helper (requiresAuth = true)
        makeJsonObjectRequest(context, method, endpoint, true, null,
                response -> {
                    // Atualiza o estado do favorito no local
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
    // metodo para alternar o estado de favorito na view dos favoritos via API
    public void toggleFavoritoAPI(final Context context, final Favorito favorito) {
        // nao deixa continuar se estiver offline
        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem ligação à internet", Toast.LENGTH_SHORT).show();
            return;
        }
        // Define Endpoint e Metodo baseado no estado atual
        String endpoint;
        int method;
        // se estiver favorito, remove, senao adiciona
        if (favorito.isFavorite()) {
            endpoint = mUrlAPIremoveFavorito + "/" + favorito.getLocalId();
            method = Request.Method.DELETE;
            // Remover dos favoritos locais
            removeFavoritoBD(favorito.getLocalId(), getUserId(context));
        } else {
            endpoint = mUrladdFavorito + "/" + favorito.getLocalId();
            method = Request.Method.POST;
        }
        // Usa o helper (requiresAuth = true)
        makeJsonObjectRequest(context, method, endpoint, true, null,
                response -> {
                    // Atualiza o estado do favorito no local
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

    //region - Login API e Signup API
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
                        if (loginListener != null)
                            loginListener.onValidateLogin(token, user, user_id);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro no Login: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String mensagem = "Erro no Login";
                    // Tenta extrair mensagem especifica do erro
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject jsonError = new JSONObject(body);
                            if (jsonError.has("message")) mensagem = jsonError.getString("message");
                        } catch (Exception ignored) {
                        }
                    }
                    Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
                }
        );
    }
    // metodo para registar um novo utilizador via API
    public void signupAPI(final String username, final String email, final String password, final String primeiro_nome, final String ultimo_nome, final Context context) {
        JSONObject jsonBody = new JSONObject();
        try {
            // Preenche o corpo do pedido com os dados do utilizador
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("primeiro_nome", primeiro_nome);
            jsonBody.put("ultimo_nome", ultimo_nome);
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao criar pedido", Toast.LENGTH_SHORT).show();
            return;
        }
        // Signup não requer Auth Token na URL (requiresAuth = false)
        // faz o pedido
        makeJsonObjectRequest(context, Request.Method.POST, "/signup-form", false, jsonBody,
                response -> {
                    // Registo bem sucedido
                    if (signupListener != null)
                        signupListener.onSignupSuccess();
                },
                error -> {
                    String mensagem = "Erro no Registo";
                    // Tenta extrair mensagem especifica do erro
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                            JSONObject jsonError = new JSONObject(body);
                            if (jsonError.has("message")) mensagem = jsonError.getString("message");
                        } catch (Exception ignored) {
                        }
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

    //Retorna um local em especifico
    public void getLocalAPI(final int localId, final Context context) {
        makeJsonObjectRequest(context, Request.Method.GET, mUrlAPILocais + "/" + localId, true, null,
                response -> {
                    try {
                        // Adicionado .toString() porque o parser espera String e o response é JSONObject
                        Local local = LocalJsonParser.parserJsonLocalDetalhes(response.toString());
                        if (locaisListener != null) locaisListener.onLocalLoaded(local);
                    } catch (Exception e) {
                        if (locaisListener != null) locaisListener.onLocalError(e.getMessage());
                    }
                },
                error -> {
                    if (locaisListener != null) locaisListener.onLocalError(error.getMessage());
                }
        );
    }

    public void searchLocalAPI(final Context context, final String query) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPILocais + "/search/" + query, true,
                response -> {
                    try {
                        // The same parser can be used for search results
                        locais = LocalJsonParser.parserJsonLocais(response);
                        if (locaisListener != null) {
                            locaisListener.onLocaisLoaded(locais);
                        }
                    } catch (Exception e) {
                        if (locaisListener != null) {
                            locaisListener.onLocaisError("Erro JSON na pesquisa");
                        }
                    }
                },
                error -> {
                    if (locaisListener != null) {
                        locaisListener.onLocaisError(error.getMessage());
                    }
                }
        );
    }
    //endregion

    //region - Noticias API
    // metodo para obter todas as noticias via API
    public void getNoticiasAPI(final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPINoticias, true,
                response -> {
                    try {
                        // Usa o parser para converter o JSONArray em ArrayList<Noticia>
                        ArrayList<Noticia> noticias = NoticiaJsonParser.parserJsonNoticias(response);
                        // Dispara o listener com as notícias carregadas
                        if (noticiaListener != null) noticiaListener.onNoticiasLoaded(noticias);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro parser notícias", Toast.LENGTH_SHORT).show();
                    }
                },
                null // Usa erro padrão
        );
    }
    // metodo para obter uma unica noticia via API
    public void getNoticiaAPI(final int noticiaId, final Context context) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPINoticias + "/" + noticiaId, true,
                response -> {
                    try {
                        // Usa o parser para converter o JSONArray em Noticia
                        Noticia noticia = NoticiaJsonParser.parserJsonNoticia(response);
                        // Dispara o listener com a notícia carregada
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
    // metodo para pesquisar noticias via API
    public void searchNoticiaAPI(final Context context, final String query) {
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPINoticias + "/search/" + query, true,
                response -> {
                    try {
                        // Usa o parser para converter o JSONArray em ArrayList<Noticia>
                        ArrayList<Noticia> noticias = NoticiaJsonParser.parserJsonNoticias(response);
                        // Dispara o listener com as notícias carregadas
                        if (noticiaListener != null) noticiaListener.onNoticiasLoaded(noticias);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro JSON Pesquisa Noticias", Toast.LENGTH_SHORT).show();
                    }
                },
                null
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
                    User user = UserJsonParser.parserJsonUser(response);
                    profileManager.saveUser(user);
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

    public void editUserProfileAPI(final Context context, final String primeiro_nome,
                                   final String ultimo_mome, final String username) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("primeiro_nome", primeiro_nome);
            jsonBody.put("ultimo_nome", ultimo_mome);
            jsonBody.put("username", username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        makeJsonObjectRequest(context, Request.Method.PUT, mUrlUser + "/update-profile", true, jsonBody,
                response -> {
                    try {
                        getUserProfileAPI(context);

                        User updatedUser = getCachedUser();
                        updatedUser.setPrimeiro_nome(primeiro_nome);
                        updatedUser.setUltimo_nome(ultimo_mome);
                        updatedUser.setUsername(username);

                        profileManager.saveUser(updatedUser);
                        if(perfilListener != null){
                            perfilListener.onPerfilLoaded(updatedUser);
                        }

                        Toast.makeText(context, "Perfil editado com sucesso", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        if (perfilListener != null)
                            perfilListener.onPerfilError("Erro: " + e.getMessage());
                    }
                },
                error -> {
                    if (perfilListener != null) perfilListener.onPerfilError(error.getMessage());
                }
        );
    }

    public void changePasswordAPI(final Context context, final String password_atual, final String password) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("current_password", password_atual);
            jsonBody.put("new_password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        makeJsonObjectRequest(context, Request.Method.PUT, mUrlUser + "/change-password", true, jsonBody,
                response -> {
                    if (perfilListener != null) perfilListener.onPasswordChanged();
                },
                error -> {
                    if (perfilListener != null) perfilListener.onPerfilError(error.getMessage());
                }
        );
    }

    public void deleteUserAPI(final Context context) {
        makeJsonObjectRequest(context, Request.Method.DELETE, mUrlUser + "/delete-account", true, null,
                response -> {
                    if (perfilListener != null) perfilListener.onPerfilLogout();
                },
                error -> {
                    if (perfilListener != null)
                        perfilListener.onPerfilLogoutError(error.getMessage());
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

    //region - Reservas
    public void getAllReservasAPI(Context context) {
        // 1. Tenta carregar dados locais para uma UI rápida.
        if (reservaListener != null) {
            ArrayList<Reserva> reservasLocais = getAllReservasBD();
            reservaListener.onReservasLoaded(reservasLocais);
        }

        // 2. Se estiver offline, os dados locais já foram mostrados. Fim.
        if (!UtilParser.isConnectionInternet(context)) {
            Toast.makeText(context, "Sem internet. A mostrar dados guardados.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. MODO ONLINE: Busca as reservas na API.
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIReserva, true,
                response -> {
                    // Sucesso ao obter as reservas
                    ArrayList<Reserva> reservasDaAPI = ReservasJsonParser.parserJsonReservas(response);

                    // PASSO A - Sincroniza as Reservas na BD
                    sincronizarReservasBD(reservasDaAPI);

                    // PASSO B - Inicia a cascata para sincronizar os bilhetes de cada reserva
                    for (Reserva reserva : reservasDaAPI) {
                        // Chama o método dos bilhetes. O bilheteListener não é necessário aqui,
                        // pois o objetivo é apenas guardar os dados silenciosamente em segundo plano.
                        getAllBilhetesAPI(context, reserva.getId());
                    }

                    // Notifica a UI do ReservasFragment com os dados frescos
                    if (reservaListener != null) {
                        reservaListener.onReservasLoaded(reservasDaAPI);
                    }
                },
                error -> {
                    // Se a API de reservas falhar, a UI já tem os dados locais.
                    Toast.makeText(context, "Falha ao sincronizar reservas. A mostrar dados guardados.", Toast.LENGTH_SHORT).show();
                }
        );
    }

    private void sincronizarReservasBD(ArrayList<Reserva> reservas) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Limpa as reservas antigas para evitar duplicados e manter os dados frescos
            db.delete(DbContract.ReservaEntry.TABLE_NAME, null, null);

            for (Reserva reserva : reservas) {
                ContentValues values = new ContentValues();
                values.put(DbContract.ReservaEntry.COLUMN_ID, reserva.getId());
                values.put(DbContract.ReservaEntry.COLUMN_LOCAL_ID, reserva.getLocalId());
                values.put(DbContract.ReservaEntry.COLUMN_LOCAL_NOME, reserva.getLocalNome());
                values.put(DbContract.ReservaEntry.COLUMN_DATA_VISITA, reserva.getDataVisita());
                values.put(DbContract.ReservaEntry.COLUMN_PRECO_TOTAL, reserva.getPrecoTotal());
                values.put(DbContract.ReservaEntry.COLUMN_ESTADO, reserva.getEstado());
                values.put(DbContract.ReservaEntry.COLUMN_DATA_CRIACAO, reserva.getDataCriacao());
                values.put(DbContract.ReservaEntry.COLUMN_IMAGEM_LOCAL, reserva.getImagemLocal());
                db.insert(DbContract.ReservaEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private ArrayList<Reserva> getAllReservasBD() {
        ArrayList<Reserva> reservas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM " + DbContract.ReservaEntry.TABLE_NAME, null)) {
            if (cursor.moveToFirst()) {
                do {
                    reservas.add(new Reserva(
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_LOCAL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_LOCAL_NOME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_DATA_VISITA)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_PRECO_TOTAL)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_ESTADO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_DATA_CRIACAO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ReservaEntry.COLUMN_IMAGEM_LOCAL))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return reservas;
    }

    public void createReservaAPI(final Context context, int localId, String dataVisita, ArrayList<TipoBilhete> tiposBilhete) {
        try {
            JSONObject jsonBody = ReservasJsonParser.criarBodyReserva(localId, dataVisita, tiposBilhete);

            makeJsonObjectRequest(context, Request.Method.POST, mUrlAPIReserva, true, jsonBody,
                    response -> {
                        try {
                            int id = response.getInt("id");
                            String estado = response.getString("estado");
                            double precoTotal = response.getDouble("preco_total");
                            String dataCriacao = response.getString("data_criacao");

                            Reserva novaReserva = new Reserva(id, localId, "", dataVisita, precoTotal, estado, dataCriacao, "");

                            Toast.makeText(context, "Reserva criada com sucesso!", Toast.LENGTH_SHORT).show();

                            if (reservaListener != null) {
                                reservaListener.onReservaCreated(novaReserva);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context, "Erro ao processar resposta", Toast.LENGTH_SHORT).show();
                            if (reservaListener != null) {
                                reservaListener.onReservaError("Erro ao processar resposta: " + e.getMessage());
                            }
                        }
                    },
                    error -> {
                        String mensagemErro = "Erro ao criar reserva";
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            try {
                                String body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                                JSONObject jsonError = new JSONObject(body);
                                mensagemErro = jsonError.optString("message", mensagemErro);
                            } catch (Exception ignored) {}
                        }
                        Toast.makeText(context, mensagemErro, Toast.LENGTH_SHORT).show();
                        if (reservaListener != null) {
                            reservaListener.onReservaError(mensagemErro);
                        }
                    }
            );
        } catch (JSONException e) {
            Toast.makeText(context, "Erro ao criar pedido de reserva", Toast.LENGTH_SHORT).show();
            if (reservaListener != null) {
                reservaListener.onReservaError("Erro ao criar pedido: " + e.getMessage());
            }
        }
    }

    public void searchReservaAPI(final Context context, String query){
        makeJsonArrayRequest(context, Request.Method.GET, mUrlAPIReserva + "/search/" + query, true,
                response -> {
                    try{
                        ArrayList<Reserva> reservas = ReservasJsonParser.parserJsonReservas(response);
                        if (reservaListener != null) reservaListener.onReservasLoaded(reservas);
                    } catch (Exception e) {
                        Toast.makeText(context, "Erro JSON Reservas", Toast.LENGTH_SHORT).show();
                    }
                }, null);
    }
    //endregion

    //region - Bilhetes
    public void getAllBilhetesAPI(final Context context, int idReserva) {
        // A primeira ação é sempre ler da base de dados local.
        ArrayList<Bilhete> bilhetesLocais = getAllBilhetesBD(idReserva);

        // Se o bilheteListener está ativo (estamos no ViewBilhetesFragment),
        // notifica imediatamente a UI com os dados locais.
        if (bilheteListener != null) {
            bilheteListener.onBilhetesLoaded(bilhetesLocais);
        }

        // Se estamos offline, o trabalho está feito. A UI foi notificada.
        if (!UtilParser.isConnectionInternet(context)) {
            // Opcional: só mostra o Toast se estivermos a interagir com o ecrã de bilhetes
            if (bilheteListener != null) {
                Toast.makeText(context, "Sem internet. A mostrar bilhetes guardados.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // MODO ONLINE: Continua para sincronizar em segundo plano.
        // A URL que você confirmou estar correta.
        String url = mUrlAPIReserva + "/" + idReserva;
        makeJsonArrayRequest(context, Request.Method.GET, url, true,
                response -> {
                    // SUCESSO DA API
                    ArrayList<Bilhete> bilhetesDaAPI = ReservasJsonParser.parserJsonBilhetes(response);

                    // AGORA ISTO FUNCIONA SEMPRE, porque sincronizarReservasBD() já correu antes.
                    sincronizarBilhetesBD(idReserva, bilhetesDaAPI);

                    // Se estamos no ViewBilhetesFragment, atualiza a UI com os dados frescos da API.
                    if (bilheteListener != null) {
                        bilheteListener.onBilhetesLoaded(bilhetesDaAPI);
                    }
                },
                error -> {
                    // Se a API de bilhetes falhar, não fazemos nada. A UI (se existir) já tem os dados locais.
                    // Um Toast aqui poderia ser confuso para o utilizador, pois ele não iniciou esta ação diretamente.
                }
        );
    }

    private void sincronizarBilhetesBD(int reservaId, ArrayList<Bilhete> bilhetes) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Limpa os bilhetes antigos APENAS para esta reserva
            db.delete(DbContract.BilheteEntry.TABLE_NAME, DbContract.BilheteEntry.COLUMN_RESERVA_ID + " = ?", new String[]{String.valueOf(reservaId)});

            for (Bilhete bilhete : bilhetes) {
                ContentValues values = new ContentValues();
                values.put(DbContract.BilheteEntry.COLUMN_CODIGO, bilhete.getCodigo());
                values.put(DbContract.BilheteEntry.COLUMN_RESERVA_ID, bilhete.getReservaId());
                values.put(DbContract.BilheteEntry.COLUMN_LOCAL_ID, bilhete.getLocalId());
                values.put(DbContract.BilheteEntry.COLUMN_LOCAL_NOME, bilhete.getLocalNome());
                values.put(DbContract.BilheteEntry.COLUMN_DATA_VISITA, bilhete.getDataVisita());
                values.put(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_ID, bilhete.getTipoBilheteId());
                values.put(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_NOME, bilhete.getTipoBilheteNome());
                values.put(DbContract.BilheteEntry.COLUMN_PRECO, bilhete.getPreco());
                values.put(DbContract.BilheteEntry.COLUMN_ESTADO, bilhete.getEstado());
                db.insert(DbContract.BilheteEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private ArrayList<Bilhete> getAllBilhetesBD(int idReserva) {
        ArrayList<Bilhete> bilhetes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.query(DbContract.BilheteEntry.TABLE_NAME, null,
                DbContract.BilheteEntry.COLUMN_RESERVA_ID + " = ?",
                new String[]{String.valueOf(idReserva)}, null, null, null)) {

            if (cursor.moveToFirst()) {
                do {
                    bilhetes.add(new Bilhete(
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_CODIGO)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_RESERVA_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_LOCAL_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_LOCAL_NOME)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_DATA_VISITA)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_TIPO_BILHETE_NOME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_PRECO)),
                            cursor.getString(cursor.getColumnIndexOrThrow(DbContract.BilheteEntry.COLUMN_ESTADO))
                    ));
                } while (cursor.moveToNext());
            }
        }
        return bilhetes;
    }

    //endregion

//region - Avaliacoes

    public void addAvaliacao(final Context context, final int localId, final float rating, final String comentario) {
        String url = mUrlAPIAvaliacao + "/add/" + localId;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("classificacao", rating);
            jsonBody.put("comentario", comentario);
        } catch (JSONException e) {
            Toast.makeText(context, "Erro ao criar o pedido de avaliação", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        makeJsonObjectRequest(context, Request.Method.POST, url, true, jsonBody,
                response -> {
                    Toast.makeText(context, "Avaliação adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    getLocalAPI(localId, context);
                },
                error -> {
                    String mensagemErro = "Erro ao adicionar avaliação";
                    Toast.makeText(context, mensagemErro, Toast.LENGTH_SHORT).show();
                }
        );
    }

    public void editAvaliacao(final Context context, final int localId, final int avaliacaoId, final float rating, final String comentario) {
        String url = mUrlAPIAvaliacao + "/edit/" + avaliacaoId;

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("classificacao", rating);
            jsonBody.put("comentario", comentario);
        } catch (JSONException e) {
            Toast.makeText(context, "Erro ao criar o pedido de edição da avaliação", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        makeJsonObjectRequest(context, Request.Method.PUT, url, true, jsonBody,
                response -> {
                    Toast.makeText(context, "Avaliação editada com sucesso!", Toast.LENGTH_SHORT).show();
                    getLocalAPI(localId, context);
                },
                error -> {
                    String mensagemErro = "Erro ao editar avaliação";
                    Toast.makeText(context, mensagemErro, Toast.LENGTH_SHORT).show();
                }
        );
    }

    public void deleteAvaliacao(final Context context, final int localId, final int avaliacaoId) {
        String url = mUrlAPIAvaliacao + "/remove/" + avaliacaoId;

        makeJsonObjectRequest(context, Request.Method.DELETE, url, true, null,
                response -> {
                    Toast.makeText(context, "Avaliação removida com sucesso!", Toast.LENGTH_SHORT).show();
                    getLocalAPI(localId, context);
                },
                error -> {
                    String mensagemErro = "Erro ao remover avaliação";
                    Toast.makeText(context, mensagemErro, Toast.LENGTH_SHORT).show();
                }
        );
    }
//endregion
}