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
import pt.ipleiria.estg.dei.maislusitania_android.fragments.DetalhesLocalFragment;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;

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

        // Define este fragmento como o listener atual e carrega os locais
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
                // Passamos o ID e a Avaliação Média (que sabemos que está correta na lista)
                Fragment fragment = DetalhesLocalFragment.newInstance(item.getId(), item.getAvaliacaoMedia());

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }


            @Override
            public void onFavoriteClick(Local item, int position) {
                SingletonLusitania.getInstance(requireContext()).toggleFavoritoAPI(requireContext(), item);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onLocaisLoaded(ArrayList<Local> listaLocais) {
        if (listaLocais != null) {
            items.clear();
            items.addAll(listaLocais);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLocaisError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro ao carregar locais: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocalLoaded(Local local) {
        // Não utilizado aqui
    }

    @Override
    public void onLocalError(String message) {
        // Não utilizado aqui
    }
}
