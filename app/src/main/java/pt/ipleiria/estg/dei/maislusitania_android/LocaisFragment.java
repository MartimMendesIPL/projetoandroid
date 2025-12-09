package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.LocalAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentLocaisBinding;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.utils.SingletonLusitania;

public class LocaisFragment extends Fragment implements SingletonLusitania.LocaisListener {

    private FragmentLocaisBinding binding;
    private LocalAdapter adapter;
    private List<Local> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLocaisBinding.inflate(inflater, container, false);

        // Inicializar lista
        items = new ArrayList<>();

        // Listener para o ícone de perfil
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        // Configurar RecyclerView
        setupRecyclerView();

        // Registar listener e carregar dados da API
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(this);
        SingletonLusitania.getInstance(requireContext()).getLocaisAPI();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewLocais;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new LocalAdapter(items, new LocalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Local item) {
                Toast.makeText(getContext(), "Clicou em: " + item.getTitle(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavoriteClick(Local item, int position) {
                String msg = item.isFavorite() ?
                        "Adicionado aos favoritos" : "Removido dos favoritos";
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLocaisLoaded(JSONArray locais) {
        items.clear();

        try {

            for (int i = 0; i < locais.length(); i++) {
                JSONObject local = locais.getJSONObject(i);

                Local item = new Local(
                        local.optInt("id", 0),
                        local.optString("nome", "Sem nome"),
                        local.optString("morada", "Morada desconhecida"),
                        local.optString("distrito", "Distrito desconhecido"),
                        local.optString("descricao", "Sem descrição"),
                        local.optString("imagem", ""),
                        (float) local.optDouble("avaliacao_media", 0.0)
                );

                items.add(item);
            }

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erro ao processar locais", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onLocaisError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
