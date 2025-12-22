package pt.ipleiria.estg.dei.maislusitania_android.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;

public class FavoritoJsonParser {
    // Converter um ARRAY de JSONs numa LISTA de Favoritos
    public static ArrayList<Favorito> parserJsonFavoritos(JSONArray response)
    {
        ArrayList<Favorito> favoritos = new ArrayList<>();

        for (int i = 0; i < response.length(); i++)
        {
            try
            {
                JSONObject favorito = (JSONObject) response.get(i);

                int id = favorito.getInt("id");
                int utilizadorId = favorito.getInt("utilizador_id");
                int localId = favorito.getInt("local_id");
                String localImagem = favorito.getString("local_imagem");
                String localNome = favorito.getString("local_nome");
                String localDistrito = favorito.getString("local_distrito");
                // Usa o optDouble para evitar exceções se o campo não existir
                float avaliacaoMedia = (float) favorito.optDouble("avaliacao_media", 0.0);
                String dataAdicao = favorito.getString("data_adicao");
                boolean isFavorite = favorito.optBoolean("is_favorite", true);

                Favorito auxFavorito = new Favorito(id, utilizadorId, localId, localImagem, localNome, localDistrito, avaliacaoMedia, dataAdicao, isFavorite);
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
