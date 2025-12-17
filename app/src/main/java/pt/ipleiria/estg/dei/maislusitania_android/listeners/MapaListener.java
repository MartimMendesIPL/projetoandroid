package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Mapa;

public interface MapaListener {
    void onMapaLoaded(ArrayList<Mapa> mapaLocais);

    void onMapaError(String message);
}
