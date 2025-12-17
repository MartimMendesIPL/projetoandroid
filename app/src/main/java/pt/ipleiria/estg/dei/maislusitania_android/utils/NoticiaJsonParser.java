package pt.ipleiria.estg.dei.maislusitania_android.utils;

import org.json.JSONArray;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

public class NoticiaJsonParser {
    public static ArrayList<Noticia> parserJsonNoticias(JSONArray response)
    {
        ArrayList<Noticia> noticias = new ArrayList<>();
        for (int i = 0; i < response.length(); i++)
        {
            try
            {
                org.json.JSONObject noticia = (org.json.JSONObject) response.get(i);
                int id = noticia.getInt("id");
                String titulo = noticia.getString("titulo");
                String conteudo = noticia.getString("conteudo");
                String resumo = noticia.getString("resumo");
                String imagem = noticia.getString("imagem");
                String dataPublicacao = noticia.getString("dataPublicacao");
                boolean ativo = noticia.getBoolean("ativo");
                int localId = noticia.getInt("localId");
                boolean destaque = noticia.getBoolean("destaque");

                Noticia auxNoticia = new Noticia(id, titulo, conteudo, resumo, imagem, dataPublicacao, ativo, localId, destaque);
                noticias.add(auxNoticia);
            }
            catch (org.json.JSONException e)
            {
                throw new RuntimeException(e);
            }
        }
        return noticias;
    }

    public static Noticia parserJsonNoticia(String response) {
        Noticia auxNoticia = null;
        try
        {
            org.json.JSONObject noticia = new org.json.JSONObject(response);
            int id = noticia.getInt("id");
            String titulo = noticia.getString("titulo");
            String conteudo = noticia.getString("conteudo");
            String resumo = noticia.getString("resumo");
            String imagem = noticia.getString("imagem");
            String dataPublicacao = noticia.getString("dataPublicacao");
            boolean ativo = noticia.getBoolean("ativo");
            int localId = noticia.getInt("localId");
            boolean destaque = noticia.getBoolean("destaque");

            auxNoticia = new Noticia(id, titulo, conteudo, resumo, imagem, dataPublicacao, ativo, localId, destaque);
        }
        catch (org.json.JSONException e)
        {
            throw new RuntimeException(e);
        }
        return auxNoticia;
    }
}
