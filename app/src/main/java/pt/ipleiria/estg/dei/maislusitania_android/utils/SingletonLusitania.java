// java
package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
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

import pt.ipleiria.estg.dei.maislusitania_android.LocaisFragment;

public class SingletonLusitania {
    private static volatile SingletonLusitania instance;
    private LoginListener loginListener;
    private LocaisListener locaisListener;
    private static RequestQueue volleyQueue = null;
    private static Context appContext;

    private String mUrlAPILogin = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/login-form";
    private String mUrlAPILocais = "http://172.22.21.218/projetopsi/maislusitania/backend/web/api/local-culturals";

    private SingletonLusitania(Context context) {
        appContext = context.getApplicationContext();
        volleyQueue = Volley.newRequestQueue(appContext);
    }

    public static synchronized SingletonLusitania getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonLusitania(context);
        }
        return instance;
    }

    public void setLoginListener(LoginListener loginListener) {
        this.loginListener = loginListener;
    }

    public void setLocaisListener(LocaisListener locaisListener) {
        this.locaisListener = locaisListener;
    }

    public void loginAPI(final String username, final String password) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                mUrlAPILogin,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // A API retorna: username, user_id, auth_key
                            if (response.has("auth_key")) {
                                String authKey = response.getString("auth_key");
                                String userName = response.getString("username");
                                int userId = response.getInt("user_id");

                                if (loginListener != null) {
                                    loginListener.onValidateLogin(authKey, userName);
                                }
                            } else {
                                Toast.makeText(appContext, "Credenciais inválidas", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(appContext, "Erro ao processar resposta", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Erro na ligação";

                        if (error.networkResponse != null) {
                            switch (error.networkResponse.statusCode) {
                                case 400:
                                    errorMessage = "Username ou password vazios";
                                    break;
                                case 404:
                                    errorMessage = "Utilizador não encontrado";
                                    break;
                                case 401:
                                    errorMessage = "Username ou Palavra-passe incorretos";
                                    break;
                            }
                        }

                        Toast.makeText(appContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });

        volleyQueue.add(request);
    }


    public void getLocaisAPI() {
        if (volleyQueue == null) {
            volleyQueue = Volley.newRequestQueue(appContext);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                mUrlAPILocais,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("data")) {
                                JSONArray locais = response.getJSONArray("data");
                                if (locaisListener != null) {
                                    locaisListener.onLocaisLoaded(locais);
                                }
                            } else {
                                if (locaisListener != null) {
                                    locaisListener.onLocaisError("Estrutura de resposta inválida");
                                }
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
                        String errorMessage = "Erro a obter locais";
                        if (error.networkResponse != null) {
                            try {
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                errorMessage = jsonObject.optString("message", errorMessage);
                            } catch (Exception e) {
                                // manter mensagem genérica
                            }
                        }
                        if (locaisListener != null) {
                            locaisListener.onLocaisError(errorMessage);
                        } else {
                            Toast.makeText(appContext, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        volleyQueue.add(request);
    }


    public interface LoginListener {
        void onValidateLogin(String accessToken, String username);
    }

    public interface LocaisListener {
        void onLocaisLoaded(JSONArray locais);
        void onLocaisError(String message);
    }


}
