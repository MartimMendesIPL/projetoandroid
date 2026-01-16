package pt.ipleiria.estg.dei.maislusitania_android.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

public class NoticiaJsonParser {
    // Metodo para fazer o parse do JSONArray de noticias
    public static ArrayList<Noticia> parserJsonNoticias(JSONArray response)
    {
        // Lista para armazenar as notícias convertidas
        ArrayList<Noticia> noticias = new ArrayList<>();
        for (int i = 0; i < response.length(); i++)
        {
            try
            {
                // Obtém o objeto JSON da posição i
                JSONObject noticia = (JSONObject) response.get(i);
                // Extrai os campos necessários
                int id = noticia.getInt("id");
                String nome = noticia.getString("nome");
                String nomeLocal = noticia.getString("local_nome");
                String resumo = noticia.getString("resumo");
                String imagem = noticia.getString("imagem");
                String dataPublicacao = noticia.getString("data_publicacao");
                // Cria o campo conteudo apenas para criar o objeto, mesmo que não seja usado aqui
                String conteudo = noticia.optString("conteudo", "");
                // Cria o objeto Noticia
                Noticia auxNoticia = new Noticia(id, nome, nomeLocal, resumo, conteudo, imagem, dataPublicacao);
                // Adiciona a notícia à lista
                noticias.add(auxNoticia);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return noticias;
    }
    // Metodo para fazer o parse de uma unica noticia
    public static Noticia parserJsonNoticia(JSONArray response)
    {
        // Objeto para armazenar a notícia convertida
        Noticia auxNoticia = null;
        try
        {
            // Verifica se o array é nulo ou vazio
            if (response == null || response.length() == 0)
            {
                android.util.Log.e("NoticiaParser", "Array vazio ou nulo");
                return null;
            }
            // Obtém o primeiro objeto JSON do array
            JSONObject noticia = (JSONObject) response.get(0);
            android.util.Log.d("NoticiaParser", "JSON recebido: " + noticia.toString());
            // Extrai os campos necessários
            int id = noticia.getInt("id");
            String nome = noticia.getString("nome");
            String conteudo = noticia.getString("conteudo");
            String imagem = noticia.getString("imagem");
            String dataPublicacao = noticia.getString("data_publicacao");
            // Usa optString para campos que podem ser opcionais
            String nomeLocal = noticia.optString("local_nome", "");
            String resumo = noticia.optString("resumo", "");
            // Cria o objeto Noticia
            auxNoticia = new Noticia(id, nome, nomeLocal, resumo, conteudo, imagem, dataPublicacao);
            // Retorna a notícia criada
            return auxNoticia;
        }
        catch (Exception e)
        {
            android.util.Log.e("NoticiaParser", "Erro ao fazer parse: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }
}
