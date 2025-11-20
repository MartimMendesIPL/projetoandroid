package pt.ipleiria.estg.dei.maislusitania_android;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private ImageButton btnVoltar;
    private ImageView ivProfilePhoto;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvTelefone;
    private LinearLayout layoutEmail;
    private LinearLayout layoutTelefone;
    private LinearLayout layoutFavoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Inicializar views
        btnVoltar = findViewById(R.id.btnVoltar);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvTelefone = findViewById(R.id.tvTelefone);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutTelefone = findViewById(R.id.layoutTelefone);
        layoutFavoritos = findViewById(R.id.layoutFavoritos);

        // Botão voltar
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // TODO: Carregar dados do utilizador
        carregarDadosUtilizador();

        // Configurar listeners
        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Editar email", Toast.LENGTH_SHORT).show();
            }
        });

        layoutTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Editar telefone", Toast.LENGTH_SHORT).show();
            }
        });

        layoutFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "Ver favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void carregarDadosUtilizador() {
        // Dados temporários - substituir por dados reais da API/BD
        tvUsername.setText("Username");
        tvEmail.setText("00000000@my.ipleiria.pt");
        tvTelefone.setText("+351 000 000 000");
    }
}