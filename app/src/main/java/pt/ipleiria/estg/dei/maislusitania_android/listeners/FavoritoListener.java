package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;

public interface FavoritoListener {
    // para carregar os favoritos
    void onFavoritosLoaded(ArrayList<Favorito> listaFavoritos);
    // para mostrar os erros
    void onFavoritosError(String message);

}
