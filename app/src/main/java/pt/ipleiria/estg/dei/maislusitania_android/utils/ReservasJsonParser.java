package pt.ipleiria.estg.dei.maislusitania_android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;
import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;
import pt.ipleiria.estg.dei.maislusitania_android.models.TipoBilhete;

public class ReservasJsonParser {

    // ==========================================
    // PARSE RESERVAS (actionIndex)
    // ==========================================
    public static ArrayList<Reserva> parserJsonReservas(JSONArray response) {
        ArrayList<Reserva> listaReservas = new ArrayList<>();
        if (response != null) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    int id = jsonObject.getInt("id");
                    int local_id = jsonObject.getInt("local_id");
                    String local_nome = jsonObject.getString("local_nome");
                    String data_visita = jsonObject.getString("data_visita");
                    String estado = jsonObject.getString("estado");
                    String data_criacao = jsonObject.getString("data_criacao");

                    // Tratamento do Preço (Vem como String do PHP number_format)
                    String precoStr = jsonObject.getString("preco_total").replace(",", "");
                    double preco_total = Double.parseDouble(precoStr);

                    String imagem_local = jsonObject.getString("imagem_local");


                    Reserva reserva = new Reserva(id, local_id, local_nome, data_visita, preco_total, estado, data_criacao, imagem_local);
                    listaReservas.add(reserva);

                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return listaReservas;
    }

    // ==========================================
    // PARSE BILHETES (actionBilhetes)
    // ==========================================
    public static ArrayList<Bilhete> parserJsonBilhetes(JSONArray response) {
        ArrayList<Bilhete> listaBilhetes = new ArrayList<>();
        if (response != null) {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);

                    String codigo = jsonObject.getString("codigo");
                    int reserva_id = jsonObject.getInt("reserva_id");
                    int local_id = jsonObject.getInt("local_id");
                    String local_nome = jsonObject.getString("local_nome");
                    String data_visita = jsonObject.getString("data_visita");
                    int tipo_bilhete_id = jsonObject.getInt("tipo_bilhete_id");
                    String tipo_bilhete_nome = jsonObject.getString("tipo_bilhete_nome");
                    String estado = jsonObject.getString("estado");

                    // Tratamento do Preço Unitário
                    String precoStr = jsonObject.getString("preco").replace(",", "");
                    double preco = Double.parseDouble(precoStr);

                    Bilhete bilhete = new Bilhete(codigo, reserva_id, local_id, local_nome, data_visita, tipo_bilhete_id, tipo_bilhete_nome, preco, estado);
                    listaBilhetes.add(bilhete);

                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return listaBilhetes;
    }

    // ==========================================
// CRIAR BODY JSON PARA POST RESERVA
// ==========================================
    public static JSONObject criarBodyReserva(int localId, String dataVisita, ArrayList<TipoBilhete> tiposBilhete) throws JSONException {
        JSONObject body = new JSONObject();
        body.put("local_id", localId);
        body.put("data_visita", dataVisita);

        // Criar objeto bilhetes com as quantidades
        JSONObject bilhetesObj = new JSONObject();
        for (TipoBilhete tipo : tiposBilhete) {
            if (tipo.getQuantidade() > 0) {
                JSONObject quantidadeObj = new JSONObject();
                quantidadeObj.put("quantidade", tipo.getQuantidade());
                bilhetesObj.put(String.valueOf(tipo.getId()), quantidadeObj);
            }
        }
        body.put("bilhetes", bilhetesObj);

        return body;
    }


    // Método auxiliar para verificar internet
    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}