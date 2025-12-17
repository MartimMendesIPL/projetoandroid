package pt.ipleiria.estg.dei.maislusitania_android.listeners;

public interface PerfilListener {
        void onPerfilLoaded(String username, String email, String telemovel, String nif);
        void onPerfilError(String error);

        void onPerfilLogout();
        void onPerfilLogoutError(String error);
}
