package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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
        });

        SingletonLusitania.getInstance(this).getUserProfileAPI(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
