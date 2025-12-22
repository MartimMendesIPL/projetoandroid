package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;

public interface FavoritoListener {
    void onFavoritosLoaded(ArrayList<Favorito> listaFavoritos);
    void onFavoritosError(String message);

}
