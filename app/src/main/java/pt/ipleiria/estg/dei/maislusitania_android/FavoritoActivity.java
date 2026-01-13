package pt.ipleiria.estg.dei.maislusitania_android;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.FavoritoAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.ActivityFavoritoBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.FavoritoListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class FavoritoActivity extends AppCompatActivity implements FavoritoListener {

    private ActivityFavoritoBinding binding;
    private FavoritoAdapter adapter;
    private ArrayList<Favorito> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoritoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        items = new ArrayList<>();

        binding.btnVoltar.setOnClickListener(v -> finish());

        setupRecyclerView();

        SingletonLusitania.getInstance(this).setFavoritoListener(this);
        SingletonLusitania.getInstance(this).getallFavoritosAPI(this);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewFavorito;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavoritoAdapter(items, new FavoritoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Favorito item) {
                Toast.makeText(FavoritoActivity.this, "Clique detectado para: " + item.getLocalNome(), Toast.LENGTH_SHORT).show();

                Fragment fragment = DetalhesLocalFragment.newInstance(item.getLocalId());

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onFavoriteClick(Favorito item, int position) {
                SingletonLusitania.getInstance(FavoritoActivity.this).toggleFavoritoAPI(FavoritoActivity.this, item);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onFavoritosLoaded(ArrayList<Favorito> listafavoritos) {
        items.clear();
        items.addAll(listafavoritos);
        adapter.notifyDataSetChanged();

        if (items.isEmpty()) {
            binding.recyclerViewFavorito.setVisibility(View.GONE);
            binding.layoutNoFavoritos.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewFavorito.setVisibility(View.VISIBLE);
            binding.layoutNoFavoritos.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFavoritosError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
