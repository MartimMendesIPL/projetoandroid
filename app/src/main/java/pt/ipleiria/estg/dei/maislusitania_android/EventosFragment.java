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
import pt.ipleiria.estg.dei.maislusitania_android.utils.UtilParser; // Import UtilParser

public class EventosFragment extends Fragment implements EventoListener {

    private FragmentEventosBinding binding;
    private EventoAdapter eventoAdapter;
    private ArrayList<Evento> items;

    // Variáveis para a Pesquisa Dinâmica
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventosBinding.inflate(inflater, container, false);
        items = new ArrayList<>();

        //Configurar Listeners
        setupSearchListeners();

        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        //Configurar a RecyclerView
        setupRecyclerView();

        // Iniciar os dados
        SingletonLusitania.getInstance(requireContext()).setEventoListener(this);
        loadEventos();

        return binding.getRoot();
    }

    private void loadEventos() {
        if (!UtilParser.isConnectionInternet(getContext())) {
            showNoInternetWarning();
        } else {
            binding.recyclerViewEventos.setVisibility(View.VISIBLE);
            binding.includeNoInternet.getRoot().setVisibility(View.GONE);
            SingletonLusitania.getInstance(requireContext()).getAllEventosAPI(getContext());
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
                    if (!UtilParser.isConnectionInternet(getContext())) {
                        showNoInternetWarning();
                        return;
                    }

                    binding.recyclerViewEventos.setVisibility(View.VISIBLE);
                    binding.includeNoInternet.getRoot().setVisibility(View.GONE);

                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getAllEventosAPI(getContext());
                    } else {
                        SingletonLusitania.getInstance(requireContext()).searchEventoAPI(getContext(), query);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewEventos;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        eventoAdapter = new EventoAdapter(getContext(), items, position -> {
            Evento item = items.get(position);

            DetalhesEventoFragment fragment = new DetalhesEventoFragment();
            Bundle args = new Bundle();
            args.putInt("evento_id", item.getId());
            fragment.setArguments(args);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(eventoAdapter);
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
    public void onEventosLoaded(ArrayList<Evento> listaEventos) {
        if (binding == null) return; // Avoid crash if view is destroyed
        items.clear();
        items.addAll(listaEventos);
        if (eventoAdapter != null) {
            eventoAdapter.notifyDataSetChanged();
        }
        // Garante que o layout correto está visível após o carregamento
        binding.recyclerViewEventos.setVisibility(View.VISIBLE);
        binding.includeNoInternet.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void onEventoLoaded(Evento evento) {
        // Metodo usado para carregar um único evento (não usado na lista)
    }

    @Override
    public void onEventoError(String message) {
        if (binding == null) return;
        // Se ocorrer um erro, verificamos se é por falta de internet
        if (!UtilParser.isConnectionInternet(getContext())) {
            showNoInternetWarning();
        }
        // Mostra o erro da API (que pode ser "Sem ligação à internet" ou outro erro do Volley)
        Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
    }

    private void showNoInternetWarning() {
        if (binding == null) return;
        binding.recyclerViewEventos.setVisibility(View.GONE);
        binding.includeNoInternet.getRoot().setVisibility(View.VISIBLE);
    }
}
