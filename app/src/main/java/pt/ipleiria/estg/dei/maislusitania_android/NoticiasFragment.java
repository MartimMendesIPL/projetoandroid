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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.maislusitania_android.adapters.NoticiaAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentNoticiasBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.NoticiaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Noticia;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

public class NoticiasFragment extends Fragment implements NoticiaListener {

    private FragmentNoticiasBinding binding;
    private NoticiaAdapter adapter;
    private ArrayList<Noticia> items;
    // Variáveis para a Pesquisa Dinâmica
    private Handler searchHandler = new Handler(Looper.getMainLooper());
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

        // Configurar o listener e pedir os dados à API
        SingletonLusitania.getInstance(requireContext()).setNoticiaListener(this);
        SingletonLusitania.getInstance(requireContext()).getNoticiasAPI(getContext());

        return binding.getRoot();
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
                // Se o utilizador continuar a escrever, removemos a pesquisa feita anteriormente
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    String query = s.toString().trim();

                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getNoticiasAPI(getContext());
                    } else {
                        SingletonLusitania.getInstance(requireContext()).searchNoticiaAPI(getContext(), query);
                    }
                };
                // Aguarda 500ms após a última tecla antes de pesquisar
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewNoticias;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NoticiaAdapter(getContext(), items, new NoticiaAdapter.OnNoticiaListener() {
            @Override
            public void onNoticiaClick(int position) {
                Noticia item = items.get(position);
                DetalhesNoticiaFragment fragment = new DetalhesNoticiaFragment();

                Bundle args = new Bundle();
                args.putInt("noticia_id", item.getId());
                fragment.setArguments(args);

                // Navegar para o fragmento
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
                //Intent intent = new Intent(getContext(), DetalhesNoticiaFragment.class);
                //intent.putExtra("noticia_id", item.getId());
                //startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        // Limpar callbacks de pesquisa pendentes
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroyView();
        binding = null;
    }

    // --- Implementação correta da interface NoticiaListener ---

    @Override
    public void onNoticiasLoaded(ArrayList<Noticia> listaNoticias) {
        // Atualizar a lista local e o adapter
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
        // Corrigido o nome do metodo (era onErrorNoticias)
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }
}
