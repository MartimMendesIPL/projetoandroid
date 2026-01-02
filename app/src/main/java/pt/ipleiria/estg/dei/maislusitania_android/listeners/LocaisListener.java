package pt.ipleiria.estg.dei.maislusitania_android.listeners;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;

import java.util.ArrayList;

public interface LocaisListener {
    void onLocaisLoaded(ArrayList<Local> listaLocais);

    void onLocaisError(String message);


    // Novos métodos para detalhes de um único local
    void onLocalLoaded(Local local);
    void onLocalError(String message);
}
