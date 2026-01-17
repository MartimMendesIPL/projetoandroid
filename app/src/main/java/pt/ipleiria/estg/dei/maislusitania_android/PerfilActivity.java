package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.ActivityPerfilBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.PerfilListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.models.User;

/**
 * Activity que exibe e permite editar o perfil do utilizador
 * Inclui opções para editar dados, mudar password, ver favoritos e apagar conta
 */
public class PerfilActivity extends AppCompatActivity {

    // Binding para acesso aos elementos da UI
    private ActivityPerfilBinding binding;

    /**
     * Inicializa a activity e carrega dados do utilizador
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarListeners();
        carregarDadosUtilizador();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Configura os listeners para todos os botões e opções do perfil
     */
    private void configurarListeners() {
        // Botão Voltar - fecha a activity
        binding.btnVoltar.setOnClickListener(v -> finish());

        // Layout Ver Favoritos - abre a activity de favoritos
        binding.layoutFavoritos.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritoActivity.class);
            startActivity(intent);
        });

        // Layout Editar Perfil - abre diálogo para editar dados do utilizador
        binding.layoutEditarPerfil.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            // Carrega o layout customizado do diálogo
            View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

            // Obtém referências dos campos de entrada
            EditText etFirstName = dialogView.findViewById(R.id.tvFirstName);
            EditText etLastName = dialogView.findViewById(R.id.tvLastName);
            EditText etUsername = dialogView.findViewById(R.id.tvUsername);

            // Pré-preenchimento com os dados atuais do utilizador
            etFirstName.setText(binding.tvFirstName.getText().toString());
            etLastName.setText(binding.tvLastName.getText().toString());
            etUsername.setText(binding.tvUsername.getText().toString());

            AlertDialog dialog = builder.setView(dialogView)
                    .setPositiveButton("Guardar", (dialogInterface, which) -> {
                        String firstName = etFirstName.getText().toString().trim();
                        String lastName = etLastName.getText().toString().trim();
                        String username = etUsername.getText().toString().trim();

                        // Valida se todos os campos estão preenchidos
                        if (!firstName.isEmpty() && !lastName.isEmpty() && !username.isEmpty()) {
                            // Envia os dados editados à API
                            SingletonLusitania.getInstance(this).editUserProfileAPI(
                                    this,
                                    firstName,
                                    lastName,
                                    username
                            );
                        } else {
                            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialogInterface, which) -> dialogInterface.dismiss())
                    .create();

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            dialog.show();
        });

        // Layout Mudar Password - abre diálogo para alterar password
        binding.layoutMudarPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            // Carrega o layout customizado do diálogo
            View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

            // Obtém referências dos campos de entrada
            EditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
            EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);

            AlertDialog dialog = builder.setView(dialogView)
                    .setPositiveButton("Guardar", (dialogInterface, which) -> {
                        String currentPassword = etCurrentPassword.getText().toString().trim();
                        String newPassword = etNewPassword.getText().toString().trim();

                        // Valida se ambos os campos estão preenchidos
                        if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
                            // Envia pedido de alteração de password à API
                            SingletonLusitania.getInstance(this).changePasswordAPI(
                                    this,
                                    currentPassword,
                                    newPassword
                            );
                            Toast.makeText(this, "Password alterada com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialogInterface, which) -> dialogInterface.dismiss())
                    .create();

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            dialog.show();
        });

        // Layout Apagar Conta - solicita confirmação antes de apagar
        binding.layoutApagarConta.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Remover Conta")
                    .setMessage("Tem a certeza que pretende remover a sua conta? Esta ação é irreversível.")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        // Envia pedido de eliminação de conta à API
                        SingletonLusitania.getInstance(this).deleteUserAPI(this);
                        // Realiza logout imediatamente após eliminação
                        binding.layoutLogout.performClick();
                        Toast.makeText(this, "Conta removida com sucesso", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        // Layout Logout - termina a sessão e volta para Login
        binding.layoutLogout.setOnClickListener(v -> {
            // Limpa os dados do utilizador guardados localmente
            SingletonLusitania.getInstance(this).logout(this);
            // Cria intent para voltar à LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            // Limpa a stack de activities para não permitir voltar atrás
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logout efetuado com sucesso", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Carrega os dados do utilizador da cache ou da API
     */
    private void carregarDadosUtilizador() {
        // Tenta carregar dados em cache primeiro (mais rápido)
        User cachedUser = SingletonLusitania.getInstance(this).getCachedUser();
        if (cachedUser != null) {
            atualizarUI(cachedUser);
        }

        // Registra listener para eventos do perfil
        SingletonLusitania.getInstance(this).setPerfilListener(new PerfilListener() {
            /**
             * Chamado quando o perfil é carregado com sucesso da API
             */
            @Override
            public void onPerfilLoaded(User user) {
                atualizarUI(user);
            }

            /**
             * Chamado quando há erro ao carregar o perfil
             */
            @Override
            public void onPerfilError(String error) {
                // Mostra erro apenas se não há cache disponível
                if (cachedUser == null) {
                    Toast.makeText(PerfilActivity.this, "Erro de rede: " + error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPerfilLogout() {}

            @Override
            public void onPerfilLogoutError(String error) {}

            @Override
            public void onPasswordChanged() {}
        });

        // Carrega dados do perfil da API
        SingletonLusitania.getInstance(this).getUserProfileAPI(this);
    }

    /**
     * Atualiza a UI com os dados do utilizador
     */
    private void atualizarUI(User user) {
        if (user != null && binding != null) {
            // Atualiza os campos de texto com os dados do utilizador
            binding.tvUsername.setText(user.getUsername());
            binding.tvFirstName.setText(user.getPrimeiro_nome());
            binding.tvLastName.setText(user.getUltimo_nome());
            binding.tvEmail.setText(user.getEmail());
            binding.tvMemberSince.setText(user.getData_adesao());

            // Carrega e exibe a imagem de perfil ou usa placeholder se não existir
            if (user.getImagem_perfil() != null && !user.getImagem_perfil().isEmpty()) {
                Glide.with(this)
                        .load(user.getImagem_perfil())
                        .placeholder(R.drawable.ic_perfil)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_perfil)
                        .into(binding.ivProfilePhoto);
            } else {
                // Usa a imagem padrão se não houver imagem de perfil
                binding.ivProfilePhoto.setImageResource(R.drawable.ic_perfil);
            }
        }
    }

    /**
     * Customiza a animação de saída quando a activity fecha
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Limpa recursos quando a activity é destruída
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
