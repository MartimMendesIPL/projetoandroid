package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

public interface NoticiaListener {
    void onNoticiasLoaded(ArrayList<Noticia> listaNoticias);
    void onNoticiaLoaded(Noticia noticia);
    void onNoticiaError(String message);
}
