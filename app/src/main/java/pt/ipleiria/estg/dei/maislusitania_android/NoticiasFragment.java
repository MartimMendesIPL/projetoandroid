package pt.ipleiria.estg.dei.maislusitania_android;

import android.content.Intent;import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.NoticiaAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentNoticiasBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.NoticiaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser; // Import UtilParser

public class NoticiasFragment extends Fragment implements NoticiaListener {

    private FragmentNoticiasBinding binding;
    private NoticiaAdapter adapter;
    private ArrayList<Noticia> items;
    // Variáveis para a Pesquisa Dinâmica
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNoticiasBinding.inflate(inflater, container, false);
        items = new ArrayList<>();

        //Configurar Listeners
        setupSearchListeners();

        binding.tilPesquisa.setEndIconOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        setupRecyclerView();

        // Configurar o listener
        SingletonLusitania.getInstance(requireContext()).setNoticiaListener(this);
        loadNoticias(); // Chamar o método que verifica a internet

        return binding.getRoot();
    }

    private void loadNoticias() {
        if (getContext() == null) return;
        if (!UtilParser.isConnectionInternet(getContext())) {
            showNoInternetWarning(true);
        } else {
            showNoInternetWarning(false);
            String query = binding.etPesquisa.getText().toString().trim();
            if (query.isEmpty()) {
                SingletonLusitania.getInstance(requireContext()).getNoticiasAPI(getContext());
            } else {
                SingletonLusitania.getInstance(requireContext()).searchNoticiaAPI(getContext(), query);
            }
        }
    }

    /**
     * Configura a pesquisa dinâmica com delay para evitar chamadas excessivas à API.
     */
    private void setupSearchListeners() {
        binding.tilPesquisa.getEditText().addTextChangedListener(new TextWatcher() {
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
                        loadNoticias();
                    }
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewNoticias;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NoticiaAdapter(getContext(), items, position -> {
            Noticia item = items.get(position);
            DetalhesNoticiaFragment fragment = new DetalhesNoticiaFragment();

            Bundle args = new Bundle();
            args.putInt("noticia_id", item.getId());
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onNoticiasLoaded(ArrayList<Noticia> listaNoticias) {
        if (binding == null) return;
        showNoInternetWarning(false);
        items.clear();
        items.addAll(listaNoticias);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNoticiaLoaded(Noticia noticia) {
        // Este metodo é usado para carregar uma única notícia (detalhes),
    }

    @Override
    public void onNoticiaError(String message) {
        if (binding == null) return;
        if (getContext() != null && !UtilParser.isConnectionInternet(getContext())) {
            showNoInternetWarning(true);
        }
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }

    private void showNoInternetWarning(boolean show) {
        if (binding != null) {
            binding.recyclerViewNoticias.setVisibility(show ? View.GONE : View.VISIBLE);
            binding.includeNoInternet.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
