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
    // Declaração dos campos de entrada
    private EditText etRegUsername, etRegEmail , etRegPassword, etRegConfirmPassword, etRegPrimeiroNome, etRegUltimoNome;
    @Override
    // Metodo onCreate
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializar componentes de UI
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        etRegPrimeiroNome = findViewById(R.id.etRegPrimeiroNome);
        etRegUltimoNome = findViewById(R.id.etRegUltimoNome);
        // Configurar o listener de registo
        SingletonLusitania.getInstance(this).setSignupListener(this);
    }
    // Metodo onClick para o botao de registo
    public void Signup(View view)
    {
        // Obter os valores dos campos de entrada
        String username = etRegUsername.getText().toString();
        String email = etRegEmail.getText().toString();
        String password = etRegPassword.getText().toString();
        String confirmPassword = etRegConfirmPassword.getText().toString();
        String primeiroNome = etRegPrimeiroNome.getText().toString();
        String ultimoNome = etRegUltimoNome.getText().toString();
        // Validar os campos de registo
        if (!validateSignupCampos())
        {
            return;
        }
        // Chamar o metodo de registo do Singleton
        SingletonLusitania.getInstance(this).signupAPI(username, email, password, primeiroNome, ultimoNome, this);
    }
    // Metodo para validar os campos de registo
    private boolean validateSignupCampos()
    {
        // Obter os valores dos campos de entrada
        String username = etRegUsername.getText().toString().trim();
        String password = etRegPassword.getText().toString().trim();
        String confirmPassword = etRegConfirmPassword.getText().toString().trim();
        String email = etRegEmail.getText().toString().trim();
        String primeiroNome = etRegPrimeiroNome.getText().toString().trim();
        String ultimoNome = etRegUltimoNome.getText().toString().trim();
        // Validar os campos
        // Verificar se os campos estão vazios
        if (username.isEmpty()) {
            etRegUsername.setError("Campo obrigatório");
            return false;
        }
        if (password.isEmpty()) {
            etRegPassword.setError("Campo obrigatório");
            return false;
        }
        // Verificar se a password é igual à confirmação
        if (!password.equals(confirmPassword)) {
            etRegConfirmPassword.setError("As passwords não coincidem");
            return false;
        }
        // Verificar se o email está vazio
        if (email.isEmpty()) {
            etRegEmail.setError("Campo obrigatório");
            return false;
        }
        // Verificar se o email é válido
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegEmail.setError("Email inválido");
            return false;
        }
        // Verificar se o primeiro e último nome estão vazios
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
    // Metodo onClick para voltar ao login
    public void goToLogin(View view)
    {
        // Volta para a tela anterior
        finish();
        // Opção 1: Fade simples
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    @Override
    public void onSignupSuccess()
    {
        Toast.makeText(this,"Registo efetuado com sucesso! Por favor, inicie sessão.", Toast.LENGTH_LONG).show();
        // Volta para a tela anterior
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}