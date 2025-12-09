package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.maislusitania_android.utils.SingletonLusitania;

public class LoginActivity extends AppCompatActivity  implements SingletonLusitania.LoginListener{

    private EditText etUsername, etPassword;
    private static final String SHARED_PREFS_NAME = "MaisLusitaniaPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        // Registar como listener
        SingletonLusitania.getInstance(this).setLoginListener(this);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    public void Login(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (!validateLoginCampos()) {
            return;
        }

        // Invocar o método loginAPI do Singleton
        SingletonLusitania.getInstance(this).loginAPI(username, password);
    }

    private boolean validateLoginCampos() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Campo obrigatório");
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Campo obrigatório");
            return false;
        }

        return true;
    }

    @Override
    public void onValidateLogin(final String token, final String username) {
        // Guardar no SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", token);
        editor.putString("username", username);
        editor.apply();

        // Ir para MainActivity
        Toast.makeText(this, "Bem-vindo, " + username + "!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void GuestLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
