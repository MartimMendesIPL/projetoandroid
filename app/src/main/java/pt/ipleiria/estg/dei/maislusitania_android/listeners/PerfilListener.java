package pt.ipleiria.estg.dei.maislusitania_android.listeners;

import pt.ipleiria.estg.dei.maislusitania_android.models.User;

public interface PerfilListener {
        void onPerfilLoaded( User user);
        void onPerfilError(String error);
        void onPerfilLogout();
        void onPerfilLogoutError(String error);
        void onPasswordChanged();
}
