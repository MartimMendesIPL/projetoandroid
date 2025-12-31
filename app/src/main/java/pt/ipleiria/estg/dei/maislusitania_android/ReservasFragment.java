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

public class ReservasFragment extends Fragment implements ReservaListener {

    private FragmentReservasBinding binding;
    private ReservaAdapter reservaAdapter;
    private ArrayList<Reserva> reservas;

    // Variáveis para a Pesquisa Dinâmica
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReservasBinding.inflate(inflater, container, false);
        reservas = new ArrayList<>();

        // Listener para o ícone de perfil (Barra de pesquisa/topo)
        binding.tilPesquisa.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PerfilActivity.class);
            startActivity(intent);
        });

        // Configurar a RecyclerView
        setupRecyclerView();
        setupSearchListeners();

        //TODO: Search de reservas

        // Configurar o Singleton e pedir os dados à API
        SingletonLusitania.getInstance(requireContext()).setReservaListener(this);
        SingletonLusitania.getInstance(requireContext()).getAllReservasAPI(requireContext());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewReservas;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reservaAdapter = new ReservaAdapter(requireContext(), reservas, new ReservaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Reserva reserva) {
                ViewBilhetesFragment fragment = new ViewBilhetesFragment();

                Bundle args = new Bundle();
                args.putInt("ID_RESERVA", reserva.getId());
                fragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        recyclerView.setAdapter(reservaAdapter);
    }

    private void setupSearchListeners() {
        binding.tilPesquisa.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Se o utilizador continuar a escrever, é apagada a pesquisa feita anteriormente
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    if (query.isEmpty()) {
                        SingletonLusitania.getInstance(requireContext()).getAllReservasAPI(getContext());
                    } else {
                        SingletonLusitania.getInstance(requireContext()).searchReservaAPI(getContext(), query);
                    }
                };
                // Aguarda 500ms após a última tecla antes de pesquisar
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onReservasLoaded(ArrayList<Reserva> listaReservas) {
        // Atualizar a lista local
        reservas.clear();
        reservas.addAll(listaReservas);

        // Notificar o adapter que os dados mudaram
        if (reservaAdapter != null) {
            reservaAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onReservasError(String message) {
        // Verificar se o fragmento ainda está anexado antes de mostrar o Toast (evita crashes)
        if (getContext() != null) {
            Toast.makeText(getContext(), "Erro: " + message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReservaLoaded(Reserva reserva) {
        // Este método não é usado na lista principal (usado apenas em detalhes/create)
    }
}