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

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.LocalAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentLocaisBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.LocaisListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Local;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class LocaisFragment extends Fragment implements LocaisListener, LocalAdapter.OnItemClickListener {

    private FragmentLocaisBinding binding;
    private LocalAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocaisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // A configuração inicial da UI e do RecyclerView permanece aqui.
        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // A chamada para carregar os dados é movida para onResume.
        // Isto garante que os dados são atualizados sempre que o fragmento se torna ativo.
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllLocaisAPI(requireContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        // É uma boa prática remover o listener em onPause para evitar memory leaks
        // ou atualizações de UI quando o fragmento não está visível.
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(null);
    }

    private void setupRecyclerView() {
        adapter = new LocalAdapter(new ArrayList<>(), this);
        binding.recyclerViewLocais.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewLocais.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onItemClick(Local item) {
        Fragment fragment = DetalhesLocalFragment.newInstance(item.getId());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFavoriteClick(Local item, int position) {
        SingletonLusitania.getInstance(requireContext()).toggleLocalFavoritoAPI(requireContext(), item);
    }

    @Override
    public void onLocaisLoaded(ArrayList<Local> listaLocais) {
        if (adapter != null && isAdded()) { // isAdded() verifica se o fragmento ainda está ligado à sua atividade
            adapter.updateList(listaLocais);
        }
    }

    @Override
    public void onLocaisError(String message) {
        if (isAdded()) {
            Toast.makeText(getContext(), "Erro ao carregar locais: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocalLoaded(Local local) {
        // Not used in this fragment
    }

    @Override
    public void onLocalError(String message) {
        // Not used in this fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // A limpeza do binding continua aqui.
        binding = null;
    }
}
