package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;

public interface NoticiaListener {
    void onNoticiaLoaded(ArrayList<Noticia> listaNoticias);
    void onNoticiaError(String message);
}
