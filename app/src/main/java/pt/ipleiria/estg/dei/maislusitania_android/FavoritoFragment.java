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

import pt.ipleiria.estg.dei.maislusitania_android.adapters.FavoritoAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentFavoritoBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.FavoritoListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Favorito;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class FavoritoFragment extends Fragment implements FavoritoListener {

    private FragmentFavoritoBinding binding;
    private FavoritoAdapter adapter;
    private ArrayList<Favorito> items;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritoBinding.inflate(inflater, container, false);

        items = new ArrayList<>();

        binding.btnVoltar.setOnClickListener(v -> requireActivity().onBackPressed());

        setupRecyclerView();

        SingletonLusitania.getInstance(requireContext()).setFavoritoListener(this);
        SingletonLusitania.getInstance(requireContext()).getallFavoritosAPI(getContext());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewFavorito;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FavoritoAdapter(items, new FavoritoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Favorito item) {
                Toast.makeText(getContext(), "Clicou em: " + item.getLocalNome(), Toast.LENGTH_SHORT).show();
                // TODO: Implementar a navegação para os detalhes do local (AFONSO)
            }

            @Override
            public void onFavoriteClick(Favorito item, int position) {
                SingletonLusitania.getInstance(requireContext()).toggleFavoritoAPI(requireContext(), item);

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
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
