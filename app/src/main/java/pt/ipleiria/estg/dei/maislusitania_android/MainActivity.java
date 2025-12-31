package pt.ipleiria.estg.dei.maislusitania_android;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        FloatingActionButton fabMapa = findViewById(R.id.fab_mapa);

        //Botao placeholder para criar espaço no menu
        bottomNavigationView.getMenu().findItem(R.id.navigation_placeholder).setEnabled(false);

        // Desmarcar itens do bottom navigation
        deselectNavMenu();

        // Carregar o fragmento Mapa por padrão (tela inicial)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapaFragment())
                    .commit();
        }

        // Listener do FAB do Mapa
        fabMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectNavMenu();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MapaFragment())
                        .commit();
            }
        });

        // Listener do BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //Ligar menu
                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);

                //Este item é preciso para que os botoes apareçam como deve ser
                if (item.getItemId() == R.id.navigation_placeholder){
                    return false;
                }

                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.navigation_bilhetes) {
                    selectedFragment = new ReservasFragment();
                } else if (item.getItemId() == R.id.navigation_eventos) {
                    selectedFragment = new EventosFragment();
                } else if (item.getItemId() == R.id.navigation_noticias) {
                    selectedFragment = new NoticiasFragment();
                } else if (item.getItemId() == R.id.navigation_locais) {
                    selectedFragment = new LocaisFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });
    }

    public void deselectNavMenu() {
        bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
    }
}
