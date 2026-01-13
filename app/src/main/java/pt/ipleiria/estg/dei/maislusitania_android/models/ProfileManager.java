// C:/Users/marti/AndroidStudioProjects/proj/app/src/main/java/pt/ipleiria/estg/dei/maislusitania_android/models/ProfileManager.java
package pt.ipleiria.estg.dei.maislusitania_android.models;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileManager {

    private static final String PREF_NAME = "UserProfile";
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FIRST_NAME = "primeiro_nome";
    private static final String KEY_LAST_NAME = "ultimo_nome";
    private static final String KEY_MEMBER_SINCE = "data_adesao";
    private static final String KEY_PROFILE_IMAGE = "imagem_perfil";

    private final SharedPreferences sharedPreferences;

    public ProfileManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FIRST_NAME, user.getPrimeiro_nome());
        editor.putString(KEY_LAST_NAME, user.getUltimo_nome());
        editor.putString(KEY_MEMBER_SINCE, user.getData_adesao());
        editor.putString(KEY_PROFILE_IMAGE, user.getImagem_perfil());
        editor.apply();
    }

    public User getUser() {
        if (!sharedPreferences.contains(KEY_USERNAME)) {
            return null;
        }
        return new User(
                sharedPreferences.getInt(KEY_ID, 0),
                sharedPreferences.getString(KEY_USERNAME, ""),
                sharedPreferences.getString(KEY_EMAIL, ""),
                sharedPreferences.getString(KEY_FIRST_NAME, ""),
                sharedPreferences.getString(KEY_LAST_NAME, ""),
                sharedPreferences.getString(KEY_MEMBER_SINCE, ""),
                sharedPreferences.getString(KEY_PROFILE_IMAGE, "")
        );
    }

    public void clearUserProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
