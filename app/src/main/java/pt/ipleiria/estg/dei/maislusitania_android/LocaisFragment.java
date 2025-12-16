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

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.LocalAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentLocaisBinding;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.LocaisFavDBHelper;

public class LocaisFragment extends Fragment implements LocaisListener {

    private FragmentLocaisBinding binding;
    private LocalAdapter adapter;
    private ArrayList<Local> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLocaisBinding.inflate(inflater, container, false);

        items = new ArrayList<>();

        binding.tilPesquisa.setEndIconOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        setupRecyclerView();

        SingletonLusitania.getInstance(requireContext()).setLocaisListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllLocaisAPI(getContext());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewLocais;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new LocalAdapter(items, new LocalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Local item) {
                Toast.makeText(getContext(), "Clicou em: " + item.getNome(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavoriteClick(Local item, int position) {
                LocaisFavDBHelper dbHelper = new LocaisFavDBHelper(getContext());

                if (item.isFavorite()) {
                    dbHelper.removerFavorito(item.getId());
                    item.setFavorite(false);
                    Toast.makeText(getContext(), "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.adicionarFavorito(item);
                    item.setFavorite(true);
                    Toast.makeText(getContext(), "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyItemChanged(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLocaisLoaded(ArrayList<Local> listaLocais) {
        items.clear();
        items.addAll(listaLocais);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
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
