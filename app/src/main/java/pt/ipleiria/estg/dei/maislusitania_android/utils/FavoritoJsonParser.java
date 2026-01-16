package pt.ipleiria.estg.dei.maislusitania_android.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;

public class FavoritoJsonParser {
    // Metodo para converter o JSONArray de favoritos em uma lista de objetos Favorito
    public static ArrayList<Favorito> parserJsonFavoritos(JSONArray response)
    {
        // Lista para armazenar os favoritos convertidos
        ArrayList<Favorito> favoritos = new ArrayList<>();
        for (int i = 0; i < response.length(); i++)
        {
            try
            {
                // Obtém o objeto JSON da posição i
                JSONObject favorito = (JSONObject) response.get(i);
                // Extrai os campos necessários
                int id = favorito.getInt("id");
                int utilizadorId = favorito.getInt("utilizador_id");
                int localId = favorito.getInt("local_id");
                String localImagem = favorito.getString("local_imagem");
                String localNome = favorito.getString("local_nome");
                String localDistrito = favorito.getString("local_distrito");
                // Usa o optDouble para evitar exceções se o campo não existir
                float avaliacaoMedia = (float) favorito.optDouble("local_rating", 0.0);
                String dataAdicao = favorito.getString("data_adicao");
                boolean isFavorite = favorito.optBoolean("is_favorite", true);
                // Cria o objeto Favorito
                Favorito auxFavorito = new Favorito(id, utilizadorId, localId, localImagem, localNome, localDistrito, avaliacaoMedia, dataAdicao, isFavorite);
                // Adiciona o favorito à lista
                favoritos.add(auxFavorito);
            }
            catch (JSONException e)
            {
                throw new RuntimeException(e);
            }
        }
        return favoritos;
    }
}
