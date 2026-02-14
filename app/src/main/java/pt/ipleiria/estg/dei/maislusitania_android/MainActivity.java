package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;
import android.widget.Toast;

import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.utils.MqttHelper;
import pt.ipleiria.estg.dei.maislusitania_android.utils.NotificationHelper;
import android.view.Window;
import android.view.WindowManager;

/**
 * Activity principal da aplicação que gerencia navegação entre fragmentos
 * Inclui barra de navegação inferior, FAB do mapa e ligação MQTT
 */
public class MainActivity extends AppCompatActivity {

    // Referência para a barra de navegação inferior
    private BottomNavigationView bottomNavigationView;

    /**
     * Inicializa a activity, configura MQTT, FAB e listeners de navegação
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pergunta permissões de notificações para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        // Cria o canal de notificações para notificações push
        NotificationHelper.createNotificationChannel(this);

        // Obtém a instância do helper MQTT
        MqttHelper mqttHelper = MqttHelper.getInstance();

        // Define listener para restaurar subscrições após conexão MQTT
        mqttHelper.setConnectionListener(new MqttHelper.MqttConnectionListener() {
            /**
             * Chamado quando a conexão MQTT é estabelecida com sucesso
             */
            @Override
            public void onConnected() {
                // Restaura subscrições aos tópicos de favoritos
                // verificar se o utilizador está logado antes de resubscrever
                if (!SingletonLusitania.getInstance(MainActivity.this).isGuestMode(MainActivity.this)) {
                    SingletonLusitania.getInstance(MainActivity.this).resubscribeToFavoritos(MainActivity.this);
                }
            }

            /**
             * Chamado quando a conexão MQTT falha
             */
            @Override
            public void onConnectionFailed(String error) {
                android.util.Log.e("MainActivity", "Falha na conexão MQTT: " + error);
            }
        });

        // Conecta ao servidor MQTT
        mqttHelper.connect(this);

        // Obtém referências para elementos da UI
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        FloatingActionButton fabMapa = findViewById(R.id.fab_mapa);

        // Desativa o item placeholder (apenas para criar espaço visual no menu)
        bottomNavigationView.getMenu().findItem(R.id.navigation_placeholder).setEnabled(false);

        // Desseleciona todos os itens da barra de navegação inicialmente
        deselectNavMenu();

        // Carrega o fragmento Mapa como tela inicial
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapaFragment())
                    .commit();
        }

        // Configura o listener do botão FAB (flutuante) do Mapa
        fabMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deseleciona itens da barra de navegação
                deselectNavMenu();

                // Carrega o fragmento Mapa
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MapaFragment())
                        .commit();
            }
        });

        // Configura o listener da barra de navegação inferior
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                // Ativa seleção nos itens do menu
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

                // Ignora o item placeholder (apenas visual)
                if (item.getItemId() == R.id.navigation_placeholder) {
                    return false;
                }

                // Determina qual fragmento carregar baseado no item selecionado
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.navigation_bilhetes) {
                    if (verificarModoConvidado("Faça login para aceder aos bilhetes")) return false;
                    selectedFragment = new ReservasFragment();
                } else if (item.getItemId() == R.id.navigation_eventos) {
                    if (verificarModoConvidado("Faça login para aceder aos eventos")) return false;
                    selectedFragment = new EventosFragment();
                } else if (item.getItemId() == R.id.navigation_noticias) {
                    if (verificarModoConvidado("Faça login para aceder as noticias")) return false;
                    selectedFragment = new NoticiasFragment();
                } else if (item.getItemId() == R.id.navigation_locais) {
                    selectedFragment = new LocaisFragment();
                }

                // Carrega o fragmento selecionado
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.fade_in,
                                    R.anim.fade_out,
                                    R.anim.fade_in,
                                    R.anim.fade_out
                            )
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
    }

    /**
     * Desseleciona todos os itens da barra de navegação
     */
    public void deselectNavMenu() {
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
    }

    private boolean verificarModoConvidado(String mensagem) {
        if (SingletonLusitania.getInstance(this).isGuestMode(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
