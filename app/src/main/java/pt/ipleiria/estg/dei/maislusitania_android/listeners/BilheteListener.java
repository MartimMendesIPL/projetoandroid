package pt.ipleiria.estg.dei.maislusitania_android.listeners;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.maislusitania_android.models.Bilhete;

public interface BilheteListener {
    void onBilhetesLoaded(ArrayList<Bilhete> bilhetes);
    void onBilhetesError(String message);
}
    