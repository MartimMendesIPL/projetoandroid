package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;

public class LocaisFragment extends Fragment implements LocaisListener, LocalAdapter.OnItemClickListener {

    private FragmentLocaisBinding binding;
    private LocalAdapter adapter;

    // Handler for delayed search
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocaisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupClickListeners();
        setupSearchListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(this);
        loadLocais();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(null);
    }

    private void loadLocais() {
        if (getContext() == null) return;
        if (!UtilParser.isConnectionInternet(requireContext())) {
            showNoInternetWarning(true);
        } else {
            showNoInternetWarning(false);
            String query = binding.etPesquisa.getText().toString().trim();
            if (query.isEmpty()) {
                SingletonLusitania.getInstance(requireContext()).getAllLocaisAPI(requireContext());
            } else {
                SingletonLusitania.getInstance(requireContext()).searchLocalAPI(requireContext(), query);
            }
        }
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

    private void setupSearchListener() {
        binding.etPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    if (isAdded()) {
                        loadLocais();
                    }
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }


    @Override
    public void onItemClick(Local item) {
        if (isAdded()) {
            Fragment fragment = DetalhesLocalFragment.newInstance(item.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onFavoriteClick(Local item, int position) {
        if (isAdded() && !UtilParser.isConnectionInternet(requireContext())) {
            Toast.makeText(getContext(), "Sem ligação à internet. Não é possível alterar favoritos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isAdded()) {
            SingletonLusitania.getInstance(requireContext()).toggleLocalFavoritoAPI(requireContext(), item);
        }
    }

    @Override
    public void onLocaisLoaded(ArrayList<Local> listaLocais) {
        if (adapter != null && isAdded()) {
            showNoInternetWarning(false);
            adapter.updateList(listaLocais);
        }
    }

    @Override
    public void onLocaisError(String message) {
        if (isAdded()) {
            if (!UtilParser.isConnectionInternet(requireContext())) {
                showNoInternetWarning(true);
            }
            Toast.makeText(getContext(), "Erro ao carregar locais: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showNoInternetWarning(boolean show) {
        if (binding != null && isAdded()) {
            binding.recyclerViewLocais.setVisibility(show ? View.GONE : View.VISIBLE);
            binding.includeNoInternet.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
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
        // Clean up handler on view destruction
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        binding = null;
    }
}
