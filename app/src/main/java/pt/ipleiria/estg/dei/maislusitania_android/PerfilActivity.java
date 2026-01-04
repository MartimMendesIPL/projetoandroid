package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pt.ipleiria.estg.dei.maislusitania_android.databinding.ActivityPerfilBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.PerfilListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.models.User;

public class PerfilActivity extends AppCompatActivity {

    private ActivityPerfilBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarListeners();
        carregarDadosUtilizador();
    }

    private void configurarListeners() {
        // Botão Voltar
        binding.btnVoltar.setOnClickListener(v -> finish());

        // Ver Favoritos
        binding.layoutFavoritos.setOnClickListener(v -> {
            FavoritoFragment fragment = new FavoritoFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.layoutEditarPerfil.setOnClickListener(v -> {
            // Abrir caixa de diálogo para editar perfil
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

            EditText etFirstName = dialogView.findViewById(R.id.tvFirstName);
            EditText etLastName = dialogView.findViewById(R.id.tvLastName);
            EditText etUsername = dialogView.findViewById(R.id.tvUsername);

            etFirstName.setText(binding.tvFirstName.getText().toString());
            etLastName.setText(binding.tvLastName.getText().toString());
            etUsername.setText(binding.tvUsername.getText().toString());

            builder.setView(dialogView)
                    .setTitle("Editar Perfil")
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String firstName = etFirstName.getText().toString().trim();
                        String lastName = etLastName.getText().toString().trim();
                        String username = etUsername.getText().toString().trim();

                        if (!firstName.isEmpty() && !lastName.isEmpty() && !username.isEmpty()) {
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
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        binding.layoutMudarPassword.setOnClickListener(v -> {
            // Abrir caixa de diálogo para mudar password
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();

            View dialogView = inflater.inflate(R.layout.dialog_change_password, null);

            EditText etCurrentPassword = dialogView.findViewById(R.id.etCurrentPassword);
            EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);

            builder.setView(dialogView)
                    .setTitle("Mudar Password")
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String currentPassword = etCurrentPassword.getText().toString().trim();
                        String newPassword = etNewPassword.getText().toString().trim();

                        if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
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
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });
        // Botão Apagar Conta
        binding.layoutApagarConta.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Remover Conta")
                    .setMessage("Tem a certeza que pretende remover a sua conta? Esta ação é irreversível.")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        SingletonLusitania.getInstance(this).deleteUserAPI(this);
                        //Sair da conta
                        binding.layoutLogout.performClick();
                        Toast.makeText(this, "Conta removida com sucesso", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });

        // Botão Logout
        binding.layoutLogout.setOnClickListener(v -> {
            SingletonLusitania.getInstance(this).logout(this);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "Logout efetuado com sucesso", Toast.LENGTH_SHORT).show();
        });
    }

    private void carregarDadosUtilizador() {
        SingletonLusitania.getInstance(this).setPerfilListener(new PerfilListener() {
            @Override
            public void onPerfilLoaded(User user) {
                binding.tvUsername.setText(user.getUsername());
                binding.tvFirstName.setText(user.getPrimeiro_nome());
                binding.tvLastName.setText(user.getUltimo_nome());
                binding.tvEmail.setText(user.getEmail());
                binding.tvMemberSince.setText(String.valueOf(user.getData_adesao()));

                if (user.getImagem_perfil() != null && !user.getImagem_perfil().isEmpty()) {

                    Glide.with(PerfilActivity.this)
                            .load(user.getImagem_perfil())
                            .placeholder(R.drawable.ic_perfil)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.ivProfilePhoto);

                } else {
                    binding.ivProfilePhoto.setImageResource(R.drawable.ic_perfil);
                }
            }

            @Override
            public void onPerfilError(String error) {
                Toast.makeText(PerfilActivity.this, "Erro: " + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPerfilLogout() {}

            @Override
            public void onPerfilLogoutError(String error) {}

            @Override
            public void onPasswordChanged() {}
        });

        SingletonLusitania.getInstance(this).getUserProfileAPI(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
