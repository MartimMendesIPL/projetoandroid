package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class PerfilActivity extends AppCompatActivity {

    // Botões de navegação
    private ImageButton btnVoltar;

    private ImageButton btnLogout;


    // Componentes de UI (Textos e Imagem)
    private ImageView ivProfilePhoto;
    private TextView tvUsername;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvEmail;
    private TextView tvMemberSince;

    // Layouts (Clicáveis)
    private LinearLayout layoutPrimeiroNome;
    private LinearLayout layoutUltimoNome;
    private LinearLayout layoutEmail;
    private LinearLayout layoutMembroDesde;
    private LinearLayout layoutFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // 1. Inicializar as Views (fazer o bind com o XML)
        inicializarViews();

        // 2. Configurar os Listeners (Cliques)
        configurarListeners();

        // 3. Carregar dados do utilizador
        carregarDadosUtilizador();
    }

    private void inicializarViews() {
        // Botões
        btnVoltar = findViewById(R.id.btnVoltar);
        btnLogout = findViewById(R.id.btnLogout);

        // Imagem e Header
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvUsername = findViewById(R.id.tvUsername);

        // Text Views de Dados
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        tvMemberSince = findViewById(R.id.tvMemberSince);

        // Layouts (Containers)
        layoutPrimeiroNome = findViewById(R.id.layoutPrimeiroNome);
        layoutUltimoNome = findViewById(R.id.layoutUltimoNome);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutMembroDesde = findViewById(R.id.layoutMembroDesde);
        layoutFavoritos = findViewById(R.id.layoutFavoritos);
    }

    private void configurarListeners() {
        // Botão Voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a activity atual
            }
        });


        // Editar Primeiro Nome
        layoutPrimeiroNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Editar primeiro nome", Toast.LENGTH_SHORT).show();
            }
        });

        // Editar Último Nome
        layoutUltimoNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Editar último nome", Toast.LENGTH_SHORT).show();
            }
        });

        // Editar Email
        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Editar email", Toast.LENGTH_SHORT).show();
            }
        });

        // Membro Desde (Geralmente informativo, mas pode ter ação)
        layoutMembroDesde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Data de adesão", Toast.LENGTH_SHORT).show();
            }
        });

        // Ver Favoritos
        layoutFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "A abrir favoritos...", Toast.LENGTH_SHORT).show();
            }
        });

        // Botão Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aqui deve limpar a sessão e voltar ao login
                SingletonLusitania.getInstance(PerfilActivity.this).logout(PerfilActivity.this);
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Fecha a activity atual
                Toast.makeText(PerfilActivity.this, "Logout efetuado com sucesso", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void carregarDadosUtilizador() {
        // Dados temporários - substituir por chamadas à API/SharedPreferences/Room
        tvUsername.setText("AdminUser");
        tvFirstName.setText("Admin");
        tvLastName.setText("Lusitania");
        tvEmail.setText("00000000@my.ipleiria.pt");
        tvMemberSince.setText("Janeiro 2024");
    }

}