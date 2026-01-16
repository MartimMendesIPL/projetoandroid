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
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;

public class FavoritoActivity extends AppCompatActivity implements FavoritoListener {
    // declaracao das variaveis
    private ActivityFavoritoBinding binding;
    private FavoritoAdapter adapter;
    private ArrayList<Favorito> items;
    // metodo onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // inflar o layout
        binding = ActivityFavoritoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // inicializar a lista de favoritos
        items = new ArrayList<>();
        // configurar o botao de voltar
        binding.btnVoltar.setOnClickListener(v -> finish());
        // configurar o RecyclerView
        setupRecyclerView();
        // configurar o listener e carregar os favoritos
        SingletonLusitania.getInstance(this).setFavoritoListener(this);
        SingletonLusitania.getInstance(this).getallFavoritosAPI(this);
    }
    // metodo para configurar o RecyclerView
    private void setupRecyclerView()
    {
        // configurar o layout manager
        RecyclerView recyclerView = binding.recyclerViewFavorito;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // configurar o adapter
        adapter = new FavoritoAdapter(items, new FavoritoAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(Favorito item)
            {
                // verificar conexao
                if (!UtilParser.isConnectionInternet(FavoritoActivity.this))
                {
                    Toast.makeText(FavoritoActivity.this, "Sem ligação à Internet.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(FavoritoActivity.this, "Clique detectado para: " + item.getLocalNome(), Toast.LENGTH_SHORT).show();
                // navegar para o fragmento de detalhes do local
                Fragment fragment = DetalhesLocalFragment.newInstance(item.getLocalId());
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            @Override
            // metodo para lidar com o clique no favorito
            public void onFavoriteClick(Favorito item, int position)
            {
                // alternar o estado do favorito
                SingletonLusitania.getInstance(FavoritoActivity.this).toggleFavoritoAPI(FavoritoActivity.this, item);
            }
        });
        recyclerView.setAdapter(adapter);
    }
    @Override
    // metodo chamado quando os favoritos sao carregados
    public void onFavoritosLoaded(ArrayList<Favorito> listafavoritos)
    {
        // atualizar a lista de favoritos
        items.clear();
        items.addAll(listafavoritos);
        adapter.notifyDataSetChanged();
        // se a lista estiver vazia, mostrar a mensagem de nenhum favorito
        if (items.isEmpty())
        {
            binding.recyclerViewFavorito.setVisibility(View.GONE);
            binding.layoutNoFavoritos.setVisibility(View.VISIBLE);
        }
        // senao, mostrar o RecyclerView
        else
        {
            binding.recyclerViewFavorito.setVisibility(View.VISIBLE);
            binding.layoutNoFavoritos.setVisibility(View.GONE);
        }
    }
    @Override
    // metodo chamado em caso de erro ao carregar os favoritos
    public void onFavoritosError(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    @Override
    // limpar o binding quando a activity for destruida
    protected void onDestroy()
    {
        super.onDestroy();
        binding = null;
    }
}
