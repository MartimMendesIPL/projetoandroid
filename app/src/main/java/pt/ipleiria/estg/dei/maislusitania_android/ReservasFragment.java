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

import pt.ipleiria.estg.dei.maislusitania_android.adapters.ReservaAdapter;
import pt.ipleiria.estg.dei.maislusitania_android.databinding.FragmentReservasBinding;
import pt.ipleiria.estg.dei.maislusitania_android.listeners.ReservaListener;
import pt.ipleiria.estg.dei.maislusitania_android.models.Reserva;
import pt.ipleiria.estg.dei.maislusitania_android.models.SingletonLusitania;

/**
 * Fragment que exibe a lista de reservas/bilhetes do utilizador
 * Inclui funcionalidade de pesquisa dinâmica e navegação para detalhes
 */
public class ReservasFragment extends Fragment implements ReservaListener {

    // Binding para acesso aos elementos da UI
    private FragmentReservasBinding binding;

    // Adaptador para a RecyclerView de reservas
    private ReservaAdapter reservaAdapter;

    // Lista de reservas carregadas da API
    private ArrayList<Reserva> reservas;

    // Handler e Runnable para atrasar a pesquisa e evitar chamadas excessivas à API
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    /**
     * Inicializa a view do fragment e configura os componentes
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReservasBinding.inflate(inflater, container, false);
        reservas = new ArrayList<>();

        // Define ação do ícone de fim da barra de pesquisa (navega para Perfil)
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        // Configura a RecyclerView com o adaptador
        setupRecyclerView();

        // Configura a pesquisa dinâmica
        setupSearchListeners();

        // Registra este fragment como listener de eventos de reservas
        SingletonLusitania.getInstance(requireContext()).setReservaListener(this);

        // Carrega todas as reservas da API
        SingletonLusitania.getInstance(requireContext()).getAllReservasAPI(requireContext());

        return binding.getRoot();
    }

    /**
     * Configura a RecyclerView com o adaptador e listeners de clique
     */
    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewReservas;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Cria o adaptador com listener para cliques em reservas
        reservaAdapter = new ReservaAdapter(requireContext(), reservas, reserva -> {
            // Cria o fragment de visualização de bilhete
            ViewBilhetesFragment fragment = new ViewBilhetesFragment();
            Bundle args = new Bundle();

            // Passa o ID da reserva para o fragment de detalhes
            args.putInt("ID_RESERVA", reserva.getId());
            fragment.setArguments(args);

            // Navega para o fragment de detalhes
            getParentFragmentManager().beginTransaction()
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

        recyclerView.setAdapter(reservaAdapter);
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
                    String query = s.toString().trim();

                    // Carrega todas as reservas ou faz pesquisa conforme necessário
                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getAllReservasAPI(getContext());
                    } else {
                        SingletonLusitania.getInstance(requireContext()).searchReservaAPI(getContext(), query);
                    }
                };
                // Executa a pesquisa após 500ms de inatividade
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    /**
     * Limpa recursos quando a view é destruída
     */
    @Override
    public void onDestroyView() {
        // Remove callback pendente da pesquisa
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        super.onDestroyView();
        binding = null;
    }

    /**
     * Callback quando a lista de reservas é carregada com sucesso
     */
    @Override
    public void onReservasLoaded(ArrayList<Reserva> listaReservas) {
        // Limpa a lista anterior e adiciona as novas reservas
        reservas.clear();
        reservas.addAll(listaReservas);

        // Notifica o adaptador sobre as alterações
        if (reservaAdapter != null) {
            reservaAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Callback quando ocorre erro ao carregar a lista de reservas
     */
    @Override
    public void onReservasError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback quando uma reserva é criada com sucesso
     */
    @Override
    public void onReservaCreated(Reserva reserva) {
        // Recarrega a lista de reservas da API
        SingletonLusitania.getInstance(requireContext()).getAllReservasAPI(requireContext());
        Toast.makeText(requireContext(), "Reserva criada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback quando uma reserva é carregada individualmente
     * Não é utilizado neste fragment
     */
    @Override
    public void onReservaLoaded(Reserva reserva) {
        // Implementação vazia (não é usado neste fragmento)
    }

    /**
     * Callback quando ocorre erro ao carregar uma reserva individual
     */
    @Override
    public void onReservaError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
        }
    }
}
