package pt.ipleiria.estg.dei.maislusitania_android.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

public class EventosJsonParser {
    public static ArrayList<Evento> parserJsonEventos(JSONArray response) {
        ArrayList<Evento> eventos = new ArrayList<>();
        if (response == null) return eventos;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject evento = (JSONObject) response.get(i);

                // Use optInt and optString to prevent crashes on null values
                int id = evento.optInt("id", 0);
                String local = evento.optString("local", "Sem local");
                String titulo = evento.optString("titulo", "Sem título");
                String descricao = evento.optString("descricao", "Sem descrição");

                // If API returns null for image, we handle it
                String imagem = evento.optString("imagem");
                if (imagem.equals("null")) imagem = null;

                String dataInicio = evento.optString("data_inicio", "");
                String dataFim = evento.optString("data_fim", "");

                Evento auxEvento = new Evento(id, local, titulo, descricao, imagem, dataInicio, dataFim);
                eventos.add(auxEvento);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return eventos;
    }

    public static Evento parserJsonEvento(JSONArray response) {
        Evento auxEvento = null;
        try {
            if (response == null || response.length() == 0) {
                android.util.Log.e("EventoParser", "String vazia ou nula");
                return null;
            }

            // Get the first object from the array
            JSONObject evento = (JSONObject) response.get(0);

            android.util.Log.d("EventoParser", "JSON recebido: " + evento.toString());

            int id = evento.optInt("id", 0);
            String local = evento.optString("local", "Sem local");
            String titulo = evento.optString("titulo", "Sem título");
            String descricao = evento.optString("descricao", "Sem descrição");
            String imagem = evento.optString("imagem", "");
            String dataInicio = evento.optString("data_inicio", ""); // Fixed key name based on parserJsonEventos
            String dataFim = evento.optString("data_fim", "");       // Fixed key name based on parserJsonEventos

            auxEvento = new Evento(id, local, titulo, descricao, imagem, dataInicio, dataFim);
            return auxEvento;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}