package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setTitle("Login");

        //Inicalizar componentes
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    //TODO implementar o método de login com API
    public void Login(View view) {

        String email = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (!validateLogin()) {
            return;
        }

        // Após o login ser bem-sucedido, adicione:
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish(); // Para não voltar ao login ao pressionar "voltar"
    }


    private boolean validateLogin() {

        if (etUsername.getText().toString().isEmpty()) {
            etUsername.setError("Username inválido");
            etUsername.requestFocus();
            return false;
        }

        if (etPassword.getText().toString().length() < 6) {
            etPassword.setError("Password inválida");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }
}