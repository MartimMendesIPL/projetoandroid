package pt.ipleiria.estg.dei.maislusitania_android.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;

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

    public static Evento parserJsonEvento(String response) {
        try {
            JSONObject evento = new JSONObject(response);
            int id = evento.optInt("id", 0);
            String local = evento.optString("local", "");
            String titulo = evento.optString("titulo", "");
            String descricao = evento.optString("descricao", "");
            String imagem = evento.optString("imagem", "");
            String dataInicio = evento.optString("dataInicio", "");
            String dataFim = evento.optString("dataFim", "");

            return new Evento(id, local, titulo, descricao, imagem, dataInicio, dataFim);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}