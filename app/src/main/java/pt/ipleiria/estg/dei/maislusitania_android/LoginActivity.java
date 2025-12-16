package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LoginListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class LoginActivity extends AppCompatActivity implements LoginListener {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. VERIFICAÇÃO AUTOMÁTICA (SharedPreferences via Singleton)
        // Se já tem token guardado, salta o login
        if (SingletonLusitania.getInstance(this).isUtilizadorLogado(this)) {
            MainActivity();
            return; // Interrompe o onCreate para não carregar o layout de login
        }

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

        SingletonLusitania.getInstance(this).loginAPI(username, password, this);
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
        runOnUiThread(() -> {
            // 2. GUARDAR NAS PREFS (via Singleton)
            SingletonLusitania.getInstance(this).guardarUtilizador(this,username, token);

            Toast.makeText(this, "Bem-vindo, " + username + "!", Toast.LENGTH_SHORT).show();
            MainActivity();
        });
    }

    private void MainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Fecha a LoginActivity para o user não voltar atrás
    }


    public void GuestLogin(View view) {
        MainActivity();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}