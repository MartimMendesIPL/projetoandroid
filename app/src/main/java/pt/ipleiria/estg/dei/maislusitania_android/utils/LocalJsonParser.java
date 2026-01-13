package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Avaliacao;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.TipoBilhete;

public class LocalJsonParser {

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
            int favorito_id = local.optInt("favorito_id", -1);


            auxlocal = new Local(id, nome, morada, distrito, descricao, imagem, avaliacaoMedia, favorito_id);
        }
        catch (JSONException e)
        {
            throw new RuntimeException(e);
        }
        return auxlocal;
    }

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
                    boolean favorito = local.optBoolean("favorito", false);
                    int favorito_id = local.optInt("favorito_id", -1);




                    Local auxlocal = new Local(id, nome, morada, distrito, descricao, imagem, avaliacaoMedia, favorito_id);
                    auxlocal.setFavorite(favorito);
                    locais.add(auxlocal);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            return locais;
        }

    public static Local parserJsonLocalDetalhes(String response) {
        Local local = null;
        try {
            JSONObject jsonObject = new JSONObject(response);

            // Informaões do local
            int id = jsonObject.getInt("id");
            float avaliacaoMedia = (float) jsonObject.getDouble("avaliacao_media");
            String nome = jsonObject.getString("nome");
            String morada = jsonObject.getString("morada");
            String distrito = jsonObject.getString("distrito");
            String descricao = jsonObject.getString("descricao");
            String imagem = jsonObject.getString("imagem");

            int favorito_id = jsonObject.optInt("favorito_id", -1);

            local = new Local(id, nome, morada, distrito, descricao, imagem, avaliacaoMedia, favorito_id);

            // Dados
            local.setTelefone(jsonObject.optString("contacto_telefone"));
            local.setEmail(jsonObject.optString("contacto_email"));
            local.setWebsite(jsonObject.optString("website"));

            // Horario
            JSONObject horarioJson = jsonObject.optJSONObject("horario");
            if (horarioJson != null) {
                // Podes guardar num HashMap<String, String> ou num objeto Horario
                // Exemplo simples passando para um Map na classe Local:
                local.addHorario("segunda", horarioJson.optString("segunda"));
                local.addHorario("terca", horarioJson.optString("terca"));
                local.addHorario("quarta", horarioJson.optString("quarta"));
                local.addHorario("quinta", horarioJson.optString("quinta"));
                local.addHorario("sexta", horarioJson.optString("sexta"));
                local.addHorario("sabado", horarioJson.optString("sabado"));
                local.addHorario("domingo", horarioJson.optString("domingo"));
            }

            // Avaliacoes
            JSONArray avaliacoesArray = jsonObject.optJSONArray("avaliacoes");
            if (avaliacoesArray != null) {
                ArrayList<Avaliacao> listaAvaliacoes = new ArrayList<>();
                for (int i = 0; i < avaliacoesArray.length(); i++) {
                    JSONObject avJson = avaliacoesArray.getJSONObject(i);
                    Avaliacao av = new Avaliacao(
                            avJson.getInt("id"),
                            avJson.getInt("local_id"),
                            avJson.getInt("utilizador_id"),
                            avJson.getString("utilizador"),
                            (float) avJson.getDouble("classificacao"),
                            avJson.getString("comentario"),
                            avJson.getString("data_avaliacao")
                    );
                    listaAvaliacoes.add(av);
                }
                local.setAvaliacoes(listaAvaliacoes);
            }

            // Bilhetes
            JSONArray bilhetesArray = jsonObject.optJSONArray("tipos-bilhete");
            if (bilhetesArray != null) {
                ArrayList<TipoBilhete> listaBilhetes = new ArrayList<>();
                for (int i = 0; i < bilhetesArray.length(); i++) {
                    JSONObject bilheteJson = bilhetesArray.getJSONObject(i);
                    TipoBilhete tb = new TipoBilhete(
                            bilheteJson.getInt("id"),
                            bilheteJson.getString("nome"),
                            bilheteJson.getString("descricao"),
                            bilheteJson.getString("preco")
                    );
                    listaBilhetes.add(tb);
                }
                local.setTiposBilhete(listaBilhetes);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return local;
    }
    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    
}