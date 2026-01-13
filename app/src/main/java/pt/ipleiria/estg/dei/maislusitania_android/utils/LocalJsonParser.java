package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
        // Redireciona para o parser detalhado para garantir que carregamos tudo
        return parserJsonLocalDetalhes(response);
    }

    @NonNull
    public static ArrayList<Local> parserJsonLocais(JSONArray response) {
        ArrayList<Local> locais = new ArrayList<>();
        if (response == null) return locais;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject local = (JSONObject) response.get(i);
                int id = local.getInt("id");
                String nome = local.getString("nome");
                String morada = local.getString("morada");
                String distrito = local.optString("distrito", "");
                String descricao = local.optString("descricao", "");
                String imagem = local.optString("imagem", "");
                float avaliacaoMedia = (float) local.optDouble("avaliacao_media", 0.0);
                boolean favorito = local.optBoolean("favorito", false);
                int favorito_id = local.optInt("favorito_id", -1);

                Local auxlocal = new Local(id, nome, morada, distrito, descricao, imagem, avaliacaoMedia, favorito_id);
                auxlocal.setFavorite(favorito);
                locais.add(auxlocal);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return locais;
    }

    public static Local parserJsonLocalDetalhes(String response) {
        Local local = null;
        try {
            JSONObject jsonObject = new JSONObject(response);

            // --- Informações Base ---
            int id = jsonObject.getInt("id");
            // optDouble é mais seguro que getDouble
            float avaliacaoMedia = (float) jsonObject.optDouble("avaliacao_media", 0.0);
            String nome = jsonObject.getString("nome");
            String morada = jsonObject.getString("morada");
            String distrito = jsonObject.optString("distrito", "");
            String descricao = jsonObject.optString("descricao", "");
            String imagem = jsonObject.optString("imagem", "");
            int favorito_id = jsonObject.optInt("favorito_id", -1);

            local = new Local(id, nome, morada, distrito, descricao, imagem, avaliacaoMedia, favorito_id);

            // --- Contactos ---
            local.setTelefone(jsonObject.optString("contacto_telefone"));
            local.setEmail(jsonObject.optString("contacto_email"));
            local.setWebsite(jsonObject.optString("website"));

            // --- Horário ---
            JSONObject horarioJson = jsonObject.optJSONObject("horario");
            if (horarioJson != null) {
                local.addHorario("segunda", horarioJson.optString("segunda"));
                local.addHorario("terca", horarioJson.optString("terca"));
                local.addHorario("quarta", horarioJson.optString("quarta"));
                local.addHorario("quinta", horarioJson.optString("quinta"));
                local.addHorario("sexta", horarioJson.optString("sexta"));
                local.addHorario("sabado", horarioJson.optString("sabado"));
                local.addHorario("domingo", horarioJson.optString("domingo"));
            }

            // --- Avaliações ---
            JSONArray avaliacoesArray = jsonObject.optJSONArray("avaliacoes");
            if (avaliacoesArray != null) {
                ArrayList<Avaliacao> listaAvaliacoes = new ArrayList<>();
                for (int i = 0; i < avaliacoesArray.length(); i++) {
                    JSONObject avJson = avaliacoesArray.getJSONObject(i);
                    Avaliacao av = new Avaliacao(
                            avJson.getInt("id"),
                            avJson.getInt("local_id"),
                            avJson.getInt("utilizador_id"),
                            avJson.optString("utilizador", "Anónimo"),
                            (float) avJson.optDouble("classificacao", 0.0),
                            avJson.optString("comentario", ""),
                            avJson.optString("data_avaliacao", "")
                    );
                    listaAvaliacoes.add(av);
                }
                local.setAvaliacoes(listaAvaliacoes);
            }

            // --- TIPOS DE BILHETE (CORREÇÃO AQUI) ---
            Log.d("LocalJsonParser", "A procurar bilhetes...");

            // Tenta encontrar o array com vários nomes possíveis
            JSONArray bilhetesArray = jsonObject.optJSONArray("tipos-bilhete");
            if (bilhetesArray == null) bilhetesArray = jsonObject.optJSONArray("tipos_bilhete");
            if (bilhetesArray == null) bilhetesArray = jsonObject.optJSONArray("bilhetes");

            if (bilhetesArray != null) {
                Log.d("LocalJsonParser", "Bilhetes encontrados: " + bilhetesArray.length());
                ArrayList<TipoBilhete> listaBilhetes = new ArrayList<>();
                for (int i = 0; i < bilhetesArray.length(); i++) {
                    JSONObject bilheteJson = bilhetesArray.getJSONObject(i);

                    // Verifica se está ativo
                    int ativo = bilheteJson.optInt("ativo", 1);

                    if (ativo == 1) {
                        // Usamos optString para evitar erros se a descrição ou preço vierem a null
                        TipoBilhete tb = new TipoBilhete(
                                bilheteJson.getInt("id"),
                                bilheteJson.getString("nome"),
                                bilheteJson.optString("descricao", "Bilhete"),
                                bilheteJson.optString("preco", "0"),
                                ativo,
                                bilheteJson.optInt("local_id", id)
                        );
                        listaBilhetes.add(tb);
                    }
                }
                local.setTiposBilhete(listaBilhetes);
            } else {
                Log.e("LocalJsonParser", "Nenhuma lista de bilhetes encontrada no JSON.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("LocalJsonParser", "Erro JSON: " + e.getMessage());
        }
        return local;
    }

    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
