package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Avaliacao;

public interface AvaliacaoListener {
    void onAvaliacaoRefresh(ArrayList<Avaliacao> avaliacoes);
    void onAvaliacaoLoaded(ArrayList<Avaliacao> avaliacoes);
    void onAvaliacaoError(String message);
}
