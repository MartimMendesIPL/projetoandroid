package pt.ipleiria.estg.dei.maislusitania_android;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Carregar o fragmento Mapa por padrão (tela inicial)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MapaFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_mapa);
        }

        // Configurar listener para navegação
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.navigation_bilhetes) {
                    selectedFragment = new BilhetesFragment();
                } else if (item.getItemId() == R.id.navigation_mapa) {
                    selectedFragment = new MapaFragment();
                } else if (item.getItemId() == R.id.navigation_eventos) {
                    selectedFragment = new EventosFragment();
                } else if (item.getItemId() == R.id.navigation_noticias) {
                    selectedFragment = new NoticiasFragment();
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
}