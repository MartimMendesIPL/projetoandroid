package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;

public interface EventoListener {
    void onEventosLoaded(ArrayList<Evento> listaEventos);
    void onEventoLoaded(ArrayList<Evento> evento);
    void onEventoError(String message);

}
