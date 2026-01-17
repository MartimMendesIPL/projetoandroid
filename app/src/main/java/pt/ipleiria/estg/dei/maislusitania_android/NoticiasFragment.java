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
    // Declaração das variáveis
    private FragmentNoticiasBinding binding;
    private NoticiaAdapter adapter;
    private ArrayList<Noticia> items;
    // Variáveis para a Pesquisa Dinâmica
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    @Nullable
    @Override
    // Inflar o layout do fragmento
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        binding = FragmentNoticiasBinding.inflate(inflater, container, false);
        items = new ArrayList<>();
        //Configurar Listeners
        setupSearchListeners();
        // Configurar o clique no ícone de perfil
        binding.tilPesquisa.setEndIconOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });
        // Configurar o RecyclerView
        setupRecyclerView();
        // Configurar o listener
        SingletonLusitania.getInstance(requireContext()).setNoticiaListener(this);
        // Carregar as notícias iniciais
        loadNoticias();
        return binding.getRoot();
    }
    // Carregar notícias da API
    private void loadNoticias()
    {
        // Verificar a conectividade antes de fazer a chamada à API
        if (getContext() == null) return;
        // Usar UtilParser para verificar a conexão com a internet
        if (!UtilParser.isConnectionInternet(getContext()))
        {
            showNoInternetWarning(true);
        }
        // Se houver conexão, carregar notícias ou pesquisar
        else {
            showNoInternetWarning(false);
            String query = binding.etPesquisa.getText().toString().trim();
            // Se a consulta estiver vazia, carregar todas as notícias
            if (query.isEmpty())
            {
                SingletonLusitania.getInstance(requireContext()).getNoticiasAPI(getContext());
            }
            // Caso contrário, pesquisar notícias com base na consulta
            else
            {
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
    // Configurar o RecyclerView
    private void setupRecyclerView()
    {
        // Configurar o layout manager
        RecyclerView recyclerView = binding.recyclerViewNoticias;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Configurar o adapter
        adapter = new NoticiaAdapter(getContext(), items, position ->
        {
            // Navegar para o fragmento de detalhes da notícia
            Noticia item = items.get(position);
            DetalhesNoticiaFragment fragment = new DetalhesNoticiaFragment();
            // Passar o ID da notícia como argumento
            Bundle args = new Bundle();
            args.putInt("noticia_id", item.getId());
            fragment.setArguments(args);
            // Realizar a transação do fragmento
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_up_fade_in,
                            R.anim.slide_down_fade_out,
                            R.anim.slide_up_fade_in,
                            R.anim.slide_down_fade_out
                    )
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);
    }
    @Override
    // Limpar o binding quando a view for destruída
    public void onDestroyView()
    {
        if (searchHandler != null && searchRunnable != null)
        {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroyView();
        binding = null;
    }
    @Override
    // Metodo chamado quando as noticias sao carregadas
    public void onNoticiasLoaded(ArrayList<Noticia> listaNoticias)
    {
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
    // Metodo chamado quando ocorre um erro ao carregar as noticias
    public void onNoticiaError(String message)
    {
        if (binding == null) return;
        // Verificar a conectividade
        if (getContext() != null && !UtilParser.isConnectionInternet(getContext()))
        {
            // mostra o aviso de sem internet
            showNoInternetWarning(true);
        }
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }
    // Mostrar ou esconder o aviso de sem internet
    private void showNoInternetWarning(boolean show)
    {
        if (binding != null)
        {
            binding.recyclerViewNoticias.setVisibility(show ? View.GONE : View.VISIBLE);
            binding.includeNoInternet.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
