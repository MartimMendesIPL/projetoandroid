package pt.ipleiria.estg.dei.maislusitania_android;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import pt.ipleiria.estg.dei.maislusitania_android.listeners.SignupListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class RegisterActivity extends AppCompatActivity implements SignupListener {

    private EditText etRegUsername, etRegEmail , etRegPassword, etRegConfirmPassword, etRegPrimeiroNome, etRegUltimoNome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar componentes de UI, se necessário
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        etRegPrimeiroNome = findViewById(R.id.etRegPrimeiroNome);
        etRegUltimoNome = findViewById(R.id.etRegUltimoNome);

        // Registar como listener
        SingletonLusitania.getInstance(this).setSignupListener(this);
    }

    public void Signup(View view) {
        String username = etRegUsername.getText().toString();
        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();
        String confirmPassword = etRegConfirmPassword.getText().toString();
        String primeiroNome = etRegPrimeiroNome.getText().toString();
        String ultimoNome = etRegUltimoNome.getText().toString();
        if (!validateSignupCampos()) {
            return;
        }
        SingletonLusitania.getInstance(this).signupAPI(username, email, password, primeiroNome, ultimoNome, this);
    }

    private boolean validateSignupCampos() {
        String username = etRegUsername.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirmPassword = etRegConfirmPassword.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String primeiroNome = etRegPrimeiroNome.getText().toString().trim();
        String ultimoNome = etRegUltimoNome.getText().toString().trim();

        if (username.isEmpty()) {
            etRegUsername.setError("Campo obrigatório");
            return false;
        }
        if (password.isEmpty()) {
            etRegPassword.setError("Campo obrigatório");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etRegConfirmPassword.setError("As passwords não coincidem");
            return false;
        }
        if (email.isEmpty()) {
            etRegEmail.setError("Campo obrigatório");
            return false;
        }
        // Simple validação de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.setError("Email inválido");
            return false;
        }
        if (primeiroNome.isEmpty()) {
            etRegPrimeiroNome.setError("Campo obrigatório");
            return false;
        }
        if (ultimoNome.isEmpty()) {
            etRegUltimoNome.setError("Campo obrigatório");
            return false;
        }
        return true;
    }


    public void goToLogin(View view) {
        finish(); // Volta para a tela anterior
        // Opção 1: Fade simples
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onSignupSuccess() {
        Toast.makeText(this,"Registo efetuado com sucesso! Por favor, inicie sessão.", Toast.LENGTH_LONG).show();
        finish(); // Volta para a tela anterior
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}