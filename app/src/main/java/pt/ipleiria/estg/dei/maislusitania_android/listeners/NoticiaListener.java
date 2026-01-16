package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

public interface  NoticiaListener {
    // para carregar as noticias
    void onNoticiasLoaded(ArrayList<Noticia> listaNoticias);
    // para carregar uma noticia
    void onNoticiaLoaded(Noticia noticia);
    // para mostrar os erros
    void onNoticiaError(String message);
}
