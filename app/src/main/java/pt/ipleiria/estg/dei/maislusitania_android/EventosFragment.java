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

import pt.ipleiria.estg.dei.maislusitania_android.adapters.EventoAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentEventosBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.EventoListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Evento;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser;

/**
 * Fragment que exibe a lista de eventos com funcionalidade de pesquisa dinâmica
 */
public class EventosFragment extends Fragment implements EventoListener {

    // Binding para acesso aos elementos da UI
    private FragmentEventosBinding binding;

    // Adaptador para a RecyclerView de eventos
    private EventoAdapter eventoAdapter;

    // Lista de eventos
    private ArrayList<Evento> items;

    // Handler e Runnable para atrasar a pesquisa e evitar chamadas excessivas à API
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    /**
     * Cria a view do fragment e inicializa componentes
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventosBinding.inflate(inflater, container, false);
        items = new ArrayList<>();

        // Configura o listener da pesquisa dinâmica
        setupSearchListeners();

        // Define ação do ícone de fim da barra de pesquisa (navega para Perfil)
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        // Configura a RecyclerView
        setupRecyclerView();

        // Registra este fragment como listener e carrega os eventos
        SingletonLusitania.getInstance(requireContext()).setEventoListener(this);
        loadEventos();

        return binding.getRoot();
    }

    /**
     * Carrega eventos verificando se há ligação à internet
     */
    private void loadEventos() {
        if (!UtilParser.isConnectionInternet(getContext())) {
            // Mostra aviso de falta de internet
            showNoInternetWarning();
        } else {
            // Oculta aviso e carrega eventos da API
            binding.recyclerViewEventos.setVisibility(View.VISIBLE);
            binding.includeNoInternet.getRoot().setVisibility(View.GONE);
            SingletonLusitania.getInstance(requireContext()).getAllEventosAPI(getContext());
        }
    }

    /**
     * Configura a pesquisa dinâmica com delay de 500ms para evitar chamadas excessivas
     */
    private void setupSearchListeners() {
        binding.tilPesquisa.getEditText().addTextChangedListener(new TextWatcher() {
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
                    // Verifica ligação à internet
                    if (!UtilParser.isConnectionInternet(getContext())) {
                        showNoInternetWarning();
                        return;
                    }

                    // Mostra a RecyclerView e oculta aviso de internet
                    binding.recyclerViewEventos.setVisibility(View.VISIBLE);
                    binding.includeNoInternet.getRoot().setVisibility(View.GONE);

                    // Se a pesquisa está vazia, carrega todos os eventos
                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getAllEventosAPI(getContext());
                    } else {
                        // Caso contrário, faz a pesquisa
                        SingletonLusitania.getInstance(requireContext()).searchEventoAPI(getContext(), query);
                    }
                };
                // Executa a pesquisa após 500ms de inatividade
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    /**
     * Configura a RecyclerView com o adaptador e o listener de cliques
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewEventos;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cria o adaptador com callback para quando um item é clicado
        eventoAdapter = new EventoAdapter(getContext(), items, position -> {
            Evento item = items.get(position);

            // Cria o fragment de detalhes
            DetalhesEventoFragment fragment = new DetalhesEventoFragment();
            Bundle args = new Bundle();
            args.putInt("evento_id", item.getId());
            fragment.setArguments(args);

            // Navega para o fragment de detalhes
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

        recyclerView.setAdapter(eventoAdapter);
    }

    /**
     * Limpa recursos quando a view é destruída
     */
    @Override
    public void onDestroyView() {
        // Remove callbacks do handler de pesquisa
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroyView();
        binding = null;
    }

    /**
     * Callback quando múltiplos eventos são carregados com sucesso
     */
    @Override
    public void onEventosLoaded(ArrayList<Evento> listaEventos) {
        if (binding == null) return; // Evita crash se a view foi destruída

        // Atualiza a lista com novos eventos
        items.clear();
        items.addAll(listaEventos);
        if (eventoAdapter != null) {
            eventoAdapter.notifyDataSetChanged();
        }

        // Garante visibilidade correta após carregamento
        binding.recyclerViewEventos.setVisibility(View.VISIBLE);
        binding.includeNoInternet.getRoot().setVisibility(View.GONE);
    }

    /**
     * Callback quando um único evento é carregado (não utilizado aqui)
     */
    @Override
    public void onEventoLoaded(Evento evento) {
        // Método não utilizado neste fragment
    }

    /**
     * Callback quando ocorre erro ao carregar eventos
     */
    @Override
    public void onEventoError(String message) {
        if (binding == null) return;

        // Verifica se o erro é por falta de internet
        if (!UtilParser.isConnectionInternet(getContext())) {
            showNoInternetWarning();
        }

        // Mostra mensagem de erro ao utilizador
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Mostra o aviso de falta de ligação à internet
     */
    private void showNoInternetWarning() {
        if (binding == null) return;
        binding.recyclerViewEventos.setVisibility(View.GONE);
        binding.includeNoInternet.getRoot().setVisibility(View.VISIBLE);
    }
}
