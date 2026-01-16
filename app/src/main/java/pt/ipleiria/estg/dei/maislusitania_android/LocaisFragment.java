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

/**
 * Fragment que exibe a lista de locais com funcionalidade de pesquisa dinâmica e favoritos
 */
public class LocaisFragment extends Fragment implements LocaisListener, LocalAdapter.OnItemClickListener {

    // Binding para acesso aos elementos da UI
    private FragmentLocaisBinding binding;

    // Adaptador para a RecyclerView de locais
    private LocalAdapter adapter;

    // Handler e Runnable para atrasar a pesquisa e evitar chamadas excessivas à API
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    /**
     * Inicializa o binding da view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLocaisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Configura os elementos da UI
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupClickListeners();
        setupSearchListener();
    }

    /**
     * Registra listeners e carrega locais quando o fragment fica visível
     */
    @Override
    public void onResume() {
        super.onResume();
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(this);
        loadLocais();
    }

    /**
     * Remove listeners e limpa callbacks quando o fragment fica oculto
     */
    @Override
    public void onPause() {
        super.onPause();
        // Remove callback pendente da pesquisa
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        // Desregistra listener
        SingletonLusitania.getInstance(requireContext()).setLocaisListener(null);
    }

    /**
     * Carrega locais verificando ligação à internet
     */
    private void loadLocais() {
        // Verifica se o fragment ainda está ligado
        if (getContext() == null) return;

        // Verifica se há ligação à internet
        if (!UtilParser.isConnectionInternet(requireContext())) {
            showNoInternetWarning(true);
        } else {
            // Oculta aviso de internet
            showNoInternetWarning(false);

            // Obtém o texto de pesquisa
            String query = binding.etPesquisa.getText().toString().trim();

            // Carrega todos os locais ou faz pesquisa conforme necessário
            if (query.isEmpty()) {
                SingletonLusitania.getInstance(requireContext()).getAllLocaisAPI(requireContext());
            } else {
                SingletonLusitania.getInstance(requireContext()).searchLocalAPI(requireContext(), query);
            }
        }
    }

    /**
     * Configura a RecyclerView com o adaptador e listeners
     */
    private void setupRecyclerView() {
        adapter = new LocalAdapter(new ArrayList<>(), this);
        binding.recyclerViewLocais.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewLocais.setAdapter(adapter);
    }

    /**
     * Configura o listener do ícone de fim da barra de pesquisa (navega para Perfil)
     */
    private void setupClickListeners() {
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            if (SingletonLusitania.getInstance(requireContext()).isGuestMode(requireContext())) {
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(requireContext(), "Faça login para aceder ao perfil", Toast.LENGTH_SHORT).show();
            }else {
                Intent intent = new Intent(requireContext(), PerfilActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Configura a pesquisa dinâmica com delay de 500ms para evitar chamadas excessivas
     */
    private void setupSearchListener() {
        binding.etPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Remove a pesquisa anterior se ainda estiver pendente
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Define a nova pesquisa com delay
                searchRunnable = () -> {
                    // Carrega os locais (com ou sem filtro)
                    if (isAdded()) {
                        loadLocais();
                    }
                };
                // Executa a pesquisa após 500ms de inatividade
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    /**
     * Callback quando um local é clicado na lista
     */
    @Override
    public void onItemClick(Local item) {
        if (isAdded()) {
            // Cria o fragment de detalhes com o ID do local
            Fragment fragment = DetalhesLocalFragment.newInstance(item.getId());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * Callback quando o ícone de favorito é clicado
     */
    @Override
    public void onFavoriteClick(Local item, int position) {
        // Verifica ligação à internet antes de alterar favorito
        if (isAdded() && !UtilParser.isConnectionInternet(requireContext())) {
            Toast.makeText(getContext(), "Sem ligação à internet. Não é possível alterar favoritos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Envia pedido à API para alternar favorito
        if (isAdded()) {
            SingletonLusitania.getInstance(requireContext()).toggleLocalFavoritoAPI(requireContext(), item);
        }
    }

    /**
     * Callback quando a lista de locais é carregada com sucesso
     */
    @Override
    public void onLocaisLoaded(ArrayList<Local> listaLocais) {
        if (adapter != null && isAdded()) {
            // Oculta aviso de internet e atualiza a lista
            showNoInternetWarning(false);
            adapter.updateList(listaLocais);
        }
    }

    /**
     * Callback quando ocorre erro ao carregar locais
     */
    @Override
    public void onLocaisError(String message) {
        if (isAdded()) {
            // Mostra aviso de internet se necessário
            if (!UtilParser.isConnectionInternet(requireContext())) {
                showNoInternetWarning(true);
            }
            // Mostra mensagem de erro ao utilizador
            Toast.makeText(getContext(), "Erro ao carregar locais: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Mostra ou oculta o aviso de falta de ligação à internet
     */
    private void showNoInternetWarning(boolean show) {
        if (binding != null && isAdded()) {
            binding.recyclerViewLocais.setVisibility(show ? View.GONE : View.VISIBLE);
            binding.includeNoInternet.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Callback não utilizado neste fragment
     */
    @Override
    public void onLocalLoaded(Local local) {
        // Método não utilizado neste fragment
    }

    /**
     * Callback não utilizado neste fragment
     */
    @Override
    public void onLocalError(String message) {
        // Método não utilizado neste fragment
    }

    /**
     * Limpa recursos quando a view é destruída
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove callbacks do handler de pesquisa
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        binding = null;
    }
}
