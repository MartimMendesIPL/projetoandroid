package pt.ipleiria.estg.dei.maislusitania_android.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

public class NoticiaJsonParser {

    public static ArrayList<Noticia> parserJsonNoticias(JSONArray response) {
        ArrayList<Noticia> noticias = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject noticia = (JSONObject) response.get(i);

                int id = noticia.getInt("id");

                // CORREÇÃO: A API devolve "nome", mas o modelo usa "titulo"
                String titulo = noticia.has("nome") ? noticia.getString("nome") : noticia.optString("titulo");

                // Usar optString para evitar erros se o campo não vier na lista
                String conteudo = noticia.optString("conteudo", "");
                String resumo = noticia.optString("resumo", "");
                String imagem = noticia.optString("imagem", "");

                // CORREÇÃO: A API devolve "data_publicacao"
                String dataPublicacao = noticia.has("data_publicacao") ? noticia.getString("data_publicacao") : noticia.optString("dataPublicacao");

                boolean ativo = noticia.optBoolean("ativo", true);
                int localId = noticia.optInt("localId", 0);
                boolean destaque = noticia.optBoolean("destaque", false);

                Noticia auxNoticia = new Noticia(id, titulo, conteudo, resumo, imagem, dataPublicacao, ativo, localId, destaque);
                noticias.add(auxNoticia);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return noticias;
    }

    public static Noticia parserJsonNoticia(JSONObject noticia) {
        Noticia auxNoticia = null;
        try {
            int id = noticia.getInt("id");
            String titulo = noticia.has("nome") ? noticia.getString("nome") : noticia.optString("titulo");
            String conteudo = noticia.optString("conteudo", "");
            String resumo = noticia.optString("resumo", "");
            String imagem = noticia.optString("imagem", "");
            String dataPublicacao = noticia.has("data_publicacao") ? noticia.getString("data_publicacao") : noticia.optString("dataPublicacao");
            boolean ativo = noticia.optBoolean("ativo", true);
            int localId = noticia.optInt("localId", 0);
            boolean destaque = noticia.optBoolean("destaque", false);

            auxNoticia = new Noticia(id, titulo, conteudo, resumo, imagem, dataPublicacao, ativo, localId, destaque);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return auxNoticia;
    }
}
