package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LoginListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

/**
 * Activity de autenticação do utilizador com login e registo
 */
public class LoginActivity extends AppCompatActivity implements LoginListener {

    // Campos de entrada de username e password
    private EditText etUsername, etPassword;

    /**
     * Inicializa a activity e verifica se o utilizador já está autenticado
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // VERIFICAÇÃO AUTOMÁTICA (SharedPreferences via Singleton)
        // Se já tem token guardado, salta o ecrã de login
        if (SingletonLusitania.getInstance(this).isUtilizadorLogado(this)) {
            MainActivity();
            return; // Interrompe o onCreate para não carregar o layout de login
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        // Registra esta activity como listener de eventos de login
        SingletonLusitania.getInstance(this).setLoginListener(this);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Navega para a activity de registo
     */
    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Realiza o login do utilizador na API
     */
    public void Login(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // Valida os campos antes de enviar
        if (!validateLoginCampos()) {
            return;
        }

        // Envia pedido de login à API
        SingletonLusitania.getInstance(this).loginAPI(username, password, this);
    }

    /**
     * Valida se os campos de username e password estão preenchidos
     */
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

    /**
     * Callback quando o login é bem-sucedido
     * Guarda as credenciais e navega para MainActivity
     */
    @Override
    public void onValidateLogin(final String token, final String username, final String user_id) {
        runOnUiThread(() -> {
            // Desativa modo convidado ao fazer login
            SingletonLusitania.getInstance(this).setGuestMode(this, false);

            SingletonLusitania.getInstance(this).guardarUtilizador(this, username, token, user_id);
            Toast.makeText(this, "Bem-vindo, " + username + "!", Toast.LENGTH_SHORT).show();
            MainActivity();
        });
    }

    /**
     * Navega para MainActivity e fecha LoginActivity
     */
    private void MainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Fecha a LoginActivity para o utilizador não voltar atrás
    }

    /**
     * Acesso como convidado sem autenticação
     */
    public void GuestLogin(View view) {
        // Ativa modo convidado
        SingletonLusitania.getInstance(this).setGuestMode(this, true);
        MainActivity();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Mostra diálogo para alterar a URL da API e guarda nas preferências
     */
    public void AlterarAPI(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        // Carrega o layout customizado do diálogo
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_url, null);
        EditText etMainUrl = dialogView.findViewById(R.id.etMainUrl);

        // Pré-preenchimento com a URL atual
        etMainUrl.setText(SingletonLusitania.getInstance(getApplicationContext()).buildUrl(""));

        builder.setView(dialogView)
                .setTitle("Configurar URL")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String url = etMainUrl.getText().toString().trim();
                    if (!url.isEmpty()) {
                        // Guarda a nova URL no Singleton
                        SingletonLusitania.getInstance(getApplicationContext()).setMainUrl(url);
                        Toast.makeText(LoginActivity.this, "URL guardada", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
