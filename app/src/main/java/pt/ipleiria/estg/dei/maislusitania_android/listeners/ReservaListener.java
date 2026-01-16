package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;

public interface ReservaListener {
    void onReservasLoaded(ArrayList<Reserva> listaReservas);
    void onReservaLoaded(Reserva reserva);
    void onReservasError(String message);
    void onReservaCreated(Reserva reserva);
    void onReservaError(String message);
}
